use chrono::Utc;
use jsonwebtoken::{DecodingKey, EncodingKey, Header, Validation, decode, encode};
use rocket::{
    Request, State,
    http::{CookieJar, Status},
    request::{FromRequest, Outcome},
    serde::{Deserialize, Serialize, json::Json},
};

use mysql::{
    Pool, params,
    prelude::{FromRow, Queryable},
};
use rocket::fairing::AdHoc;

#[derive(Serialize, Deserialize)]
#[serde(crate = "rocket::serde")]
struct ResultVO<'a, T> {
    code: &'a str,
    data: T,
}

#[derive(Serialize, Deserialize, Clone, FromRow, Debug)]
#[serde(crate = "rocket::serde")]
struct User {
    uuid: Option<String>,
    username: String,
    password: String,
    name: String,
    avatar: Option<String>,
    telephone: Option<String>,
    email: Option<String>,
    location: Option<String>,
}

#[derive(Serialize, Deserialize, Clone, FromRow)]
#[serde(crate = "rocket::serde")]
struct UserVO {
    username: String,
    name: String,
    avatar: Option<String>,
    telephone: Option<String>,
    email: Option<String>,
    location: Option<String>,
}

#[derive(Serialize, Deserialize, Clone, FromRow)]
#[serde(crate = "rocket::serde")]
struct LoginVO {
    username: String,
    password: String,
}

#[derive(Serialize, Deserialize, Clone)]
#[serde(crate = "rocket::serde")]
struct MessageVO<'a> {
    msg: &'a str,
}

#[derive(Serialize, Deserialize, Clone)]
#[serde(crate = "rocket::serde")]
struct LoginMessageVO<'a> {
    msg: &'a str,
    token: String,
}

#[derive(Serialize, Deserialize, Clone, Debug)]
#[serde(crate = "rocket::serde")]
struct UpdateUserVO<'a> {
    username: &'a str,
    name: Option<&'a str>,
    avatar: Option<&'a str>,
    telephone: Option<&'a str>,
    email: Option<&'a str>,
    location: Option<&'a str>,
}

const EXPIRE_TIME_SECS: usize = 24 * 60 * 60;

#[derive(Serialize, Deserialize, Debug)]
#[serde(crate = "rocket::serde")]
struct JwtPayload {
    uuid: Vec<u8>,
    exp: usize,
}

struct UserAuthentication;

#[rocket::async_trait]
impl<'r> FromRequest<'r> for UserAuthentication {
    type Error = ();

    async fn from_request(request: &'r Request<'_>) -> Outcome<Self, Self::Error> {
        match request.cookies().get("token").map(|c| c.value()) {
            Some(token) => {
                if let Ok(result) = decode::<JwtPayload>(
                    token,
                    &DecodingKey::from_secret("secret".as_ref()),
                    &Validation::default(),
                ) {
                    let uuid = result.claims.uuid;
                    let db = request.rocket().state::<Pool>().unwrap();

                    let mut conn = db
                        .get_conn()
                        .map_err(|_| rocket::http::Status::InternalServerError)
                        .unwrap();

                    if let Ok(Some(_)) = conn.exec_first::<Vec<u8>, _, _>(
                        r"SELECT uuid FROM users WHERE uuid = :uuid",
                        params! {
                            "uuid" => uuid
                        },
                    ) {
                        return Outcome::Success(UserAuthentication);
                    }
                }
                Outcome::Error((Status::Unauthorized, ()))
            }
            _ => Outcome::Error((Status::Unauthorized, ())),
        }
    }
}

#[get("/<username>")]
fn get_user_detail<'a>(
    _user_auth: UserAuthentication,
    pool: &'a State<Pool>,
    username: &'a str,
) -> Result<Json<ResultVO<'a, UserVO>>, rocket::http::Status> {
    let mut conn = pool
        .get_conn()
        .map_err(|_| rocket::http::Status::InternalServerError)?;

    let result: UserVO = conn.exec_first(
            "SELECT username, name, avatar, telephone, email, location FROM users WHERE username = (:username)",
            params! {
                "username" => username,
            },
        )
        .map_err(|_| rocket::http::Status::InternalServerError)?
        .ok_or(rocket::http::Status::NotFound)?;

    Ok(Json(ResultVO {
        code: "400",
        data: result,
    }))
}

#[post("/", data = "<user>")]
fn create_user(
    pool: &State<Pool>,
    user: Json<User>,
) -> Result<Json<ResultVO<MessageVO>>, rocket::http::Status> {
    let mut conn = pool
        .get_conn()
        .map_err(|_| rocket::http::Status::InternalServerError)?;

    if conn
        .exec_first::<Vec<u8>, _, _>(
            r"SELECT uuid FROM users where username = :username",
            params! {
                "username" => &user.username
            },
        )
        .is_ok()
    {
        return Ok(Json(ResultVO {
            code: "400",
            data: MessageVO {
                msg: "用户名已存在",
            },
        }));
    }

    conn.exec_drop(
        r"INSERT INTO users (username, password, name) VALUES (:username, :password, :name)",
        params! {
            "username" => &user.username,
            "password" => &user.password,
            "name" => &user.name
        },
    )
    .map_err(|_| rocket::http::Status::InternalServerError)?;

    Ok(Json(ResultVO {
        code: "000",
        data: MessageVO {
            msg: "用户创建成功",
        },
    }))
}

#[post("/login", data = "<login_vo>")]
fn login<'a>(
    pool: &'a State<Pool>,
    cookies: &'a CookieJar<'a>,
    login_vo: Json<LoginVO>,
) -> Result<Json<ResultVO<'a, LoginMessageVO<'a>>>, rocket::http::Status> {
    let mut conn = pool
        .get_conn()
        .map_err(|_| rocket::http::Status::InternalServerError)?;

    let uuid: Vec<u8> = conn
        .exec_first(
            r"SELECT uuid FROM users WHERE username = :username and password = :password",
            params! {
                "username" => &login_vo.username,
                "password" => &login_vo.password,
            },
        )
        .map_err(|_| rocket::http::Status::InternalServerError)?
        .ok_or(rocket::http::Status::NotFound)?;

    let now = Utc::now();

    let token = encode(
        &Header::default(),
        &JwtPayload {
            uuid,
            exp: now.timestamp() as usize + EXPIRE_TIME_SECS,
        },
        &EncodingKey::from_secret("secret".as_ref()),
    )
    .unwrap();

    cookies.add(("token", token.clone()));

    Ok(Json(ResultVO {
        code: "400",
        data: LoginMessageVO {
            msg: "登录成功",
            token,
        },
    }))
}

#[put("/", data = "<user>")]
fn update_user<'a>(
    _user_auth: UserAuthentication,
    pool: &'a State<Pool>,
    user: Json<UpdateUserVO<'a>>,
) -> Result<Json<ResultVO<'a, MessageVO<'a>>>, rocket::http::Status> {
    let mut conn = pool
        .get_conn()
        .map_err(|_| rocket::http::Status::InternalServerError)?;

    println!("{:?}", user);

    // If the user not exists, no error returned!!!
    conn.exec_drop(
        r"UPDATE users
SET 
    name = COALESCE(NULLIF(:name, ''), name),
    avatar = COALESCE(NULLIF(:avatar, ''), avatar),
    telephone = COALESCE(NULLIF(:telephone, ''), telephone),
    email = COALESCE(NULLIF(:email, ''), email),
    location = COALESCE(NULLIF(:location, ''), location)
WHERE username = :username;",
        params! {
            "name" => user.name.unwrap_or(""),
            "avatar" => user.avatar.unwrap_or(""),
            "telephone" => user.telephone.unwrap_or(""),
            "email" => user.email.unwrap_or(""),
            "location" => user.location.unwrap_or(""),
            "username" => user.username
        },
    )
    .map_err(|_| rocket::http::Status::InternalServerError)?;

    Ok(Json(ResultVO {
        code: "400",
        data: MessageVO {
            msg: "用户信息更新成功",
        },
    }))
}

pub fn stage() -> AdHoc {
    let url = "mysql://root:12345678@127.0.0.1:33060/Supercode";
    let pool = Pool::new(url).unwrap();
    AdHoc::on_ignite("", |rocket| async {
        rocket.manage(pool).mount(
            "/api/accounts",
            routes![get_user_detail, create_user, login, update_user],
        )
    })
}

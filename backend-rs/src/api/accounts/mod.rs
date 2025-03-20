use axum::extract::{Path, Request};
use axum::http::{Method, StatusCode};
use axum::middleware::Next;
use axum::response::Response;
use axum::routing::{post, put};
use axum::{Json, middleware};
use axum::{Router, extract::State, routing::get};
use chrono::Utc;
use jsonwebtoken::{DecodingKey, EncodingKey, Header, Validation, decode, encode};
use mysql::prelude::{FromRow, Queryable};
use mysql::{Pool, params};
use serde::{Deserialize, Serialize};

#[derive(Serialize, Deserialize)]
struct ResultVO<'a, T> {
    code: &'a str,
    data: T,
}

#[derive(Serialize, Deserialize, FromRow)]
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

#[derive(Serialize, Deserialize, FromRow)]
struct UserInfo {
    username: String,
    name: String,
    avatar: Option<String>,
    telephone: Option<String>,
    email: Option<String>,
    location: Option<String>,
}

#[derive(Deserialize, Serialize)]
struct MessageVO<'a> {
    msg: &'a str,
}

#[derive(Deserialize, Serialize)]
struct LoginVO {
    username: String,
    password: String,
}

#[derive(Deserialize, Serialize)]
struct LoginMessageVO<'a> {
    msg: &'a str,
    token: String,
}

#[derive(Deserialize, Serialize)]
struct JwtPayload {
    uuid: Vec<u8>,
    exp: usize,
}

#[derive(Serialize, Deserialize)]
struct UpdateUserVO {
    username: String,
    name: Option<String>,
    avatar: Option<String>,
    telephone: Option<String>,
    email: Option<String>,
    location: Option<String>,
}

const EXPIRE_TIME_SECS: usize = 24 * 60 * 60;

async fn auth(State(pool): State<Pool>, req: Request, next: Next) -> Result<Response, StatusCode> {
    if req.uri() == "/login" && req.method() == Method::POST {
        return Ok(next.run(req).await);
    }
    if req.uri() == "/" && req.method() == Method::POST {
        return Ok(next.run(req).await);
    }
    let headers = req.headers();
    if let Some(token) = headers.get("token") {
        if let Ok(result) = decode::<JwtPayload>(
            token.to_str().unwrap(),
            &DecodingKey::from_secret("secret".as_ref()),
            &Validation::default(),
        ) {
            let uuid = result.claims.uuid;
            let mut conn = pool.get_conn().unwrap();
            if let Ok(Some(_)) = conn.exec_first::<Vec<u8>, _, _>(
                r"SELECT uuid FROM users WHERE uuid = :uuid",
                params! {
                    "uuid" => uuid
                },
            ) {}
            return Ok(next.run(req).await);
        }
    }
    Err(StatusCode::UNAUTHORIZED)
}

async fn get_user_detail<'a>(
    State(pool): State<Pool>,
    Path(username): Path<String>,
) -> Json<ResultVO<'a, UserInfo>> {
    let mut conn = pool.get_conn().unwrap();

    let result: UserInfo = conn.exec_first(
        "SELECT username, name, avatar, telephone, email, location FROM users WHERE username = (:username)",
        params! {
            "username" => username,
        },
    ).unwrap().unwrap();

    Json(ResultVO {
        code: "400",
        data: result,
    })
}

async fn create_user<'a>(
    State(pool): State<Pool>,
    Json(user): Json<User>,
) -> Json<ResultVO<'a, MessageVO<'a>>> {
    let mut conn = pool.get_conn().unwrap();

    if let Ok(Some(_)) = conn.exec_first::<Vec<u8>, _, _>(
        r"SELECT uuid FROM users where username = :username",
        params! {
            "username" => &user.username
        },
    ) {
        return Json(ResultVO {
            code: "400",
            data: MessageVO {
                msg: "用户名已存在",
            },
        });
    }

    conn.exec_drop(
        r"INSERT INTO users (username, password, name) VALUES (:username, :password, :name)",
        params! {
            "username" => &user.username,
            "password" => &user.password,
            "name" => &user.name
        },
    )
    .unwrap();

    Json(ResultVO {
        code: "000",
        data: MessageVO {
            msg: "用户创建成功",
        },
    })
}

async fn login<'a>(
    State(pool): State<Pool>,
    Json(login_vo): Json<LoginVO>,
) -> Json<ResultVO<'a, LoginMessageVO<'a>>> {
    let mut conn = pool.get_conn().unwrap();

    let uuid: Vec<u8> = conn
        .exec_first(
            r"SELECT uuid FROM users WHERE username = :username and password = :password",
            params! {
                "username" => &login_vo.username,
                "password" => &login_vo.password,
            },
        )
        .unwrap()
        .unwrap();

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

    Json(ResultVO {
        code: "400",
        data: LoginMessageVO {
            msg: "登录成功",
            token,
        },
    })
}

async fn update_user<'a>(
    State(pool): State<Pool>,
    Json(user): Json<UpdateUserVO>,
) -> Json<ResultVO<'a, MessageVO<'a>>> {
    let mut conn = pool.get_conn().unwrap();

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
            "name" => user.name.unwrap_or("".into()),
            "avatar" => user.avatar.unwrap_or("".into()),
            "telephone" => user.telephone.unwrap_or("".into()),
            "email" => user.email.unwrap_or("".into()),
            "location" => user.location.unwrap_or("".into()),
            "username" => user.username
        },
    )
    .unwrap();

    Json(ResultVO {
        code: "400",
        data: MessageVO {
            msg: "用户信息更新成功",
        },
    })
}

pub fn stage() -> Router {
    let url = "mysql://root:12345678@127.0.0.1:33060/Supercode";
    let pool = Pool::new(url).unwrap();

    // TODO: CREATE TABLE IF NOT EXISTS

    Router::new()
        .route("/", post(create_user))
        .route("/{username}", get(get_user_detail))
        .route("/login", post(login))
        .route("/", put(update_user))
        .with_state(pool.clone())
        .route_layer(middleware::from_fn_with_state(pool, auth))
}

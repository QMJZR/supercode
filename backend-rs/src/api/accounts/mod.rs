use axum::extract::{Path, Request};
use axum::http::{Method, StatusCode};
use axum::middleware::Next;
use axum::response::{IntoResponse, Response};
use axum::routing::{post, put};
use axum::{Json, middleware};
use axum::{Router, extract::State, routing::get};
use axum_extra::extract::CookieJar;
use axum_extra::extract::cookie::Cookie;
use bcrypt::{DEFAULT_COST, hash, verify};
use chrono::Utc;
use jsonwebtoken::{DecodingKey, EncodingKey, Header, Validation, decode, encode};
use mysql::prelude::{FromRow, Queryable};
use mysql::{Pool, params};
use serde::{Deserialize, Serialize};
use utoipa::ToSchema;

#[derive(Serialize, Deserialize)]
struct ResultVO<'a, T> {
    code: &'a str,
    data: T,
}

#[derive(Serialize, Deserialize, FromRow, ToSchema)]
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

#[derive(Deserialize, Serialize, ToSchema)]
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

#[derive(Serialize, Deserialize, ToSchema)]
struct UpdateUserVO {
    username: String,
    name: Option<String>,
    avatar: Option<String>,
    telephone: Option<String>,
    email: Option<String>,
    location: Option<String>,
}

const EXPIRE_TIME_SECS: usize = 24 * 60 * 60;

async fn auth(
    State(pool): State<Pool>,
    jar: CookieJar,
    req: Request,
    next: Next,
) -> Result<Response, StatusCode> {
    if req.uri() == "/login" && req.method() == Method::POST {
        return Ok(next.run(req).await);
    }
    if req.uri() == "/" && req.method() == Method::POST {
        return Ok(next.run(req).await);
    }
    if let Some(token) = jar.get("token") {
        println!("{}", token.value());
        if let Ok(result) = decode::<JwtPayload>(
            token.value(),
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
            ) {
                return Ok(next.run(req).await);
            }
            return Ok(Json(ResultVO {
                code: "400",
                data: MessageVO { msg: "未登录" },
            })
            .into_response());
        }
    }
    Ok(Json(ResultVO {
        code: "400",
        data: MessageVO { msg: "未登录" },
    })
    .into_response())
}

#[derive(Deserialize, Serialize)]
#[serde(bound(deserialize = "'de: 'a"))]
#[serde(untagged)] // Removes enum variant names from serialization
enum GetUserDetailResponse<'a> {
    Success(UserInfo),
    Failure(MessageVO<'a>),
}

#[utoipa::path(
    get, 
    path = "/api/accounts/{username}", 
    params(
        ("username" = String, Path)
    ), 
    responses((status = 200)),
    description = "get user details 获取用户详细信息"
)]
async fn get_user_detail<'a>(
    State(pool): State<Pool>,
    Path(username): Path<String>,
) -> Json<ResultVO<'a, GetUserDetailResponse<'a>>> {
    let mut conn = pool.get_conn().unwrap();

    if let Ok(Some(result)) = conn.exec_first::<UserInfo,_,_>(
        "SELECT username, name, avatar, telephone, email, location FROM users WHERE username = (:username)",
        params! {
            "username" => username,
        },
    ) {
        return Json(ResultVO {
            code: "200",
            data: GetUserDetailResponse::Success(result),
        });
    }
    Json(ResultVO {
        code: "400",
        data: GetUserDetailResponse::Failure(MessageVO { msg: "123" }),
    })
}

#[utoipa::path(
    post, 
    path = "/api/accounts", 
    responses((status = 200)),
    description = "register 注册"
)]
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
            "password" => hash(&user.password, DEFAULT_COST).unwrap(),
            "name" => &user.name
        },
    )
    .unwrap();

    Json(ResultVO {
        code: "200",
        data: MessageVO {
            msg: "用户创建成功",
        },
    })
}

#[utoipa::path(
    post, 
    path = "/api/accounts/login", 
    responses((status = 200)),
    description = "login 登录"
)]
async fn login<'a>(
    State(pool): State<Pool>,
    jar: CookieJar,
    Json(login_vo): Json<LoginVO>,
) -> (CookieJar, Json<ResultVO<'a, LoginMessageVO<'a>>>) {
    let mut conn = pool.get_conn().unwrap();

    if let Ok(Some((password, uuid))) = conn.exec_first::<(String, Vec<u8>), _, _>(
        r"SELECT password, uuid FROM users WHERE username = :username",
        params! {
            "username" => &login_vo.username,
        },
    ) {
        let now = Utc::now();

        if !verify(login_vo.password, &password).unwrap() {
            return (
                jar,
                Json(ResultVO {
                    code: "400",
                    data: LoginMessageVO {
                        msg: "用户不存在/用户密码错误",
                        token: "".to_string(),
                    },
                }),
            );
        }

        let token = encode(
            &Header::default(),
            &JwtPayload {
                uuid,
                exp: now.timestamp() as usize + EXPIRE_TIME_SECS,
            },
            &EncodingKey::from_secret("secret".as_ref()),
        )
        .unwrap();

        (
            jar.add(Cookie::new("token", token.clone())),
            Json(ResultVO {
                code: "200",
                data: LoginMessageVO {
                    msg: "登录成功",
                    token,
                },
            }),
        )
    } else {
        (
            jar,
            Json(ResultVO {
                code: "400",
                data: LoginMessageVO {
                    msg: "用户不存在/用户密码错误",
                    token: "".to_string(),
                },
            }),
        )
    }
}

#[utoipa::path(
    put, 
    path = "/api/accounts", 
    responses((status = 200)), 
    description = "Update User Info 更新用户信息"
)]
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
        code: "200",
        data: MessageVO {
            msg: "用户信息更新成功",
        },
    })
}

pub fn stage() -> Router {
    let url = "mysql://root:12345678@127.0.0.1:33060/Supercode";
    let pool = Pool::new(url).unwrap();

    // CREATE TABLE IF NOT EXISTS
    pool.get_conn()
        .unwrap()
        .query_drop(
            r"CREATE TABLE IF NOT EXISTS  users  (
    uuid BINARY(16) NOT NULL PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    username VARCHAR(50) NOT NULL COMMENT '用户名，不允许为空',
    password VARCHAR(100) NOT NULL COMMENT '用户密码，仅参与插入操作',
    name VARCHAR(50) NOT NULL COMMENT '用户姓名，不允许为空',
    avatar VARCHAR(255) COMMENT '用户头像链接',
    telephone VARCHAR(11) COMMENT '用户手机号，格式需符合1开头的11位数字',
    email VARCHAR(100) COMMENT '用户邮箱，格式需符合邮箱规范',
    location VARCHAR(255) COMMENT '用户所在地'
) COMMENT='用户表';",
        )
        .unwrap();

    Router::new()
        .route("/", post(create_user))
        .route("/{username}", get(get_user_detail))
        .route("/login", post(login))
        .route("/", put(update_user))
        .with_state(pool.clone())
        .route_layer(middleware::from_fn_with_state(pool, auth))
}

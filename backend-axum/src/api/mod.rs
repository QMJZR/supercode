use axum::Router;

pub mod accounts;

pub fn stage() -> Router {
    Router::new().nest("/accounts", accounts::stage())
}


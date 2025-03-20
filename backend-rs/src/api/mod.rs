use axum::Router;

pub mod accounts;
pub mod worker;

pub fn stage() -> Router {
    Router::new()
        .nest("/accounts", accounts::stage())
        .nest("/worker", worker::stage())
}

use axum::Router;

mod api;

#[tokio::main]
async fn main() {
    let app = Router::new().nest("/api", api::stage());

    let listener = tokio::net::TcpListener::bind("127.0.0.1:8000")
        .await
        .unwrap();

    axum::serve(listener, app).await.unwrap();
}

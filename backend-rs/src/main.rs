use api::worker;
use axum::Router;
use utoipa::OpenApi;
use utoipa_swagger_ui::SwaggerUi;

mod api;

#[derive(OpenApi)]
#[openapi(paths(
    api::accounts::get_user_detail,
    api::accounts::create_user,
    api::accounts::login,
    api::accounts::update_user,
    api::worker::c_controller,
    api::worker::cpp_controller,
    api::worker::python3_controller,
    api::worker::c_test_controller,
    api::worker::cpp_test_controller,
    api::worker::java_test_controller,
    api::worker::python3_test_controller,
    api::worker::go_test_controller,
    api::worker::robust_contrller,
))]
pub struct ApiDoc;

#[tokio::main]
async fn main() {
    let app = Router::new()
        .nest("/api", api::stage())
        .nest("/worker", worker::stage())
        .merge(SwaggerUi::new("/swagger").url("/api-docs/openapi.json", ApiDoc::openapi()));

    let listener = tokio::net::TcpListener::bind("127.0.0.1:8000")
        .await
        .unwrap();

    println!("App running on http://localhost:8000");
    println!("SwaggerUI running on http://localhost:8000/swagger");

    axum::serve(listener, app).await.unwrap();
}

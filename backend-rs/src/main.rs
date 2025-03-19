#[macro_use]
extern crate rocket;

mod api;
mod worker;

#[launch]
fn rocket() -> _ {
    rocket::build()
        .attach(api::accounts::stage())
        .attach(worker::stage())
}

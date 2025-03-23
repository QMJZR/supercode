import { defineMiddleware } from "astro:middleware";

export const onRequest = defineMiddleware((context, next) => {
  // exclude login and register
  if (context.url.pathname == "/login" || context.url.pathname == "/register") {
    return next();
  }
  return new Response("Unauthorized", { status: 401 });
});

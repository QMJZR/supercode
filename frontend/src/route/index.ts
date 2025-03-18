import { createRouter, createWebHashHistory } from "vue-router";

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: "/",
      redirect: "/login"
    },
    {
      path: "/login",
      component: () => import("../views/user/UserLogin.vue")
    },
    {
      path: "/register",
      component: () => import("../views/user/UserRegister.vue")
    },
    {
      path: "/app",
      component: () => import("../views/app/UserApp.vue")
    },
    {
      path: "/admin",
      component: () => import("../views/admin/AdminDashboard.vue"),
    }
  ]
});

router.beforeEach((to, _, next) => {
  if (to.meta.title) {
    document.title = to.meta.title as string;
  }
  next()
})

export {router}

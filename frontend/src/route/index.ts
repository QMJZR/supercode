import { createRouter, createWebHashHistory } from "vue-router";

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: "/",
      redirect: "/login",
    },
    {
      path: "/login",
      name: "Login",
      component: () => import("../views/user/UserLogin.vue"),
      meta: { title: "请登录" },
    },
    {
      path: "/register",
      name: "Register",
      component: () => import("../views/user/UserRegister.vue"),
      meta: { title: "注册" },
    },
    {
      path: "/app",
      name: "App",
      component: () => import("../views/app/UserApp.vue"),
      meta: { title: "首页" },
    },
    {
      path: "/admin",
      name: "Admin",
      component: () => import("../views/admin/AdminDashboard.vue"),
      meta: { title: "管理" },
    },
  ],
});

router.beforeEach((to, _, next) => {
  if (to.meta.title) {
    document.title = to.meta.title as string;
  }
  if (to.path == '/login') {
    
  }
  next();
});

export { router };

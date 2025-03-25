import axios from "axios";
axios.defaults.withCredentials = true;
type LoginInfo = {
  username: string;
  password: string;
};

type RegisterInfo = {
  username: string;
  password: string;
  name: string;
  avatar: string | null;
  telephone: string | null;
  email: string | null;
  location: string | null;
};

export type UpdateInfo = {
  username: string;
  name: string;
  avatar: string | null;
  telephone: string | null;
  email: string | null;
  location: string | null;
};

// 如果有“Vue: This may be converted to an async function”警告，可以不管
// 用户登录
export const userLogin = (loginInfo: LoginInfo) => {
  return axios
    .post(`/api/accounts/login`, loginInfo, {
      headers: { "Content-Type": "application/json" },
    })
    .then((res) => {
      return res;
    });
};

// 用户注册
export const userRegister = (registerInfo: RegisterInfo) => {
  return axios
    .post(`/api/accounts`, registerInfo, {
      headers: { "Content-Type": "application/json" },
    })
    .then((res) => {
      return res;
    });
};

// 获取用户信息
export const userInfo = (username: string) => {
  return axios.get(`/api/accounts/${username}`).then((res) => {
    return res.data;
  });
};

// 更新用户信息
export const userInfoUpdate = (updateInfo: UpdateInfo) => {
  return axios
    .put(`/api/accounts`, updateInfo, { headers: { "Content-Type": "application/json" } })
    .then((res) => {
      return res.data;
    });
};

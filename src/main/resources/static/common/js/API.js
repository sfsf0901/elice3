import axios from "./axios/dist/esm/axios.js";

const api = axios.create({
  baseURL: "/api/v1",
  timeout: 10000,
  headers: {"Content-Type": "application/json"}
});

api.interceptors.response.use(
  response => response, error => {
    if (error.response) {
      const status = error.response.status;

      console.log(error.response);

      if(status === 401){
        alert(error.response.data)
        location.href = "/login";
      } else if(status === 403) {
        // TODO Jwt reissue API 호출
      }
    }
    return Promise.reject(error);
  }
)

const time = function time(date){
  const start = new Date(date);
  const end = new Date();

  const seconds = Math.floor((end.getTime() - start.getTime()) / 1000);
  if (seconds < 60) return '방금 전';

  const minutes = seconds / 60;
  if (minutes < 60) return `${Math.floor(minutes)}분 전`;

  const hours = minutes / 60;
  if (hours < 24) return `${Math.floor(hours)}시간 전`;

  const days = hours / 24;
  if (days < 7) return `${Math.floor(days)}일 전`;

  return `${start.toLocaleDateString()}`;
}

const memberAPI = {
  REGISTER: "api/v1/members",
}

export default api
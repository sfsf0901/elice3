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
      alert(error.response.data);

      console.log(error.response);

      if(status === 401){
        location.href = "/login";
      } else if(status === 403) {
        // TODO Jwt reissue API 호출
      }
    }
    return Promise.reject(error);
  }
)

export default api
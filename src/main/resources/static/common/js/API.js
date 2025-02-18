import axios from "./axios/dist/esm/axios.js";

const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
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

function test401(){
  api.get("/members/401");
}

document.getElementById("401").addEventListener("click", () => {
  api.get("/members/401")
})

const memberAPI = {
  REGISTER: "api/v1/members",
}
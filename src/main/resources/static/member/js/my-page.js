import api from "/common/js/API.js";

function popup() {
  let url = "/license";
  let name = "면허 인증";
  let option = "width = 800, height = 500, top = 100, left = 200, location = no, resizable = no"
  window.open(url, name, option);
}

document.getElementById("doctor-verification").addEventListener("click", popup);

// document.getElementById("quit").addEventListener("click", () => {
//   if(confirm("정말 탈퇴하시겠습니까?")){
//     api.patch("members/quit", {
//       password:
//     })
//       .then(response => {
//         fetch("/logout", {method: "POST"});
//       });
//   }
// })
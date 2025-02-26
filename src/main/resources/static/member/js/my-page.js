import api from "/common/js/API.js";

function popup() {
  let url = "/license";
  let name = "면허 인증";
  let option = "width = 800, height = 500, top = 100, left = 200, location = no, resizable = no"
  window.open(url, name, option);
}

document.getElementById("doctor-verification").addEventListener("click", popup);

function renderMemberInfo(data) {
  console.log(document.getElementById("email"));
  document.getElementById("name").textContent = data.name;
  document.getElementById("email").textContent = `(${data.email})`;
  document.getElementById("current-name").textContent = data.name;
}

document.getElementById("name-confirm-button").addEventListener("click", () => {
  api.patch("members/info", {
    name: document.getElementById("name-input").value
  }).then(response => {
    alert("닉네임이 변경되었습니다.");
    location.href = "/my-page";
  })
})

api.get("members/info")
  .then(response => {
    console.log(response.data);
    renderMemberInfo(response.data);
  })

document.getElementById("quit-confirm-button").addEventListener("click", () => {
  if (confirm("정말 탈퇴하시겠습니까?")) {
    api.patch("members/quit").then(response => {
      if(response.status !== 200){
        alert(response.data.message);
      } else {
        alert("탈퇴가 완료되었습니다.");
        api.post("http://localhost:8080/logout");
        location.href = "/";
      }
    }).catch(error => {
      alert(error.response.data.message);
      console.log("error : " + error);
      console.log(error);
    })
  }
})
import api from "/common/js/API.js";

document.getElementById("confirm-button").addEventListener("click", () => {
  api.patch("members/password", {
    currentPassword: document.getElementById("current-password").value,
    newPassword: document.getElementById("new-password").value
  }).then(response => {
    console.log(response)
    alert("비밀번호가 변경되었습니다.");
    api.post("http://localhost:8080/logout")
    location.href = "/";
  }).catch(error => {
    if(error.status === 400)
      alert("비밀번호를 양식에 맞게 입력해주세요");
    if(error.status === 500)
      alert("현재 비밀번호가 일치하지 않습니다.");
    console.log(error);
  })
})

const currentPassword = document.getElementById("current-password");
const newPassword = document.getElementById("new-password");
const confirmPassword = document.getElementById("confirm-password");
const submitBtn = document.getElementById("confirm-button");

function validateForm() {
  if (currentPassword.value && newPassword.value && confirmPassword.value && newPassword.value === confirmPassword.value) {
    submitBtn.disabled = false;
  } else {
    submitBtn.disabled = true;
  }
}

currentPassword.addEventListener("input", validateForm);
newPassword.addEventListener("input", validateForm);
confirmPassword.addEventListener("input", validateForm);
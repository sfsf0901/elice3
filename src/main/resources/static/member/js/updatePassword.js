import api from "/common/js/API.js";

(() => {
  'use strict';

  // Fetch all the forms we want to apply custom Bootstrap validation styles to
  const forms = document.querySelectorAll('.needs-validation');

  // Loop over them and prevent submission
  Array.prototype.slice.call(forms).forEach((form) => {
    form.addEventListener('input', (event) => {
      const password = document.getElementById("new-password");
      const confirmPassword = document.getElementById("confirm-password");
      const passwordMatch = password.value === confirmPassword.value && password.length > 0;

      if (password.value !== confirmPassword.value) {
        confirmPassword.setCustomValidity("비밀번호가 일치하지 않습니다.");
      } else {
        confirmPassword.setCustomValidity(""); // 문제 없으면 초기화
      }

      if (!form.checkValidity()) {
        document.getElementById("submit-button").disabled = true;
        event.preventDefault();
        event.stopPropagation();
      } else{
        document.getElementById("submit-button").disabled = false;
      }
      form.classList.add('was-validated');

    }, false);

  });
})();

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
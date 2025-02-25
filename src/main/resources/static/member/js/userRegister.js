import api from "/common/js/API.js";

(() => {
  'use strict';

  // Fetch all the forms we want to apply custom Bootstrap validation styles to
  const forms = document.querySelectorAll('.needs-validation');

  // Loop over them and prevent submission
  Array.prototype.slice.call(forms).forEach((form) => {
    form.addEventListener('submit', (event) => {
      if (!form.checkValidity()) {
        event.preventDefault();
        event.stopPropagation();
      }
      form.classList.add('was-validated');
    }, false);
  });
})();

// document.querySelectorAll("input").forEach(element => {
//   element.addEventListener("input", )
// })

const code = Math.random().toString(36).substring(2, 11);

document.getElementById("submit-button").addEventListener("click", userRegister);

async function userRegister() {
  const isExist = (await api.get("members/exist", {
    params:{email: document.getElementById("email").value}
  })).data;
  if(isExist){
    alert("가입된 이메일이 이미 존재합니다.");
    return;
  }

  api.post("/mail", {
    address: document.getElementById("email").value,
    code
  })
  alert("인증 메일이 발송되었습니다.")

  document.querySelectorAll("input").forEach(item => {
    item.disabled = true;
  });

  document.getElementById("submit-button").style.display = "none";
  document.getElementById("verify-button").style.display = "block"
}

document.getElementById("verify-button").addEventListener("click", verifySuccess)

function verifySuccess(){
  api.get("/mail/verification-success", {
    params: {
      code: code
    }
  })
    .then(response => {
      console.log(response);
      if(response.data) {
        api.post("members", {
          email: document.getElementById('email').value,
          name: document.getElementById('name').value,
          password: document.getElementById('password').value,
        })
          .then(res => {
            console.log(res);
            alert("회원가입이 완료되었습니다.");
            location.href = "/login";
          })
      } else
        alert("이메일 인증을 완료해주세요.");
    })
}

window.addEventListener("beforeunload", event => {
  event.preventDefault();
})

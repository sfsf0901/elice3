// document.getElementById('signupForm').addEventListener('submit', function(event) {
//     event.preventDefault(); // 폼 제출 막기
//
//     let isValid = true;
//
//     // 각 입력값을 가져옵니다.
//     const email = document.getElementById('email').value;
//     const name = document.getElementById('name').value;
//     const password = document.getElementById('password').value;
//     const confirmPassword = document.getElementById('confirmPassword').value;
//     const contact = document.getElementById('contact').value;
//
//     // 에러 메시지 초기화
//     document.getElementById('emailError').style.display = 'none';
//     document.getElementById('nameError').style.display = 'none';
//     document.getElementById('passwordError').style.display = 'none';
//     document.getElementById('confirmPasswordError').style.display = 'none';
//     document.getElementById('contactError').style.display = 'none';
//
//     // 이메일 검증
//     if (!email) {
//         document.getElementById('emailError').textContent = '이메일을 입력하세요';
//         document.getElementById('emailError').style.display = 'block';
//         isValid = false;
//     }
//
//     // 이름 검증
//     if (!name) {
//         document.getElementById('nameError').textContent = '이름을 입력하세요';
//         document.getElementById('nameError').style.display = 'block';
//         isValid = false;
//     }
//
//     // 비밀번호 검증
//     if (!password) {
//         document.getElementById('passwordError').textContent = '비밀번호를 입력하세요';
//         document.getElementById('passwordError').style.display = 'block';
//         isValid = false;
//     }
//
//     // 비밀번호 확인 검증
//     if (password !== confirmPassword) {
//         document.getElementById('confirmPasswordError').textContent = '비밀번호가 일치하지 않습니다';
//         document.getElementById('confirmPasswordError').style.display = 'block';
//         isValid = false;
//     }
//
//     // 연락처 검증
//     if (!contact) {
//         document.getElementById('contactError').textContent = '연락처를 입력하세요';
//         document.getElementById('contactError').style.display = 'block';
//         isValid = false;
//     }
//
//     // 모든 검증이 통과하면 회원가입 성공 처리
//     if (isValid) {
//         alert('회원가입이 완료되었습니다!');
//         // 실제로 회원가입 API 요청을 여기서 처리할 수 있습니다.
//     }
// });

document.getElementById("submit-button").addEventListener("click", userRegister);

async function userRegister(){
    const memberDto = {
        email: document.getElementById('email').value,
        name: document.getElementById('name').value,
        password: document.getElementById('password').value,
        contact: document.getElementById('contact').value,
        role: "USER"
    }

    console.log(memberDto);

    const response = await fetch("/api/v1/members/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(memberDto)
    });

    if(response.ok)
        location.href = response.headers.get("Location");
}
// 특정 섹션 보이기
function showSection(sectionId) {
    document.querySelectorAll('.content-section').forEach(section => {
        section.style.display = 'none';
    });
    document.getElementById(sectionId).style.display = 'block';
}

// 회원탈퇴 확인
function confirmWithdrawal() {
    if (confirm("정말로 탈퇴하시겠습니까?")) {
        alert("회원탈퇴가 완료되었습니다.");
        window.location.href = "/";  // 메인 페이지로 이동
    }
}

// 회원정보 수정 제출
document.getElementById("editProfileForm").addEventListener("submit", function(event) {
    event.preventDefault();
    alert("회원 정보가 수정되었습니다.");
});

// 비밀번호 변경 제출
document.getElementById("changePasswordForm").addEventListener("submit", function(event) {
    event.preventDefault();
    const newPassword = document.getElementById("new-password").value;
    const confirmPassword = document.getElementById("confirm-new-password").value;

    if (newPassword !== confirmPassword) {
        alert("새 비밀번호가 일치하지 않습니다.");
        return;
    }

    alert("비밀번호가 변경되었습니다.");
});

async function test() {
    const member = await (await fetch("api/v1/members/info")).json()
    document.getElementById("name").textContent = member.name;
    document.getElementById("email").textContent = "(" + member.email + ")";
}

test();
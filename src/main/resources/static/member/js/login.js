

document.getElementById("loginForm").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.target;
  const formData = new FormData(form);

  try {
    const response = await fetch(form.action, {
      method: "POST",
      body: formData,
    });

    if(!response.ok) {
      throw new Error("로그인 실패");
    }
  } catch (error) {
    alert("회원 정보가 일치하지 않습니다. 다시 시도해주세요");
  }
})
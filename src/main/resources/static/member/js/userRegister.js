import memberAPI from "/common/js/APIUrl.js";

console.log(memberAPI.REGISTER)

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

    const response = await fetch(memberAPI.REGISTER, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(memberDto)
    });

    if(response.ok)
        location.href = response.headers.get("Location");
}
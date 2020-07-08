function blobUpload() {
    fetch('/blobstore-upload-url')
        .then((response) => {
            return response.text();
    })
    .then((resumeUploadUrl) => { 
        const resume = document.getElementById('form'); 
        resume.action = resumeUploadUrl; 
    });
}

function login() {
    fetch('/login').then(response => response.json()).then((login) => {
        const loginLinkElement = document.getElementById('login-container');
        const myAccountElement = document.getElementById('my-account');
        const greetingElement = document.getElementById('greeting-container');
        if (login.isValidUser) {
            // Show the 'my account' option
            // Show a log out option
            greetingElement.innerHTML = "Welcome " + login.email + "!";
            myAccountElement.innerHTML = "<a href=\"resume-review.html\">My Account</a>" +
            "  •  " + "<a href=\"" + login.logout_url + "\">Log Out</a>";
            loginLinkElement.style.display = "none";
        }
        else {
            // Show the log in option
            loginLinkElement.innerHTML = "<a href=\"" + 
            login.login_url + "\">Log In</a>";
            myAccountElement.style.display = "none";
            greetingElement.style.display = "none";
        }
    });
}

function login() {
    fetch('/login').then(response => response.json()).then((login) => {
        const loginLinkElement = document.getElementById('login-container');
        const myAccountElement = document.getElementById('my-account');
        if (login.status) {
            // Show the 'my account' option
            // Show a log out option
            myAccountElement.style.display = "block";
            loginLinkElement.innerHTML = "<a style=\"text-decoration:none\" href=\"" + 
            login.logout_url + "\">Log Out</a>";
            
        }
        else {
            // Show the log in option
            loginLinkElement.innerHTML = "<a style=\"text-decoration:none\" href=\"" + 
            login.login_url + "\">Log In</a>";
            myAccountElement.style.display = "none";
        }
    });
}

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

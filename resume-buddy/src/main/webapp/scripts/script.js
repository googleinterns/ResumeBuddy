function blobUpload() {
  fetch('/blobstore-upload')
    .then((response) => {
      return response.text();
    })
    .then((resumeUploadUrl) => {
      const resume = document.getElementById('reviewee-form');
      resume.action = resumeUploadUrl;
    });
}

function login() {
  fetch('/login').then(response => response.json()).then((login) => {
    const loginLinkElement = document.getElementById('login-link-container');
    const greetingElement = document.getElementById('greeting-container');
    loginLinkElement.style.display = "block";
    if (login.status) {
      greetingElement.innerHTML = "Welcome " + login.email + "!";
      loginLinkElement.innerHTML = "<a href=\"resume-review.html\">My Account</a>" +
        "  •  " + "<a href=\"" + login.logout_url + "\">Log Out</a>";
    }
    else {
      loginLinkElement.innerHTML = "Log in <a href=\"" + login.login_url + "\">here</a>";
      greetingElement.style.display = "none";
    }
  });
}

/** Class function when form page loads */
function startForm() {
  blobUpload();
  populateUnis();
  populateCareers();
  populateFormWithKnownData();
}

/** Gets university names from json file and populates options for school */
function populateUnis() {
  const schoolSelect = document.getElementById("school");
  fetch("universities.json")
    .then(response => response.json())
    .then(unis => {
      unis.forEach((uni) => {
        let option = document.createElement("option");
        option.text = uni.institution;
        option.value = uni.institution;
        schoolSelect.appendChild(option);
      })
    });
}

/** Gets career field names from json file and populates options for career */
function populateCareers() {
  const careerSelect = document.getElementById("career");
  fetch("careers.json")
    .then(response => response.json())
    .then(careers => {
      careers.forEach((career) => {
        let option = document.createElement("option");
        option.text = career.name;
        option.value = career.name;
        careerSelect.appendChild(option);
      })
    });
}

/** Gets known data from the User db and populates form */
function populateFormWithKnownData() {
  fetch('/user-data')
    .then(response => response.json())
    .then(user => {
      if (user.firstName !== 'undefined' && user.lastName !== 'undefined') {
        document.getElementById("fname").value = user.firstName;
        document.getElementById("lname").value = user.lastName;
      }
    });
}

function openForm(page) {
  fetch('/login?redirect=' + page).then(response => response.json()).then((login) => {
    if (login.status) {
      window.location.href = page;
    }
    else {
      window.location.href = login.login_url;
    }
  });
}

function successMessage() {
  alert('Thank you, your form has been submitted.');
}
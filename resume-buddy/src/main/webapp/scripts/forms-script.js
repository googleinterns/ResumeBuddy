function populateUnis() {

  const schoolSelect = document.getElementById("school");

  fetch("universities.json")
    .then(response => response.json())
    .then(unis => {
      unis.forEach((uni) => {
        let option = document.createElement("option");
        console.log(uni.institution);
        option.text = uni.institution;
        console.log(option.text);
        option.value = uni.institution;
        schoolSelect.appendChild(option);
      })
    });
}

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

function populateOptions() {
  populateUnis();
  populateCareers();
}

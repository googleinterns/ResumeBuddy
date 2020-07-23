/** Run functions when page is loaded */
function onLoad() {
  fetch('/user-data')
    .then(response => response.json())
    .then(user => {
      if (user.matchID != '') { 
        console.log(user.matchID);
        getMatch(user.matchID);
        getComments(); 
      }
      else { document.getElementById('match-info').innerText = "You have not been matched yet."; }
    });
}

/** Gets reviewer and reviewee using matchID */
async function getMatch(matchID) {
  fetch('/review-page?matchId=' + matchID)
    .then(response => response.json())
    .then((match) => {
        populateReviewer(match.reviewer);
        populateReviewee(match.reviewee);
      }) 
}

/** Add information about reviewer in HTML DOM */
async function populateReviewer(reviewerEmail) {
  fetch('/user-data?email=' + reviewerEmail)
    .then(response => response.json())
    .then(reviewer => {
      document.getElementById("reviewer-name").innerHTML = reviewer.firstName + ' ' + reviewer.lastName;
      document.getElementById("reviewer-education").innerHTML = reviewer.degree;
      document.getElementById("reviewer-school").innerHTML = reviewer.school;
      document.getElementById("reviewer-career").innerHTML = reviewer.career;      
    });
}

/** Add information about reviewee in HTML DOM */
async function populateReviewee(revieweeEmail) {
  fetch('/user-data?email=' + revieweeEmail)
    .then(response => response.json())
    .then(reviewee => {
      document.getElementById("reviewee-name").innerHTML = reviewee.firstName + ' ' + reviewee.lastName;
      document.getElementById("reviewee-school-year").innerHTML = reviewee.schoolYear;
      document.getElementById("reviewee-school").innerHTML = reviewee.school;
      document.getElementById("reviewee-career").innerHTML = reviewee.career;
    });
}

/**
 * Fetches comments from the servers and adds them to the DOM.
 */
function getComments() {
  document.getElementById('match-info').style.display = "none";
  document.getElementById('comments').style.display = "block";
  fetch('/comment').
    then(response => response.json())
    .then((comments) => {
      const commentListElement = document
        .getElementById('comments-container');

      commentListElement.innerHTML = '';
      comments.forEach((comment) => {
        let date = new Date(comment.date);
        commentListElement.appendChild(
          createListElement(
            date.getMonth() + '/' + date.getDate() + '/' +
            date.getFullYear(), comment.type, comment.text,
            comment.id, comment.author));
      })

    });
}

/**
 * Fetches delete-comments to delete comment using comment id
 */
function deleteComments(id) {
  const queryStr = 'id=' + id;
  fetch('/delete-comment?' + queryStr, {
    method: 'POST',
  });

  if (!id) {
    document.getElementById('comments-container').innerHTML = '';
  } else {
    location.reload();
  }

}

/** 
 * Creates an <li> element containing date, comment type and text
 */
function createListElement(date, type, text, id, author) {
  const liElement = document.createElement('li');
  const containerDiv = document.createElement('div');

  containerDiv.className = 'comment-container';
  const typeText = document.createElement('b');
  typeText.innerText = type + ":  ";
  liElement.appendChild(typeText);

  const textNode = document.createTextNode(text + " ");
  liElement.appendChild(textNode);

  const signatureNode = document.createElement("div");
  signatureNode.innerHTML = "<i>" + author + " " + date + "</i>";
  liElement.appendChild(signatureNode);

  const deleteButton = document.createElement('button');
  deleteButton.innerHTML = '&#10005;';
  deleteButton.className = "delete-button";
  deleteButton.onclick = function() {
    deleteComments(id);
  }

  liElement.appendChild(deleteButton);

  return liElement;
}

/**
 * Fetches the blobstore-serve to sends its response as an array buffer to the Adobe DC View
 */
 const previewConfig={
  "showLeftHandPanel":true,
  "showPageControls":false,
  "showDownloadPDF": false,
  "showAnnotationTools": false,
  "showPrintPDF": false,
  "embedMode": "IN_LINE"
}

async function getRevieweeResume() {
  fetch('/blobstore-serve')
    .then((response) => {
      var adobeDCView = new AdobeDC.View({
        clientId: "",
        divId: "adobe-dc-view"
      });
      adobeDCView.previewFile({
        content: {
          promise: response.arrayBuffer()
        },
        metaData: {
          fileName: "revieweeResume.pdf"
        }},
    previewConfig);
});
}

/*
 * Sends POST request to /review-done which updates status
 */
function reviewIsDone() {
  fetch('/review-page', {
    method: 'POST',
  });
}

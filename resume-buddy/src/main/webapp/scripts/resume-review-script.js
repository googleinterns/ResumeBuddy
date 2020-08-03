/** Run functions when page is loaded */
function onLoad() {
  fetch('/user-data')
    .then(response => response.json())
    .then(user => {
      if (user.matchID != '') {
        getMatch(user.matchID);
        getComments();
      } else {
        document.getElementById('match-info').innerText = "You have not been matched yet.";
      }
    });
}

/** Gets reviewer and reviewee using matchID */
async function getMatch(matchID) {
  fetch('/review-page?matchId=' + matchID)
    .then(response => response.json())
    .then((match) => {
      document.getElementById("match").style.display = 'block';
      populateReviewer(match.reviewer);
      populateReviewee(match.reviewee);
    })
}

/** Add information about reviewer in HTML DOM */
async function populateReviewer(reviewerEmail) {
  fetch('/user-data?email=' + reviewerEmail)
    .then(response => response.json())
    .then(reviewer => {
      document.getElementById("reviewer-name").innerText += reviewer.firstName + ' ' + reviewer.lastName;
      document.getElementById("reviewer-education").innerText += reviewer.degree.toLowerCase();
      document.getElementById("reviewer-school").innerText += reviewer.school;
      document.getElementById("reviewer-company").innerText += reviewer.company;
      document.getElementById("reviewer-career").innerText += reviewer.career;
      if (reviewer.isCurrentUser) {
        document.getElementById("review-done-button").style.display = 'block';
      }
    });
}

/** Add information about reviewee in HTML DOM */
async function populateReviewee(revieweeEmail) {
  fetch('/user-data?email=' + revieweeEmail)
    .then(response => response.json())
    .then(reviewee => {
      document.getElementById("reviewee-name").innerText += reviewee.firstName + ' ' + reviewee.lastName;
      document.getElementById("reviewee-school-year").innerText += reviewee.schoolYear.toLowerCase();
      document.getElementById("reviewee-school").innerText += reviewee.school;
      document.getElementById("reviewee-career").innerText += reviewee.career;
      if (reviewee.isCurrentUser) {
        document.getElementById("feedback-done-button").style.display = 'block';
      }
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
function deleteComments(id, author) {
  const queryStr = `id=${id}&author=${author}`;
  fetch(`/delete-comment?${queryStr}`, {
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
    deleteComments(id, author);
  }
  liElement.appendChild(deleteButton);
  return liElement;
}

//Adobe Preview configurations for getRevieweeResume function
const previewConfig = {
  showLeftHandPanel:true,
  showPageControls:true,
  showDownloadPDF: false,
  showPrintPDF: false,
  enableAnnotationsAPI: true,
  includePDFAnnotations: true
}

/** Save button shown after annotation is added */
const saveOptions = {
  showSaveButton: true
}

/**
 * Fetches the blobstore-serve to sends its response as an array buffer to the Adobe DC View
 */
async function getRevieweeResume() {
  fetch('/blobstore-serve')
    .then((response) => {
      const pdfId = response.headers.get('blobKeyString');
      const resumeFileName = response.headers.get('resumeFileName');
      const showAnnoTools = (response.headers.get('annoToolBool') === 'true');
      previewConfig.showAnnotationTools = showAnnoTools;
      var adobeDCView = new AdobeDC.View({
        clientId: '',
        divId: 'adobe-dc-view'
      });
      adobeDCView.previewFile({
        content: {
          promise: response.arrayBuffer()
        },
        metaData: {
          fileName: resumeFileName,
          id: pdfId
        }
      },
        previewConfig);

      // saves annotated pdf in 'content' after 'save' is clicked
      adobeDCView.registerCallback(
        AdobeDC.View.Enum.CallbackType.SAVE_API,
        function(metaData, content, options) {
          changePdf(content, resumeFileName + 'Resume.pdf');
          return new Promise((resolve, reject) => {
            resolve({
              code: AdobeDC.View.Enum.ApiResponseCode.SUCCESS,
              data: {
                metaData: {
                  fileName: resumeFileName + 'Resume.pdf'
                },
              },
            }),
            reject({
              code: AdobeDC.View.Enum.ApiResponseCode.FAIL,
              data: {
                optional
              },
            });
          });
        }, saveOptions
      );
  });
}

/**
 * Gets new upload url and uploads blob
 */
async function changePdf(content, fileName) {
  var file = new Blob([content], {
    type: 'application/pdf'
  });
  var formData = new FormData();
  formData.append('resume', file, fileName);

  fetch('/blobstore-upload?redirect=/change-blob')
    .then((response) => {
      return response.text();
    }).
    then(uploadUrl => {
      fetch(uploadUrl, {
        method: 'POST',
        body: formData
      })
      .then(response => {
        console.log(`Response from updating BlobStore entry is ${response}`);
      })
    });
}

/** Sends POST request to /review-done which updates status */
function reviewIsDone() {
  fetch('/review-page', {
    method: 'PUT'
  });
  window.location.href = '/index.html';
}

/** Sends PUT request to /delete-match-data which deletes match and blob data */
function feedbackIsRead() {
  fetch('/delete-match-data', {
    method: 'PUT'
  });
  window.location.href = '/index.html';
}

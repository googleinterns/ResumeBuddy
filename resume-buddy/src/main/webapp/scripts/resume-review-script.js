/** Run functions when page is loaded */
function onLoad() {
  getComments();
}

/**
 * Fetches comments from the servers and adds them to the DOM.
 */
function getComments() {
  // default hide comments section
  const commentsElement = document.getElementById('comments');
  commentsElement.style.display = "none";  
  // IF the user has a match, display comments functionality :
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
            comment.id));
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
function createListElement(date, type, text, id) {
  const liElement = document.createElement('li');
  const containerDiv = document.createElement('div');

  containerDiv.className = 'comment-container';
  const typeText = document.createElement('b');
  typeText.innerText = type + ":  ";
  liElement.appendChild(typeText);

  const textNode = document.createTextNode(text + " ");
  liElement.appendChild(textNode);

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
        }
      }, {embedMode : "IN-LINE"});
    });
}

 /*
  * Sends POST request to /review-done which updates status
  */
function reviewIsDone() {
  fetch('/review-done', {
    method: 'POST',
  });
}

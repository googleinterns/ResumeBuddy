/**
 * Fetches comments from the servers and adds them to the DOM.
 */
function getComments() {
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
 * Creates an <li> element containing date, comment type and text
 */
function createListElement(date, type, text, id) {
	const liElement = document.createElement('li');
	const containerDiv = document.createElement('div');

	containerDiv.className = 'comment-container';
	const typeText = document.createElement('b');
	typeText.innerText = type + " ";
	liElement.appendChild(typeText);

	const dateNode = document.createElement('i');
	dateNode.innerText = date + " ";
	liElement.appendChild(dateNode);

	const textNode = document.createTextNode(text);
	liElement.appendChild(textNode);

	// TODO: Add delete button next to comment which deletes comment

	return liElement;
}

function blobUpload() {
	fetch('/blobstore-upload-url')
		.then((response) => {
			return response.text();
		})
		.then((imageUploadUrl) => {
			const resume = document.getElementById('form');
			resume.action = imageUploadUrl;
		});
}
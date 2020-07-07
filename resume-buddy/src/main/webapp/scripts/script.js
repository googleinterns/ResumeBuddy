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

async function uploadDocument() {
    const formData = new FormData();

    const fileInput = $("#pdfFile")[0];
    const file = fileInput.files[0];

    if (!file) {
        console.error('No file selected');
        return;
    }

    console.log('File selected:', file);

    formData.append('file', file);

    const response = await fetch('http://localhost:8081/api/documents', {
        method: 'POST',
        body: formData
    });

    if (response.ok) {
        $("#message").text('Successfully uploaded!');
    } else {
        $("#message").text('There was an error. Please try again!');
    }
}
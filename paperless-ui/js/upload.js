// upload.js - Handles all document upload functionality
document.addEventListener('DOMContentLoaded', () => {
    // Initialize UI elements
    const fileInput = document.getElementById('pdfFile');
    const uploadButton = document.getElementById('uploadButton');
    const progressBar = document.getElementById('progressBar');
    const progressBarInner = document.getElementById('uploadProgress');
    const dropZone = document.querySelector('.upload-area');

    // Initialize Bootstrap toast for notifications
    const toast = new bootstrap.Toast(document.getElementById('uploadToast'));

    /**
     * Displays a toast notification with the given message and type
     * @param {string} message - Message to display
     * @param {string} type - Bootstrap color class (success, danger, warning, info)
     */
    function showToast(message, type = 'primary') {
        const toastElement = document.getElementById('uploadToast');

        // Remove existing color classes and add new one
        toastElement.className = 'toast';
        toastElement.classList.add(`bg-${type}`, 'text-white');

        // Update message and show toast
        document.getElementById('toastMessage').textContent = message;
        toast.show();
    }

    /**
     * Updates the progress bar state
     * @param {number} progress - Progress percentage (0-100)
     */
    function updateProgress(progress) {
        progressBar.classList.remove('d-none');
        progressBarInner.style.width = `${progress}%`;
        progressBarInner.setAttribute('aria-valuenow', progress);
    }

    /**
     * Resets the upload form to its initial state
     */
    function resetForm() {
        fileInput.value = '';
        fileInput.disabled = false;
        progressBar.classList.add('d-none');
        progressBarInner.style.width = '0%';
        progressBarInner.setAttribute('aria-valuenow', 0);
    }

    /**
     * Handles the document upload process
     * @param {FormData} formData - Form data containing the file to upload
     */
    async function handleUpload(formData) {
        try {
            // Disable input during upload
            fileInput.disabled = true;
            updateProgress(50);

            // Send upload request
            const response = await fetch('http://localhost:8081/api/documents', {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                throw new Error(`Upload failed: ${response.statusText}`);
            }

            // Show success state
            updateProgress(100);
            showToast('Document uploaded successfully! Redirecting...', 'success');

            // Redirect to documents list after short delay
            setTimeout(() => {
                window.location.href = 'documentsList.html';
            }, 1500);

        } catch (error) {
            console.error('Upload error:', error);
            showToast(error.message, 'danger');
            resetForm();
        }
    }

    /**
     * Validates the selected file
     * @param {File} file - The file to validate
     * @returns {boolean} Whether the file is valid
     */
    function validateFile(file) {
        if (!file) {
            showToast('Please select a file first', 'warning');
            return false;
        }

        if (file.type !== 'application/pdf') {
            showToast('Only PDF files are allowed', 'warning');
            return false;
        }

        if (file.size > 10 * 1024 * 1024) { // 10MB limit
            showToast('File size must be less than 10MB', 'warning');
            return false;
        }

        return true;
    }

    // Event Listeners

    // Handle file selection via the file input
    fileInput.addEventListener('change', () => {
        const file = fileInput.files[0];
        if (validateFile(file)) {
            const formData = new FormData();
            formData.append('file', file);
            handleUpload(formData);
        }
    });

    // Drag and drop handling
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dropZone.addEventListener(eventName, (e) => {
            e.preventDefault();
            e.stopPropagation();
        });
    });

    // Visual feedback for drag operations
    ['dragenter', 'dragover'].forEach(eventName => {
        dropZone.addEventListener(eventName, () => {
            dropZone.classList.add('border-primary');
        });
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dropZone.addEventListener(eventName, () => {
            dropZone.classList.remove('border-primary');
        });
    });

    // Handle dropped files
    dropZone.addEventListener('drop', (e) => {
        const file = e.dataTransfer.files[0];
        if (validateFile(file)) {
            fileInput.files = e.dataTransfer.files;
            showToast('File selected, click Upload to proceed', 'info');
        }
    });

    // Handle upload button click
    uploadButton.addEventListener('click', () => {
        const file = fileInput.files[0];
        if (validateFile(file)) {
            const formData = new FormData();
            formData.append('file', file);
            handleUpload(formData);
        }
    });
});
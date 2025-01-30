document.addEventListener('DOMContentLoaded', () => {
    // Initialize UI elements
    const searchInput = document.getElementById('search-input');
    const searchButton = document.getElementById('search-button');
    const tableBody = document.getElementById('documents-table-body');
    const loader = document.getElementById('loader');
    const emptyState = document.getElementById('empty-state');
    const table = document.querySelector('.table');

    // Helper function to show/hide the loading spinner
    function toggleLoader(show) {
        loader.style.display = show ? 'flex' : 'none';
    }

    // Helper function to toggle between empty state and table
    function toggleEmptyState(isEmpty) {
        emptyState.classList.toggle('d-none', !isEmpty);
        table.classList.toggle('d-none', isEmpty);
    }

    // Helper function to format file sizes consistently
    function formatFileSize(bytes) {
        const units = ['B', 'KB', 'MB', 'GB'];
        let size = bytes;
        let unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return `${size.toFixed(1)} ${units[unitIndex]}`;
    }

    // Create table row with Bootstrap icons instead of SVG
    function createTableRow(document) {
        const downloadUrl = `/api/documents/${document.id}/download`;

        // Using Bootstrap icons for better consistency
        const ocrStatus = document.ocrJobDone
            ? '<i class="bi bi-check-circle-fill text-success fs-5"></i>'
            : '<i class="bi bi-hourglass-split text-warning fs-5"></i>';

        return `
            <tr>
                <td>${document.id}</td>
                <td>
                    <i class="bi bi-file-earmark-pdf text-danger me-2"></i>
                    ${document.filename}
                </td>
                <td>${formatFileSize(document.filesize)}</td>
                <td>${document.filetype}</td>
                <td>${new Date(document.uploadDate).toLocaleString()}</td>
                <td class="text-center">${ocrStatus}</td>
                <td>
                    <div class="action-buttons">
                        <a href="${downloadUrl}" 
                           class="btn btn-sm btn-outline-primary" 
                           download="${document.filename}"
                           title="Download">
                            <i class="bi bi-download"></i>
                        </a>
                        <button class="btn btn-sm btn-outline-danger"
                                onclick="deleteDocument('${document.id}')"
                                title="Delete">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>`;
    }

    // Main function to load and render documents
    async function loadDocuments() {
        try {
            toggleLoader(true);
            console.log('Fetching documents...');

            // Using relative path for proxy
            const response = await fetch('/api/documents');
            console.log('Response status:', response.status);

            if (!response.ok) {
                throw new Error(`Failed to fetch documents: ${response.status} ${response.statusText}`);
            }

            const documents = await response.json();
            console.log('Documents received:', documents);

            if (!documents || documents.length === 0) {
                toggleEmptyState(true);
                return;
            }

            toggleEmptyState(false);
            tableBody.innerHTML = documents.map(createTableRow).join('');

        } catch (error) {
            console.error('Error loading documents:', error);
            tableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-danger">
                        <i class="bi bi-exclamation-triangle me-2"></i>
                        ${error.message}
                    </td>
                </tr>`;
        } finally {
            toggleLoader(false);
        }
    }

    // Search documents function
    async function searchDocuments(query) {
        try {
            toggleLoader(true);
            console.log('Searching documents with query:', query);

            const response = await fetch(
                `/api/documents/search?query=${encodeURIComponent(query)}`
            );

            if (!response.ok) {
                throw new Error(`Search failed: ${response.status} ${response.statusText}`);
            }

            const documents = await response.json();
            console.log('Search results:', documents);

            if (!documents || documents.length === 0) {
                toggleEmptyState(true);
                return;
            }

            toggleEmptyState(false);
            tableBody.innerHTML = documents.map(createTableRow).join('');

        } catch (error) {
            console.error('Error searching documents:', error);
            tableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-danger">
                        <i class="bi bi-exclamation-triangle me-2"></i>
                        ${error.message}
                    </td>
                </tr>`;
        } finally {
            toggleLoader(false);
        }
    }

    // Delete document function
    window.deleteDocument = async (id) => {
        if (!confirm('Are you sure you want to delete this document?')) {
            return;
        }

        try {
            toggleLoader(true);
            const response = await fetch(`/api/documents/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('Failed to delete document');
            }

            await loadDocuments();
        } catch (error) {
            console.error('Error deleting document:', error);
            alert('Failed to delete document');
        } finally {
            toggleLoader(false);
        }
    };

    // Event Listeners
    searchButton.addEventListener('click', () => {
        const query = searchInput.value.trim();
        if (query) {
            searchDocuments(query);
        } else {
            loadDocuments();
        }
    });

    searchInput.addEventListener('keypress', (event) => {
        if (event.key === 'Enter') {
            searchButton.click();
        }
    });

    // Initialize by loading documents
    loadDocuments();
});
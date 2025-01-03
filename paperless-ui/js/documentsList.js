$(document).ready(function () {
    $.get("http://localhost:8081/api/documents", function (data) {
        const tableBody = $("#documents-table-body");
        tableBody.empty();

        data.forEach(doc => {
            console.log(doc.createdAt)
            const row = `
            <tr>
              <td>${doc.name}</td>
              <td>${doc.createdAt[2] + "-" + doc.createdAt[1] + "-" + doc.createdAt[0]}</td> 
              <td>
                <a href="http://localhost:8081/api/documents/download/${doc.id}" target="_blank" class="btn btn-success">Download</a>
              </td>
            </tr>
          `;
            tableBody.append(row);
        });
    }).fail(function () {
        alert("Failed to fetch documents from the API.");
    });
});
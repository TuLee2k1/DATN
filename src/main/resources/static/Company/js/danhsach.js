function sortTable(columnIndex, type) {
    const table = document.querySelector("table tbody");
    const rows = Array.from(table.rows);
    const header = document.querySelectorAll("th")[columnIndex];
    const isAscending = header.classList.toggle("asc");

    rows.sort((a, b) => {
        const aText = a.cells[columnIndex].innerText;
        const bText = b.cells[columnIndex].innerText;

        if (type === 'date') {
            const aDate = new Date(aText.split('/').reverse().join('-'));
            const bDate = new Date(bText.split('/').reverse().join('-'));
            return isAscending ? aDate - bDate : bDate - aDate;
        } else if (type === 'number') {
            return isAscending ? parseInt(aText) - parseInt(bText) : parseInt(bText) - parseInt(aText);
        } else {
            return isAscending ? aText.localeCompare(bText) : bText.localeCompare(aText);
        }
    });

    rows.forEach(row => table.appendChild(row));
}

document.getElementById("create-job-btn").addEventListener("click", function() {
    window.location.href = 'Taotintuyendung.html';
  });




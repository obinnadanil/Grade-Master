
        function search() {
            var input = document.getElementById('searchInput').value.toUpperCase();
            var rows = document.getElementById('tableBody').getElementsByTagName('tr');
            for (var i = 0; i < rows.length; i++) {
                var cells = rows[i].getElementsByTagName('td');
                var found = false;
                for (var j = 0; j < cells.length; j++) {
                    var cellText = cells[j].textContent.toUpperCase();
                    if (cellText.indexOf(input) > -1) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    rows[i].style.display = "";
                } else {
                    rows[i].style.display = "none";
                }
            }
        }





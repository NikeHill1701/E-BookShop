function addColumn() {
    var table = document.getElementById('booksTable');
    table.innerHTML +=  "<tr>" +
                        "<td><input type='text' id='title' name='title'></td>" +
                        "<td><input type='text' id='author' name='author'></td>" + 
                        "<td><input type='text' id='price' name='price'></td>" +
                        "<td><input type='number' id='qty' name='qty'></td>" +
                        "</tr>";    
}
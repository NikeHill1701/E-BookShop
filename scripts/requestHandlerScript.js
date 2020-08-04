function handleAddToCartRequest() {
    const form = document.getElementById('addToCartForm');

    const xhttp = new XMLHttpRequest();
    var fd = new FormData(document.getElementById('addToCartForm'));
    console.log(fd);
    return true;
    xhttp.open('POST', 'http://localhost:9999/yaebookshop/cart', false);
    xhttp.send(fd);

    xhttp.open('GET', 'http://localhost:9999/yaebookshop/cart', false);
}
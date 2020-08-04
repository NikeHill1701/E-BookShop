window.onload = init;

function init() {
    document.getElementById('usernameError').innerHTML = '';
    document.getElementById('passwordError').innerHTML = '';
    document.getElementById('loginForm').onsubmit = validate;
    document.getElementById('reset').onclick = () => {
        document.getElementById('usernameError').innerHTML = '';
        document.getElementById('passwordError').innerHTML = '';
    }
}

function validate() {
    var form = document.getElementById('loginForm');
    const username = form['username'].value;
    var flag = 1;
    if (username === "") {
        document.getElementById('usernameError').innerHTML = 'Username cannot be empty';
        flag = 0;
    }
    const password = form['password'].value;
    if (password === "") {
        document.getElementById('passwordError').innerHTML = 'Password cannot be empty';
        flag = 0;
    }
    if (flag === 1)
        return true;
    else
        return false;
}
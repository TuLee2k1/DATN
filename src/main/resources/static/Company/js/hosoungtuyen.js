function setActive(button) {
    var buttons = document.querySelectorAll('.filter-bar .btn');
    buttons.forEach(function(btn) {
        btn.classList.remove('active');
    });
    button.classList.add('active');
}
document.addEventListener("DOMContentLoaded", function() {
    var successAlert = document.querySelector('.alert-success');
    var errorAlert = document.querySelector('.alert-warning');

    // Nếu có thông báo thành công
    if (successAlert) {
        setTimeout(function() {
            successAlert.style.display = 'none'; // Ẩn thông báo sau 3 giây
        }, 3000);
    }

    // Nếu có thông báo lỗi
    if (errorAlert) {
        setTimeout(function() {
            errorAlert.style.display = 'none'; // Ẩn thông báo sau 3 giây
        }, 3000);
    }
});
document.addEventListener("DOMContentLoaded", function() {
    var favoriteButtons = document.querySelectorAll(".favorite.toggle-follow-job");

    favoriteButtons.forEach(function(favoriteButton) {
        favoriteButton.addEventListener("click", function() {
            var jobPostId = this.getAttribute("data-job-id");
            var heartIcon = this.querySelector('i');

            fetch('/follow/toggle-follow-job/' + jobPostId, {
                method: 'POST',
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    // Toggle heart icon classes
                    if (heartIcon.classList.contains('far')) {
                        heartIcon.classList.remove('far');
                        heartIcon.classList.add('fas');
                    } else {
                        heartIcon.classList.remove('fas');
                        heartIcon.classList.add('far');
                    }

                    alert(data.message);
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Có lỗi xảy ra: ' + error.message);
                });
        });
    });
});
function previewAvatar(event) {
    var input = event.target;
    var reader = new FileReader();
    reader.onload = function(){
        var dataURL = reader.result;
        var output = document.getElementById('avatarPreview');
        output.src = dataURL;
    };
    reader.readAsDataURL(input.files[0]);
}

function validateForm(event) {
    let isValid = true;

    // Lấy giá trị input
    const fullName = document.getElementById('fullName').value.trim();
    const dateOfBirth = document.getElementById('dateOfBirth').value.trim();
    const email = document.getElementById('contactEmail').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const address = document.getElementById('address').value.trim();
    const sex = document.getElementById('sex').value.trim();

    // Xóa thông báo lỗi trước đó
    document.querySelectorAll('.error').forEach(el => el.innerText = '');
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));

    // // Kiểm tra họ và tên (không được để trống và không được chứa số)
    // if (fullName === '') {
    //     document.getElementById('fullNameError').innerText = 'Họ và tên không được để trống.';
    //     isValid = false;
    // } else if (/\d/.test(fullName)) { // Kiểm tra xem có số không
    //     document.getElementById('fullNameError').innerText = 'Họ và tên không được chứa số.';
    //     document.getElementById('fullName').classList.add('is-invalid');
    //     isValid = false;
    // }
    // Kiểm tra họ và tên (không được để trống, không chứa số và ký tự đặc biệt)
    const specialCharPattern = /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\\/~`0-9]/; // Ký tự đặc biệt và số

    if (fullName === '') {
        document.getElementById('fullNameError').innerText = 'Họ và tên không được để trống.';
        document.getElementById('fullName').classList.add('is-invalid');
        isValid = false;
    }
// Kiểm tra số và ký tự đặc biệt
    else if (specialCharPattern.test(fullName)) {
        document.getElementById('fullNameError').innerText = 'Họ và tên không được chứa số hoặc ký tự đặc biệt.';
        document.getElementById('fullName').classList.add('is-invalid');
        isValid = false;
    } else {
        // Xóa lỗi nếu hợp lệ
        document.getElementById('fullNameError').innerText = '';
        document.getElementById('fullName').classList.remove('is-invalid');
    }

    // Kiểm tra email
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
        document.getElementById('contactEmail').classList.add('is-invalid');
        document.getElementById('contactEmail').focus();
        document.getElementById('contactEmail').nextElementSibling.innerText = 'Email không đúng định dạng.';
        isValid = false;
    }
    else if (email === '')
    {
        document.getElementById('contactEmail').nextElementSibling.innerText = 'Email không đuược bỏ trống!.';
    }

    // Kiểm tra số điện thoại
    const phonePattern = /^[0-9]{10,11}$/; // Chấp nhận 10-11 chữ số
    if (!phonePattern.test(phone)) {
        document.getElementById('phoneError').innerText = 'Số điện thoại không hợp lệ (10-11 chữ số).';
        isValid = false;
    }

    // Kiểm tra địa chỉ
    if (address === '') {
        document.getElementById('addressError').innerText = 'Địa chỉ không được để trống.';
        isValid = false;
    }

    // Nếu form không hợp lệ, ngăn submit
    if (!isValid) {
        event.preventDefault(); // Ngăn form submit
    }
}

// Gắn sự kiện vào form
document.getElementById('profileForm').addEventListener('submit', validateForm);


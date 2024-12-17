function validateForm(event) {
    let isValid = true;

    // Lấy giá trị các input
    const name = document.getElementById('contactName').value.trim();
    const email = document.getElementById('contactEmail').value.trim();
    const phone = document.getElementById('contactPhone').value.trim();
    const address = document.getElementById('interviewAddress').value.trim();
    const sex = document.getElementById('sex').value.trim();
    const dateOfBirth = document.getElementById('dateOfBirth').value.trim();
    const schoolName = document.getElementById('schoolName').value.trim();
    const degree = document.getElementById('degree').value.trim();
    const startDate = document.getElementById('startDate').value.trim();
    const endDate = document.getElementById('endDate').value.trim();
    const gpa = document.getElementById('gpa').value.trim();
    const jobTitle = document.getElementById('jobTitle').value.trim();
    const companyName = document.getElementById('companyName').value.trim();
    const startJobDate = document.getElementById('startJobDate').value.trim();
    const endJobDate = document.getElementById('endJobDate').value.trim();
    const jobDescription = document.getElementById('jobDescription').value.trim();

    // Xóa thông báo lỗi trước đó
    document.querySelectorAll('.error').forEach(el => el.remove());
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));

    // Kiểm tra Họ và tên
    const namePattern = /^[^\d]*$/; // Không chứa số
    if (name === '') {
        const nameField = document.getElementById('contactName');
        nameField.classList.add('is-invalid');
        nameField.insertAdjacentHTML('afterend', '<div class="error text-danger">Họ và tên không được để trống.</div>');
        isValid = false;
    } else if (!namePattern.test(name)) {
        const nameField = document.getElementById('contactName');
        nameField.classList.add('is-invalid');
        nameField.insertAdjacentHTML('afterend', '<div class="error text-danger">Họ và tên không được chứa số.</div>');
        isValid = false;
    }

    // Kiểm tra Email
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
        const emailField = document.getElementById('contactEmail');
        emailField.classList.add('is-invalid');
        emailField.insertAdjacentHTML('afterend', '<div class="error text-danger">Email không đúng định dạng.</div>');
        isValid = false;
    }

    // Kiểm tra Số điện thoại
    const phonePattern = /^[0-9]{10,11}$/; // Chấp nhận 10-11 chữ số
    if (!phonePattern.test(phone)) {
        const phoneField = document.getElementById('contactPhone');
        phoneField.classList.add('is-invalid');
        phoneField.insertAdjacentHTML('afterend', '<div class="error text-danger">Số điện thoại không hợp lệ (10-11 chữ số).</div>');
        isValid = false;
    }

    // Kiểm tra Địa chỉ phỏng vấn
    if (address === '') {
        const addressField = document.getElementById('interviewAddress');
        addressField.classList.add('is-invalid');
        addressField.insertAdjacentHTML('afterend', '<div class="error text-danger">Địa chỉ không được để trống.</div>');
        isValid = false;
    }

    // Kiểm tra Giới tính
    if (sex === '') {
        const sexField = document.getElementById('sex');
        sexField.classList.add('is-invalid');
        sexField.insertAdjacentHTML('afterend', '<div class="error text-danger">Vui lòng chọn giới tính.</div>');
        isValid = false;
    }

    // Kiểm tra Ngày sinh
    if (dateOfBirth === '') {
        const dobField = document.getElementById('dateOfBirth');
        dobField.classList.add('is-invalid');
        dobField.insertAdjacentHTML('afterend', '<div class="error text-danger">Ngày sinh không được để trống.</div>');
        isValid = false;
    }

    // Kiểm tra Tên trường
    if (schoolName === '') {
        const schoolField = document.getElementById('schoolName');
        schoolField.classList.add('is-invalid');
        schoolField.insertAdjacentHTML('afterend', '<div class="error text-danger">Tên trường không được để trống.</div>');
        isValid = false;
    }

    // Kiểm tra Bằng cấp
    if (degree === '') {
        const degreeField = document.getElementById('degree');
        degreeField.classList.add('is-invalid');
        degreeField.insertAdjacentHTML('afterend', '<div class="error text-danger">Vui lòng chọn bằng cấp.</div>');
        isValid = false;
    }

    // Kiểm tra Ngày bắt đầu và kết thúc học tập
    if (startDate && endDate && new Date(startDate) > new Date(endDate)) {
        const endDateField = document.getElementById('endDate');
        endDateField.classList.add('is-invalid');
        endDateField.insertAdjacentHTML('afterend', '<div class="error text-danger">Ngày kết thúc phải sau ngày bắt đầu.</div>');
        isValid = false;
    }

    // Kiểm tra GPA
    if (gpa && (gpa < 0 || gpa > 4)) {
        const gpaField = document.getElementById('gpa');
        gpaField.classList.add('is-invalid');
        gpaField.insertAdjacentHTML('afterend', '<div class="error text-danger">GPA phải nằm trong khoảng từ 0 đến 4.</div>');
        isValid = false;
    }

    // Kiểm tra Tiêu đề công việc
    if (jobTitle === '') {
        const jobTitleField = document.getElementById('jobTitle');
        jobTitleField.classList.add('is-invalid');
        jobTitleField.insertAdjacentHTML('afterend', '<div class="error text-danger">Tiêu đề công việc không được để trống.</div>');
        isValid = false;
    }

    // Kiểm tra Tên công ty
    if (companyName === '') {
        const companyField = document.getElementById('companyName');
        companyField.classList.add('is-invalid');
        companyField.insertAdjacentHTML('afterend', '<div class="error text-danger">Tên công ty không được để trống.</div>');
        isValid = false;
    }

    // Kiểm tra Ngày bắt đầu và kết thúc công việc
    if (startJobDate && endJobDate && new Date(startJobDate) > new Date(endJobDate)) {
        const endJobDateField = document.getElementById('endJobDate');
        endJobDateField.classList.add('is-invalid');
        endJobDateField.insertAdjacentHTML('afterend', '<div class="error text-danger">Ngày kết thúc phải sau ngày bắt đầu.</div>');
        isValid = false;
    }

    // Kiểm tra Mô tả công việc
    if (jobDescription === '') {
        const jobDescriptionField = document.getElementById('jobDescription');
        jobDescriptionField.classList.add('is-invalid');
        jobDescriptionField.insertAdjacentHTML('afterend', '<div class="error text-danger">Mô tả công việc không được để trống.</div>');
        isValid = false;
    }

    // Ngăn form submit nếu không hợp lệ
    if (!isValid) {
        event.preventDefault();
    }
}

// Gắn sự kiện validate vào form
document.querySelector('form').addEventListener('submit', validateForm);
// Gắn sự kiện vào form
document.getElementById('profileForm').addEventListener('submit', validateForm);

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


    document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');

    form.addEventListener('submit', function(e) {
    e.preventDefault();

    // Lấy giá trị từ form
    const formData = {
    jobTitle: document.getElementById('jobTitle').value,
    jobDescription: document.getElementById('jobDescriptions').value,
    quantity: parseInt(document.getElementById('quantity').value),
    jobRequire: document.getElementById('jobrequirements').value,
    jobBenefit: document.getElementById('benefits').value,
    createDate: new Date(), // Ngày tạo là ngày hiện tại
    endDate: new Date(document.getElementById('deadline').value),
    minSalary: parseFloat(document.getElementById('minSalary').value),
    maxSalary: parseFloat(document.getElementById('maxSalary').value),
    city: document.getElementById('city').value,
    district: document.getElementById('district').value,
    address: document.getElementById('address').value,
    jobCategoryId: document.getElementById('industry').value,
    subCategoryIds: document.getElementById('workType').value // Giả sử ánh xạ với workType
};

    // Validate dữ liệu
    if (!validateForm(formData)) {
    return;
}

    // Gửi dữ liệu đến backend
    fetch('/Company/createJobPost', {
    method: 'POST',
    headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + localStorage.getItem('token')
},
    body: JSON.stringify(formData)
})
    .then(response => {
    if (!response.ok) {
    return response.text().then(errorMsg => {
    throw new Error(errorMsg);
});
}
    return response.json();
})
    .then(data => {
    alert('Đăng tin tuyển dụng thành công!');
    console.log('Kết quả:', data);
    // Có thể chuyển hướng hoặc làm gì đó sau khi tạo thành công
})
    .catch(error => {
    console.error('Lỗi:', error);
    alert('Có lỗi xảy ra: ' + error.message);
});
});

    // Validation function
    function validateForm(data) {
    const errors = [];

    // Validate job title
    if (!data.jobTitle || data.jobTitle.trim() === '') {
    errors.push('Tiêu đề công việc không được để trống');
}

    // Validate job description
    if (!data.jobDescription || data.jobDescription.length > 2000) {
    errors.push('Mô tả công việc không hợp lệ (tối đa 2000 ký tự)');
}

    // Validate quantity
    if (!data.quantity || data.quantity < 1) {
    errors.push('Số lượng tuyển phải lớn hơn 0');
}

    // Validate job requirements
    if (!data.jobRequire || data.jobRequire.length > 2000) {
    errors.push('Yêu cầu công việc không hợp lệ (tối đa 2000 ký tự)');
}

    // Validate job benefits
    if (!data.jobBenefit || data.jobBenefit.length > 2000) {
    errors.push('Quyền lợi không hợp lệ (tối đa 2000 ký tự)');
}

    // Validate salary
    if (data.minSalary < 0 || data.maxSalary < 0 || data.minSalary > data.maxSalary) {
    errors.push('Mức lương không hợp lệ');
}

    // Validate end date
    if (!data.endDate || data.endDate < new Date()) {
    errors.push('Ngày kết thúc không hợp lệ');
}

    // Hiển thị lỗi
    if (errors.length > 0) {
    alert(errors.join('\n'));
    return false;
}

    return true;
}

    // Populate các select options
    populateJobCategories();
    populateWorkTypes();
    populateCities();
});

    function populateJobCategories() {
    const industrySelect = document.getElementById('industry');
    // Gọi API để lấy danh sách ngành nghề
    fetch('/api/jobCategories')
    .then(response => response.json())
    .then(data => {
    data.forEach(category => {
    const option = document.createElement('option');
    option.value = category.id;
    option.textContent = category.categoryName;
    industrySelect.appendChild(option);
});
})
    .catch(error => {
    console.error('Lỗi khi tải danh mục:', error);
});
}

    function populateWorkTypes() {
    const workTypeSelect = document.getElementById('workType');
    // Giả sử ánh xạ với SubCategory
    const workTypes = [
{ id: 1, name: 'Toàn thời gian' },
{ id: 2, name: 'Bán thời gian' },
{ id: 3, name: 'Làm từ xa' },
{ id: 4, name: 'Thực tập' },
{ id: 5, name: 'Hợp đồng' }
    ];

    workTypes.forEach(type => {
    const option = document.createElement('option');
    option.value = type.id;
    option.textContent = type.name;
    workTypeSelect.appendChild(option);
});
}

    function populateCities() {
    const citySelect = document.getElementById('city');
    // Gọi API để lấy danh sách tỉnh/thành phố
    fetch('/api/cities')
    .then(response => response.json())
    .then(data => {
    data.forEach(city => {
    const option = document.createElement('option');
    option.value = city.id;
    option.textContent = city.name;
    citySelect.appendChild(option);
});
})
    .catch(error => {
    console.error('Lỗi khi tải danh sách thành phố:', error);
});
}

    // Xử lý checkbox lương thỏa thuận
    document.getElementById('negotiable').addEventListener('change', function() {
    const minSalary = document.getElementById('minSalary');
    const maxSalary = document.getElementById('maxSalary');

    if (this.checked) {
    minSalary.value = 0;
    maxSalary.value = 0;
    minSalary.disabled = true;
    maxSalary.disabled = true;
} else {
    minSalary.disabled = false;
    maxSalary.disabled = false;
}
});

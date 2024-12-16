document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.toggle-follow').forEach(button => {
        button.addEventListener('click', function () {
            const userId = this.getAttribute('data-user-id');
            const followed = this.getAttribute('data-followed') === 'true';

            // Disable the button to prevent multiple clicks
            this.disabled = true;
            this.textContent = 'Đang xử lý...';

            fetch(`/company/toggle-follow/${userId}`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Không thể cập nhật trạng thái theo dõi.");
                    }
                    return response.text();
                })
                .then(message => {
                    // Update button text after successful response
                    this.textContent = followed ? 'Theo dõi' : 'Hủy theo dõi';
                    this.setAttribute('data-followed', !followed);
                })
                .catch(error => {
                    // Handle error by showing a message and re-enabling the button
                    this.textContent = followed ? 'Theo dõi' : 'Hủy theo dõi';
                    alert(error.message); // Optionally show an alert
                })
                .finally(() => {
                    // Re-enable the button after request
                    this.disabled = false;
                });
        });
    });
});


    document.addEventListener('DOMContentLoaded', function() {
    // Lắng nghe sự kiện khi người dùng nhấn vào nút "Xem"
    const buttons = document.querySelectorAll('button[data-bs-toggle="modal"]');

    buttons.forEach(button => {
    button.addEventListener('click', function() {
    // Lấy ID của profile từ thuộc tính data-id
    const profileId = button.getAttribute('data-id');

    // Gửi yêu cầu Fetch đến server để lấy dữ liệu chi tiết của profile
    fetch(`/company/profile/${profileId}`)
    .then(response => response.json())  // Giả sử API trả về dữ liệu JSON
    .then(data => {
    // Cập nhật nội dung trong modal với dữ liệu nhận được
    const profileInfoContainer = document.getElementById('profileInfo');

    // Giả sử API trả về dữ liệu profile
    profileInfoContainer.innerHTML = `
                            <p><strong>Tên:</strong> ${data.name}</p>
                            <p><strong>Email:</strong> ${data.email}</p>
                            <p><strong>Địa chỉ:</strong> ${data.address}</p>
                            <!-- Thêm các thông tin khác của profile -->
                        `;
})
    .catch(error => {
    console.error('Có lỗi xảy ra khi tải dữ liệu:', error);
});
});
});
});



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




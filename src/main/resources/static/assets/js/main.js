document.addEventListener("DOMContentLoaded", function () {
    lucide.createIcons();

    // Tab navigation submit button
    const submitButton = document.getElementById("submit");
    if (submitButton) {
        submitButton.addEventListener("click", function () {
            const form = document.getElementsByTagName("form")[0];
            if (form) {
                form.submit();
            }
        });
    }
});
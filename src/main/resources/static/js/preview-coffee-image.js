function updatePreview() {
    const imageUrl = document.getElementById("imageUrl").value;
    const imagePreview = document.getElementById("imagePreview");

    if (imageUrl.trim() !== "") {
        imagePreview.src = imageUrl;
        imagePreview.style.display = "block";
    } else {
        imagePreview.style.display = "none";
    }
}

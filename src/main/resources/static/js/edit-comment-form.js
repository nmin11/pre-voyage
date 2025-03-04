function editComment(commentId) {
    let textElement = document.getElementById('comment-text-' + commentId);
    let formElement = document.getElementById('comment-form-' + commentId);

    if (!textElement || !formElement) {
        console.error("One or both elements are missing!");
        return;
    }

    textElement.style.display = 'block';
    formElement.style.display = 'block';
}

function cancelEdit(commentId) {
    document.getElementById('comment-text-' + commentId).style.display = 'inline';
    document.getElementById('comment-form-' + commentId).style.display = 'none';
}

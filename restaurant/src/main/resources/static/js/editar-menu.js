document.addEventListener('DOMContentLoaded', function() {
    if (typeof window.editMode !== 'undefined' && window.editMode) {
        const modal = new bootstrap.Modal(document.getElementById('addItemModal'));
        modal.show();
    }
});
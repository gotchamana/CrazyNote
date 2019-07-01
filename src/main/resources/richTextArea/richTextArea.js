var quill = new Quill('#textarea', {
    modules: {
        toolbar: {
            container: '#toolbar',
            handlers: {
                'image': uploadImage
            }
        },
        syntax: true
    },
    theme: 'snow'
});

function getQuill() {
    return quill;
}

function getContents() {
    var delta = quill.getContents();
    return JSON.stringify(delta);
}

function setContents(delta) {
    quill.setContents(JSON.parse(delta));
}

function uploadImage() {
    var imageURI = textArea.uploadImage();
    var range = quill.getSelection(true);

    quill.deleteText(range.index, range.length, "user");
    quill.insertEmbed(range.index, "image", imageURI, "user");
    quill.setSelection(range.index + 1, "silent");
}

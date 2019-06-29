var toolbarOptions = ['bold', 'italic', 'underline', 'strike', { 'color': [] }, { 'background': [] }, 'clean'];

var quill = new Quill('#editor', {
    modules: {
        toolbar: toolbarOptions
    },
    theme: 'bubble'
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

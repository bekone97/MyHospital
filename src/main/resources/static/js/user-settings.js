$(document).ready(function () {
    $('#userImg').change(function () {
        showImageThumbnail(this);
    });
});

function showImageThumbnail(fileInput) {
    let file = fileInput.files[0];
    let reader = new FileReader();

    reader.onload = function (e) {
        $('.thumbnail').attr('src', e.target.result);
    };

    reader.readAsDataURL(file);
}
document.addEventListener('DOMContentLoaded',()=>{
    let callBackProcessButton = document.getElementById('callbackChangePassword-button');


    let modalProcess = document.getElementById('modalForChangePassword');


    let closeProcessButton = modalProcess.getElementsByClassName('modal__close-button')[0];

    let tagBody = document.getElementsByTagName('body');

    callBackProcessButton.onclick=function (e){
        modalProcess.classList.add('modal_active');
        tagBody.classList.add('hidden');
    }

    closeProcessButton.onclick = function (e){
        modalProcess.classList.remove('modal_active');
        tagBody.classList.remove('hidden');
    }

    modalProcess.onmousedown = function (e) {
        let target = e.target;
        let modalContentMedication = modalProcess.getElementsByClassName('modalForChangePassword_content')[0];
        if (e.target.closest('.' + modalContentMedication.className) === null) {
            modalProcess.classList.remove('modal_active');
            tagBody.classList.remove('hidden');
        }
    }


})
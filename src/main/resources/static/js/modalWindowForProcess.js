document.addEventListener('DOMContentLoaded',()=>{
    let callBackProcessButton = document.getElementById('callbackProcess-button');


    let modalProcess = document.getElementById('modalForProcess');


    let closeProcessButton = modalProcess.getElementsByClassName('modal__closeProcess-button')[0];

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
        let modalContentMedication = modalProcess.getElementsByClassName('modalForProcess_content')[0];
        if (e.target.closest('.' + modalContentMedication.className) === null) {
            modalProcess.classList.remove('modal_active');
            tagBody.classList.remove('hidden');
        }
    }


})
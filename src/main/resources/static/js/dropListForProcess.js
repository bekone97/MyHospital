function dropProcedureListFunction(){
    document.getElementById("myDropdownListForProcedure").classList.toggle("show");
}
function dropOperationListFunction(){
    document.getElementById("myDropdownListForOperation").classList.toggle("show");
}
function dropMedicationListFunction(){
    document.getElementById("myDropdownListForProcess").classList.toggle("show");
}
window.onclick = function (event){
    if(!event.target.matches('.dropbtnForProcess')){
        let dropdowns = document.getElementsByClassName("dropDownList-processContent");
        let i;
        for (i=0;i<dropdowns.length;i++){
            let openDropDownList=dropdowns[i];
            if(openDropDownList.classList.contains('show')){
                openDropDownList.classList.remove('show');
            }
        }
    }
}
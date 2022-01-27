function dropUserListFunction() {
    document.getElementById("dropdownListForUser").classList.toggle("show");
}

function dropAnonymListFunction() {
    document.getElementById("anonymDropdownList").classList.toggle("show");
}

function dropAppointmentListFunction() {
    document.getElementById("dropdownListForAppointment").classList.toggle("show");
}

window.onclick = function (event) {
    if (!event.target.matches('.dropbtnForUser')) {
        let dropdowns = document.getElementsByClassName("dropDownList-contentForUser");
        let i;
        for (i = 0; i < dropdowns.length; i++) {
            let openDropDownList = dropdowns[i];
            if (openDropDownList.classList.contains('show')) {
                openDropDownList.classList.remove('show');
                break;
            }

        }
    }
    if (!event.target.matches('.dropbtnForAppointment')) {
        let appointmentDropdowns = document.getElementsByClassName("dropDownList-contentForAppointment");
        let i;
        for (i = 0; i < appointmentDropdowns.length; i++) {
            let openAppointmentDropDownList = appointmentDropdowns[i];
            if (openAppointmentDropDownList.classList.contains('show')) {
                openAppointmentDropDownList.classList.remove('show');
                break;
            }
        }
    }
}
// window.onclick = function (event){
//     alert('Hui')
//
// }
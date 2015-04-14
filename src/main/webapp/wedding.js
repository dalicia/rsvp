var curGuests = -1;
var numGuestsAllowed = -1;
var numGuests = -1;
var names = '';
var guestAllowed = false;
var showRsvp = function(hideStageLeft){
    toggle(false, true, false, false);
    setActive("rsvpmenu");
    if (hideStageLeft){
        toggleStageLeft();
    }
}
var showForm = function(){
    var rsvpbody = document.getElementById("rsvpbody");
    if (rsvpbody.classList.contains("hidden")){
        setClass("rsvpbody", true);
    }
}

var showInfo = function(hideStageLeft){
    toggle(false, false, true, false);
    setActive("infomenu");
    if (hideStageLeft){
        toggleStageLeft();
    }
}
var showContact = function(hideStageLeft){
    toggle(false, false, false, true);
    setActive("contactmenu");
    if (hideStageLeft){
        toggleStageLeft();
    }
}
/* hides certain items in the UI */
var toggle = function(landing, rsvp, info, contact){
    setClass("choices", true);
    setClass("landing", landing);
    setClass("rsvp", rsvp);
    setClass("rsvpform", rsvp);
    setClass("rsvpconfirm", !rsvp);
    setClass("info", info);
    setClass("contact", contact);
}
var options = ["rsvpmenu", "infomenu", "contactmenu"];
/* Bolds the selected in the non-mobile UI*/
var setActive = function(activeItem){
    for (var i = 0; i < options.length; i++){
        document.getElementById(options[i]).classList.remove("active");
        document.getElementById(options[i] + "StageLeft").classList.remove("active");
    }
    document.getElementById(activeItem).classList.add("active");
    document.getElementById(activeItem + "StageLeft").classList.add("active");
}
var submitRsvp = function(){
    var whoami = document.getElementById("whoami").value;
    var comingyes = document.getElementById("comingyes").checked;
    var comingno = document.getElementById("comingno").checked;
    if (!comingyes && !comingno){
        alert("Before submitting, please select 'YES!' or 'Sorry, can't make it'!");
        return;
    }
    if ("" === whoami){
        alert("Before submitting, please enter your name(s).");
        return;
    }
    var req = new XMLHttpRequest();
    req.open("get", "/rsvp?whoami=" +whoami + "&coming=" + comingyes + "&email=" + document.getElementById("email").value, true);
    req.onreadystatechange = function(){
        if (req.readyState == 4){
            if (req.status == 200){
                var response = JSON.parse(req.response);
                if (response["errorCode"]){
                    alert(response["details"]);
                }else{
                    setClass("rsvpform", false);
                    setClass("rsvpconfirm", true);
                    document.getElementById("rsvpconfirmmsg").innerHTML = numGuests > 0 ? 'Great!' : 'Aw, shucks.';
                    document.getElementById("rsvpmessage").innerHTML = numGuests > 0 ? ("Got it, " + numGuests + " seat(s). We're looking forward to celebrating with you!") :
                       "Thanks for letting us know. Don't worry; we still love you! :-)";
                    updateRsvpCount(response);
                }
            }else{
                alert("Oops! We couldn't record your response because something's wrong with our server. Please try again in a few moments, or email us at xxxx@icloud.com");
            }
        }
    }
    req.send();

}
var showSubmitButton = function(){
    document.getElementById("submitbutton").classList.remove("hidden");
}

var setClass = function(elemName, show){
    var elem = document.getElementById(elemName);
    if (!elem){
        return;
    }
    if(show){
        elem.classList.remove("hidden");
    }else{
        elem.classList.add("hidden");
    }
}

var toggleStageLeft = function(){
    var stageLeft = document.getElementById("viewport");
    if (stageLeft.classList.contains("hideStageLeft")){
        stageLeft.classList.remove("hideStageLeft");
    }else{
        stageLeft.classList.add("hideStageLeft");
    }

}

var curGuests = -1;
var numGuestsAllowed = -1;
var numGuests = -1;
var names = '';
var guestAllowed = false;
var showRsvp = function(hideStageLeft){
    toggle(false, true, false, false);
    //TODO: issue ajax for number
    setActive("rsvpmenu");
    if (hideStageLeft){
        toggleStageLeft();
    }
    if (curGuests != null){ //hide body
        setClass("rsvpbody", false);
        setClass("editbody", true);
    }else{
        setClass("editbody", false);
    }
};
var showForm = function(){
    var rsvpbody = document.getElementById("rsvpbody");
    if (rsvpbody.classList.contains("hidden")){
        setClass("rsvpbody", true);
        setClass("editbody", false);
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
    var yes = document.getElementById("attendyes");
    var no = document.getElementById("attendno");
    var guest = document.getElementById("attendwithguest");
    if (yes.checked){
        if (document.getElementById("numGuests")){
            var guestSelector = document.getElementById("numGuests").value;
            numGuests = parseInt(guestSelector);
        }else{
            numGuests = 1;
        }
    }else if (guest.checked){
        numGuests = 2;
    }else if (no.checked){
        numGuests = 0;
    }else{
        return;
    }
    var req = new XMLHttpRequest();
    req.open("get", "/rsvp?code=" +document.getElementById('code').value + "&numGuests=" + numGuests, true);
    req.onreadystatechange = function(){
        if (req.readyState == 4){
            if (req.status == 200){
                var response = JSON.parse(req.response);
                if (response["errorCode"]){
                    alert(response["details"]);
                }else{
                    setClass("rsvpform", false);
                    setClass("rsvpconfirm", true);
                    document.getElementById("rsvpconfirmmsg").innerHTML = 'Thanks for the RSVP!';
                    updateRsvpCount(response);
                }
            }else{
                alert('Could not rsvp');
            }
        }
    }
    req.send();

}
var submitCode = function(){
    var req = new XMLHttpRequest();
    req.open("get", "/login?code=" +document.getElementById('code').value, true);
    //    req.open("post", "/rsvp", true);
    //    var params = "code=" + document.getElementById("code").value;
    req.onreadystatechange = function(){
        if (req.readyState == 4){
            if (req.status == 200){
                var response = JSON.parse(req.response);
                if (response["errorCode"]){
                    alert(response["details"]);
                }else{
                    names = response.result["name"];
                    document.getElementById("names").innerHTML = names;
                    numGuestsAllowed = response.result["primaryGuests"];
                    guestAllowed = response.result["additionalGuestAllowed"];
                    if (guestAllowed){
                        document.getElementById("guestoption").classList.remove("hidden");
                    }
                    alterPicklist(numGuestsAllowed);
                    updateRsvpCount(response);
                    showRsvp();
                    document.getElementById("headline").classList.remove("hidden");
                }
            }else{
                alert('Could not submit');
            }
        }
    }
    req.send();
    //    req.send(params);
}
var updateRsvpCount = function(response){
    curGuests = response.result["seatsReserved"];
    if (curGuests != null){
        var numActualString = curGuests + " seats";
        if (curGuests == 1){
            numActualString = "one seat";
        }
        document.getElementById("subGreeting").innerHTML = "You're currently confirmed for " + numActualString + ".";
        if (curGuests > 0){
            if (guestAllowed && curGuests == 2){
                document.getElementById("attendwithguest").checked='yes';
            }else{
                document.getElementById("attendyes").checked='yes';
                if (document.getElementById("numGuests")){
                   document.getElementById("numGuests").options[curGuests-1].selected=true;
                }
            }
        }else{
            document.getElementById("attendno").checked='yes';
        }
    }

}

var alterPicklist = function(maxGuests){
    if (maxGuests > 1){
        var newPicklist = " <select id=\"numGuests\">";
        var i = 1;
        for (i ; i <= maxGuests; i++){ //
            newPicklist += "<option value=\"" + i + "\">" + i + "</option>";
        }
        newPicklist += "</select> seat(s).";
        document.getElementById("guestSelector").innerHTML = newPicklist;
        document.getElementById("numGuests").options[i-2].selected=true;
        document.getElementById("subGreeting").innerHTML = "We've saved " + maxGuests + " seats for you at our wedding."
    }else{
        document.getElementById("subGreeting").innerHTML = "We've saved a seat for you at our wedding."
        document.getElementById("guestSelector").innerHTML = "a seat for me.";
    }
    
}
var setClass = function(elemName, show){
    var elem = document.getElementById(elemName);
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

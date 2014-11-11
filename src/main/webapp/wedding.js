var curGuests = -1;
var numGuestsAllowed = -1;
var numGuests = -1;
var names = '';
var showRsvp = function(hideStageLeft){
    toggle(false, true, false, false);
    //TODO: issue ajax for number
    setActive("rsvpmenu");
    if (hideStageLeft){
        toggleStageLeft();
    }
};
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
/* Bolds the selected in the non-mobile UI*/
var setActive = function(activeItem){
    document.getElementById("rsvpmenu").classList.remove("active");
    document.getElementById("infomenu").classList.remove("active");
    document.getElementById("contactmenu").classList.remove("active");
    document.getElementById(activeItem).classList.add("active");
}
var submitRsvp = function(){
    var yes = document.getElementById("attendyes");
    var no = document.getElementById("attendno");
    if (yes.checked){
        var guestSelector = document.getElementById("numGuests").value;
        numGuests = parseInt(guestSelector) + 1;
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
                    numGuestsAllowed = response.result["maxGuests"];
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
    curGuests = response.result["actualGuests"];
    if (curGuests != null){
        var numActualString = curGuests + " spots";
        if (curGuests == 1){
            numActualString = "1 spot";
        }
        document.getElementById("curGuests").innerHTML = "You currently have " + numActualString + " reserved.";
    }

}

var alterPicklist = function(maxGuests){
    if (maxGuests > 1){
        var newPicklist = " and <select id=\"numGuests\">";
        for (var i = 0 ; i < maxGuests; i++){ //2 max guests, then 0, 1
            var displayValue = i;
            if (i == 0){
                displayValue = "no"
            }
            newPicklist += "<option value=\"" + i + "\">" + displayValue + "</option>";
        }
        newPicklist += "</select>";
        newPicklist += (maxGuests == 2 ? "other guest" : "other guests");
        document.getElementById("guestSelector").innerHTML = newPicklist;
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

var numGuestsAllowed = -1;
var names = '';
var showRsvp = function(){
    toggle(false, true, false, false);
    //TODO: issue ajax for number
};
var showInfo = function(){
    toggle(false, false, true, false);
}
var showContact = function(){
    window.open("mailto:aradia@gmail.com");
}
var toggle = function(landing, rsvp, info, contact){
    setClass("choices", true);
    setClass("landing", landing);
    setClass("rsvp", rsvp);
    setClass("info", info);
}
var submitRsvp = function(){
    alert('submit');
}
var submitCode = function(){
    var req = new XMLHttpRequest();
    req.open("get", "/rsvp?code=" +document.getElementById('code').value, true);
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
                    showInfo();
                }
            }else{
                alert('Could not submit');
            }
        }
    }
    req.send();
    //    req.send(params);
}
var setClass = function(elemName, value){
    var elem = document.getElementById(elemName);
    if(value){
        elem.classList.remove("hidden");
    }else{
        elem.classList.add("hidden");
    }
}

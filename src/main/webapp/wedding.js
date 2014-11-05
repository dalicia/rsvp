var curGuests = -1;
var numGuestsAllowed = -1;
var numGuests = -1;
var names = '';
var showRsvp = function(){
    toggle(false, true, false, false);
    //TODO: issue ajax for number
    document.getElementById("rsvpmenu").classList.add("active");
    document.getElementById("infomenu").classList.remove("active");
};
var showInfo = function(){
    toggle(false, false, true, false);
    document.getElementById("infomenu").classList.add("active");
    document.getElementById("rsvpmenu").classList.remove("active");
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
    var req = new XMLHttpRequest();
    req.open("get", "/rsvp?code=" +document.getElementById('code').value + "&numGuests=" + numGuests, true);
    req.onreadystatechange = function(){
        if (req.readyState == 4){
            if (req.status == 200){
                var response = JSON.parse(req.response);
                if (response["errorCode"]){
                    alert(response["details"]);
                }else{
                    //do something
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
                    curGuests = response.result["actualGuests"];
                    if (curGuests != null){
                        document.getElementById("curGuests").innerHTML = "You currently have " + curGuests + " spot(s) reserved";
                    }
                    showInfo();
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
var setClass = function(elemName, value){
    var elem = document.getElementById(elemName);
    if(value){
        elem.classList.remove("hidden");
    }else{
        elem.classList.add("hidden");
    }
}

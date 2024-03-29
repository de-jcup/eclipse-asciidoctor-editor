function tocDisabledReload() {
	currentlocation = window.location.href;
	baseLocation = window.location.href.split(/[?#]/)[0];
	
	storeBodyPos();
	
	if (currentlocation == baseLocation) {
		location.reload(true)
	} else {
		window.location.href=baseLocation
	}
}

function storeBodyPos(){
	var bodyElement = document.getElementsByTagName("body")[0];
	if (typeof sessionStorage == "undefined"){
		// means we got an old browser or not the right to store... so we use
		// old school a cookie...
		document.cookie = bodyElement.scrollTop
	}else{
		sessionStorage.scrollTop =  bodyElement.scrollTop
	}
}

function readBodyPos(){
	if (typeof sessionStorage == "undefined"){
		// means we got an old browser or not the right to store... so we use
		// old school a cookie...
		return document.cookie
	}else{
		return sessionStorage.scrollTop
	}
}

function pageloadEvery(t) {
	
	pos = readBodyPos();
	
	if (typeof pos != "undefined"){
		var bodyElement = document.getElementsByTagName("body")[0];
		bodyElement.scrollTop=pos;
	}
	setTimeout('tocDisabledReload()', t);
	
}

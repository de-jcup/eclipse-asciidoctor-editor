function tocEnabledReload() {
	currentlocation = window.location.href;
	baseLocation = window.location.href.split(/[?#]/)[0];
	
	storePosition();
	
	if (currentlocation == baseLocation) {
		location.reload(true)
	} else {
		/* happens on TOC enabled and link to ancher was used, 
		 * we switch back to baselocation and set cursor location to 
		 * former scroll pos of body, so its okay again*/
		window.location.href=baseLocation
	}
}


function storePosition(){
	const html = document.documentElement;
	if (typeof sessionStorage == "undefined"){
		// means we got an old browser or not the right to store... so we use
		// old school a cookie...
		document.cookie = html.scrollTop
	}else{
		sessionStorage.scrollTop =  html.scrollTop
	}
}

function readPosition(){
	if (typeof sessionStorage == "undefined"){
		// means we got an old browser or not the right to store... so we use
		// old school a cookie...
		return document.cookie
	}else{
		return sessionStorage.scrollTop
	}
}

function pageloadEvery(t) {
	
    // restore position
	pos = readPosition();
	
	if (typeof pos != "undefined") {
		const html = document.documentElement;
		html.scrollTop=pos;
	}
	setTimeout('tocEnabledReload()', t);
	
}

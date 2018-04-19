function smartReload() {
	currentlocation = window.location.href;
	baseLocation = window.location.href.split(/[?#]/)[0];
	
//	var fox= isFirefox();
//	var chrome = isChrome();
//	var explorer = isInternetExplorer();
	
	storeBodyPos();
	
	if (currentlocation == baseLocation) {
		location.reload(true)
	} else {
		/* happens on TOC enabled and link to ancher was used, 
		 * we switch back to baselocation and set cursor location to 
		 * former scroll pos of body, so its okay again*/
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

//function isInternetExplorer(){
//	if((navigator.userAgent.indexOf("MSIE") != -1 ) || (!!document.documentMode == true )) {//IF IE > 10
//		return true
//	}
//	return false;	
//}
//
//function isFirefox(){
//	if(navigator.userAgent.indexOf("Firefox") != -1 ) {
//		return true
//	}
//	return false;	
//}
//
//function isChrome(){
//	if(navigator.userAgent.indexOf("Chrome") != -1 ) {
//		return true
//	}
//	return false;	
//}


function pageloadEvery(t) {
	
	pos = readBodyPos();
	
	if (typeof pos != "undefined"){
		var bodyElement = document.getElementsByTagName("body")[0];
		bodyElement.scrollTop=pos;
	}
	setTimeout('smartReload()', t);
	
}

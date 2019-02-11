import RequestManager from './RequestManager.js';
import BlogEntry from './BlogEntry.js';

const request = new RequestManager();
const baseHref = request.pathArray[request.pathArray.length - 2];
const href = request.pathArray[request.pathArray.length - 1];
let blogEntry = new BlogEntry(request.ctx, baseHref, href);

function processResponse(resp){
	return blogEntry.processResponse(resp)
}
function blogOnLoadEnd(){
    document.querySelector("#loader").classList.add("hide");
    blogEntry.navLinkDecorator(blogEntryLinkHandler);
}
function blogEntryLinkHandler(e){
    document.querySelector("#entry").innerHTML = "";
    document.querySelector("#navContainer").innerHTML = "";
    document.querySelector("#loader").classList.remove("hide");
    document.querySelector("#loader").classList.add("show");
    try {
        e.preventDefault();
        e.stopImmediatePropagation();
    } catch (err) {
        console.log(err);
    }
    const urlHref = this.getAttribute("href");
    window.history.pushState("", "", urlHref);

    const aHref = urlHref.split("/");
    const entryHref = aHref[aHref.length-1];
    
    blogEntry.href = entryHref;
    request.get(blogEntry.getRequestUrl(), processResponse, blogOnLoadEnd);
    scrollToTop(100);
}
function scrollToTop(scrollDuration) {
    var scrollStep = -window.scrollY / (scrollDuration / 15),
        scrollInterval = setInterval(function(){
        if ( window.scrollY != 0 ) {
            window.scrollBy( 0, scrollStep );
        }
        else clearInterval(scrollInterval); 
    },15);
}
document.addEventListener('DOMContentLoaded', 
    request.get(blogEntry.getRequestUrl(), processResponse, blogOnLoadEnd));
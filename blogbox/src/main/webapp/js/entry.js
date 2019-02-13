import RequestManager from './RequestManager.js';
import BlogEntry from './BlogEntry.js';
import Loader from './Loader.js';


const request = new RequestManager();
const baseHref = request.pathArray[request.pathArray.length - 2];
const href = request.pathArray[request.pathArray.length - 1];
let blogEntry = new BlogEntry(request.ctx, baseHref, href);

const entryContainer = document.getElementById("entry");
const navContainer = document.getElementById("navContainer");
let loaderContainer = document.getElementById("loader");
let loader = new Loader();
loaderContainer.parentNode.replaceChild(loader.dots(),loaderContainer);


function processResponse(resp){
   return blogEntry.processResponse(resp)
}
function blogOnLoadEnd(){
    loader.hide();
    navContainer.classList.remove("hide");
    blogEntry.navLinkDecorator(blogEntryLinkHandler);
}
function blogEntryLinkHandler(e){ 
    navContainer.classList.add("hide");
    loader.show();
    entryContainer.innerHTML = "";
    navContainer.innerHTML = "";
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
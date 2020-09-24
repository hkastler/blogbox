import RequestManager from './RequestManager.js';
import BlogEntry from './BlogEntry.js';
import Loader from './Loader.js';


const requestManager = new RequestManager();
const baseHref = requestManager.pathArray[requestManager.pathArray.length - 2];
const href = requestManager.pathArray[requestManager.pathArray.length - 1];
const blogDataCtx = "/blogbox";

let blogEntry = new BlogEntry(blogDataCtx, requestManager.ctx, baseHref, href);

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
    requestManager.get(blogEntry.getRequestUrl(), processResponse, blogOnLoadEnd);
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
    requestManager.get(blogEntry.getRequestUrl(), processResponse, blogOnLoadEnd));
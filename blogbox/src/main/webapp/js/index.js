import Paginator from './Paginator.js';
import RequestManager from './RequestManager.js';
import BlogEntries from './BlogEntries.js';
import Loader from './Loader.js';

const request = new RequestManager();
const paginator = new Paginator(1, 4, 0, request.ctx);
paginator.init(request);
const blogEntries = new BlogEntries(request.ctx,document.getElementById("blogEntries"));
let loader = new Loader();
document.getElementById("loader").parentNode.replaceChild(loader.dots(), document.getElementById("loader"));
let loaderElem = document.getElementById("loader");
loaderElem.classList.add("show");

function processBlogListings(resp) {
    return blogEntries.processResponse(resp);
}
function processPaginator(resp) {
    return paginator.processResponse(resp);
}
function blogOnLoadEnd() {
   loader.hide();
}


function paginatorEventHandler(e) {
    paginator.page = parseInt(this.getAttribute("data-page"));
    if(paginator.page === 0 || paginator.page > paginator.calcNumberOfPages()){
        return;
    }    
    try {
        e.preventDefault();
        e.stopImmediatePropagation();
    } catch (err) {
        console.log(err);
    }
    blogEntries.clearEntries();
    loader.show(); 
    window.history.pushState("", "", this.href);
    request.get(blogEntries.getRequestUrl(paginator.page, paginator.pageSize), processBlogListings, function(){
        paginator.paginate();
        paginatorDecorator(e),
        loader.hide();});
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
function paginatorDecorator() {
    paginator.linkDecorator(paginatorEventHandler);
}

document.addEventListener('DOMContentLoaded',
    request.get(blogEntries.getRequestUrl(paginator.page, paginator.pageSize), processBlogListings, blogOnLoadEnd));
document.addEventListener('DOMContentLoaded',
    request.get(paginator.getRequestUrl(), processPaginator, paginatorDecorator));



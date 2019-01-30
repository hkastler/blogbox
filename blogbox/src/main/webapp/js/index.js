import Paginator from './Paginator.js';
import RequestManager from './RequestManager.js';
import BlogListings from './BlogListings.js';

const request = new RequestManager();
const paginator = new Paginator(1, 4, 0, request.ctx);
paginator.init(request);
const blogListings = new BlogListings(request.ctx);

function processBlogListings(resp) {
    return blogListings.processResponse(resp);
}
function processPaginator(resp) {
    return paginator.processResponse(resp);
}
function blogOnLoadEnd() {
    document.querySelector("#loader").classList.add("hide");
}

function paginatorEventHandler(e) {
    document.querySelector("#loader").classList.remove("hide");
    document.querySelector("#loader").classList.add("show");
    try {
        e.preventDefault();
        e.stopImmediatePropagation();
    } catch (err) {
        console.log(err);
    }
    paginator.page = parseInt(this.getAttribute("data-page"));
    paginator.paginate();
    window.history.pushState("", "", this.href);
    request.get(blogListings.getRequestUrl(paginator.page, paginator.pageSize), processBlogListings, paginatorDecorator);
    blogOnLoadEnd();
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

document.querySelector('#content').addEventListener('load',
    request.get(blogListings.getRequestUrl(paginator.page, paginator.pageSize), processBlogListings, blogOnLoadEnd));
document.querySelector("#content").addEventListener('load',
    request.get(paginator.getRequestUrl(), processPaginator, paginatorDecorator));



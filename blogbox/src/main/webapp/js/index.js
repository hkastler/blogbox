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
document.querySelector('#content').addEventListener('load',
    request.get(blogListings.getRequestUrl(paginator.page, paginator.pageSize), processBlogListings, blogOnLoadEnd));
document.querySelector("#content").addEventListener('load',
    request.get(paginator.getRequestUrl(), processPaginator, paginatorDecorator));

function paginatorDecorator() {
    var anchors = document.querySelectorAll("[id^='paginator'] a");
    for (var i = 0; i < anchors.length; i++) {
        var current = anchors[i];
        current.addEventListener('click', clickHandler);
    }
}

function clickHandler(e) {
    document.querySelector("#loader").classList.remove("hide");
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
}
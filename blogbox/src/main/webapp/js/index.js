import Paginator from './Paginator.js';
import RequestManager from './RequestManager.js';
import BlogListings from './BlogListings.js';

const request = new RequestManager();

const paginator = new Paginator(1,4,0,request.ctx);
paginator.init(request);

const blogListings = new BlogListings(request.ctx);

function processBlogListings(resp){
    return blogListings.processResponse(resp);
}
function processPaginator(resp){
    return paginator.processResponse(resp);
}
function blogOnLoadEnd(){
    document.querySelector("#loader").classList.add("hide");
}
document.querySelector('#content').addEventListener('load', 
        request.get(blogListings.getRequestUrl(paginator.page, paginator.pageSize), processBlogListings, blogOnLoadEnd));
document.querySelector("#content").addEventListener('load', 
        request.get(paginator.getRequestUrl(),processPaginator, function(){}));

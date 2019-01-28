var req = new RequestManager();
var paginator = new Paginator(1,4,0);
paginator.init(req);

var blogListings = new BlogListings(req.ctx);

function processBlogListings(resp){
    return blogListings.processResponse(resp);
}
function processPaginator(resp){
    return paginator.processResponse(resp);
}
function blogOnLoadEnd(){
    //console.log("blogOnLoadEnd");
    document.querySelector("#loader").classList.add("hide");
}
document.querySelector('#content').addEventListener('load', 
        req.get(blogListings.getRequestUrl(paginator.page, paginator.pageSize), processBlogListings, blogOnLoadEnd));
document.querySelector("#content").addEventListener('load', 
        req.get(paginator.getRequestUrl(req.ctx),processPaginator, function(){}));

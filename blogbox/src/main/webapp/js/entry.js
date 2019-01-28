var req = new RequestManager();
var baseHref = req.pathArray[req.pathArray.length - 2];
var href = req.pathArray[req.pathArray.length - 1];
var blogEntry = new BlogEntry(req.ctx, baseHref, href);

function processResponse(resp){
	return blogEntry.processResponse(resp)
}
document.querySelector("#content").addEventListener('load', req.get(blogEntry.getRequestUrl(), processResponse));
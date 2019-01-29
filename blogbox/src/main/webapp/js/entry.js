import RequestManager from './RequestManager.js';
import BlogEntry from './BlogEntry.js';

const request = new RequestManager();
const baseHref = request.pathArray[request.pathArray.length - 2];
const href = request.pathArray[request.pathArray.length - 1];
const blogEntry = new BlogEntry(request.ctx, baseHref, href);

function processResponse(resp){
	return blogEntry.processResponse(resp)
}
function blogOnLoadEnd(){
    document.querySelector("#loader").classList.add("hide");
}
document.querySelector("#content").addEventListener('load', request.get(blogEntry.getRequestUrl(), processResponse, blogOnLoadEnd));
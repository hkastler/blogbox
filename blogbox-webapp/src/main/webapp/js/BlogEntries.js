import BlogEntryDecorator from './BlogEntryDecorator.js';
class BlogEntries{
    constructor(dataCtx, displayCtx, container, path){
        this.dataCtx = dataCtx;
        this.displayCtx = displayCtx;
        this.container = container;
        this.path = path;
        this.decorator = new BlogEntryDecorator(null);
        
    }
    getRequestUrl( page, pageSize ) {
        let schema = "//"
        let baseUrl = location.host + this.dataCtx;
        baseUrl = "www.hkstlr.com/blogbox"
        schema = "https://"
        let restUrl = `${schema}${baseUrl}/rest/srvc/entries/page/${page}/pageSize/${pageSize}`;
        return restUrl;
    }
    
    processResponse(data) {
        this.writeBlogEntries(data);
    }

    clearEntries(){
        while (null != this.container.firstChild && this.container.firstChild) {
            this.container.removeChild(this.container.firstChild);
        }
    }
    
    writeBlogEntries(data) {
        this.clearEntries();
        this.container.classList.add('blogs');
        // loop through the data
        data.forEach((msg, idx) => {
            let listing = document.createElement("div");
            listing.innerHTML = this.blogEntriesHtml(msg, idx);
            this.container.appendChild(listing);
        });
    }
    
    blogEntriesHtml(msg, idx) {
        let msgCtx = this.displayCtx;
        let lPath = this.path;
        if("/" !== msgCtx[msgCtx.length -1]){
            msgCtx += "/";
        }
        
        this.decorator.msg = msg;
        this.decorator.decorate();
        let lMsg = this.decorator.msg;
        
        return `
        <h4 class="mt-4" id="msgSubject-${lMsg.messageNumber}"><a href="${msgCtx}${lPath}/${lMsg.href}">${lMsg.subject}</a></h4>
        <div id="msgCreateDate-${lMsg.messageNumber}">${lMsg.createDate}</div>
        <div id="msgBodyBegin-${lMsg.messageNumber}" class="msg-body-begin">${lMsg.body}</div>        
        `;
    }

    
    
}
export default BlogEntries;
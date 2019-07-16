class BlogEntries{
    constructor(dataCtx, displayCtx, container){
        this.dataCtx = dataCtx;
        this.displayCtx = displayCtx;
        this.container = container;
    }
    getRequestUrl( page, pageSize ) {
        let restUrl = `//${location.host}${this.dataCtx}/rest/srvc/entries/page/${page}/pageSize/${pageSize}`;
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
        if("/" !== msgCtx[msgCtx.length -1]){
            msgCtx += "/";
        }
        return `
        <h4 class="mt-4" id="msgSubject-${msg.messageNumber}"><a href="${msgCtx}entry/${msg.href}">${msg.subject}</a></h4>
        <div id="msgCreateDate-${msg.messageNumber}">${msg.createDate}</div>
        <div id="msgBodyBegin-${msg.messageNumber}" class="msg-body-begin">${msg.body}</div>
        <hr/>
        `;
    }
    
}
export default BlogEntries;
class BlogListings{
    constructor(ctx){
        this.ctx = ctx;
    }
    getRequestUrl( page, pageSize ) {
        let restUrl = `//${location.host}${this.ctx}/rest/srvc/entries/page/${page}/pageSize/${pageSize}`;
        return restUrl;
    }
    
    processResponse(data) {
        this.writeBlogListings(data);
    }
    
    writeBlogListings(data) {
        let container = document.querySelector("#blogListings");
        container.classList.add('blogs');
        // loop through the data
        data.forEach((msg, idx) => {
            let listing = document.createElement("div");
            listing.innerHTML = this.blogListingHtml(msg, idx);
            container.appendChild(listing);
        });
    }
    
    blogListingHtml(msg, idx) {
        return `
        <h4 class="mt-4" id="msgSubject-${msg.messageNumber}">
        <a href="${this.ctx}/entry/${msg.href}">${msg.subject}</a></h4>
        <div id="msgCreateDate-${msg.messageNumber}">${msg.createDate}</div>
        <div id="msgBodyBegin-${msg.messageNumber}">${msg.body}</div>
        `;
    }
    
}
export default BlogListings;
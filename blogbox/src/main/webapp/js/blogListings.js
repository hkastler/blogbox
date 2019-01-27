
var ctx = Blogbox.ctx;

function getRequestUrl(page, pageSize) {
    let restUrl = `//${location.host}${ctx}/rest/srvc/entries/page/${page}/pageSize/${pageSize}`;
    return restUrl;
}

function get(url, callback) {
    let xhr = new XMLHttpRequest();
    xhr.onload = function() {
        let data = JSON.parse(this.responseText);
        callback(data);
    }
    xhr.open("GET" , url);
    xhr.onerror = function (e) {
        console.error(xhr.statusText);
    };
    xhr.send();
}

function processResponse(data) {
    writeBlogListings(data);
}

function writeBlogListings(data) {
    let container = document.querySelector("#blogListings");
    container.classList.add('blogs');
    // loop through the data
    data.forEach((msg, idx) => {
        let listing = document.createElement("div");
        listing.innerHTML = blogListingHtml(msg, idx);
        container.appendChild(listing);
    });
}

function blogListingHtml(msg, idx) {
    return `
    <h4 class="mt-4" id="msgSubject-${msg.messageNumber}">
    <a href="${ctx}/entry/${msg.href}">${msg.subject}</a></h4>
    <div id="msgCreateDate-${msg.messageNumber}">${new Intl.DateTimeFormat('en-US').format(new Date(msg.createDate))}</div>
    <div id="msgBodyBegin-${msg.messageNumber}">${msg.body}</div>
    `;
}
document.querySelector('#content').addEventListener('load', get(getRequestUrl(Blogbox.page, Blogbox.pageSize), processResponse));
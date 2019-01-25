var data = [];
var httpRequest;

function getRequestUrl(page, pageSize) {
    let restUrl = `//${location.host}${ctx}/rest/srvc/entries/page/${page}/pageSize/${pageSize}`;
    return restUrl;
}

function makeRequest() {
    httpRequest = new XMLHttpRequest();
    if (!httpRequest) {
        //alert('Giving up :( Cannot create an XMLHTTP instance');
        return false;
    }
    httpRequest.onreadystatechange = processBlogJson;
    httpRequest.open('GET', getRequestUrl(page, pageSize));
    httpRequest.send();
}

function processBlogJson() {
    if (httpRequest.readyState === XMLHttpRequest.DONE) {
        if (httpRequest.status === 200) {
            data = JSON.parse(httpRequest.responseText);
            writeBlogListings(data);
        } else {
            console.log('There was a problem with the request.');
        }
    }
}

function writeBlogListings(data){
    let container = document.querySelector("#blogListings");
    container.classList.add('blogs');
    // loop through the data
    data.forEach((msg, idx) => {
        let listing =  document.createElement("div");
        listing.innerHTML = createBlogListing(msg, idx);
        container.appendChild(listing);
    });
}

function createBlogListing(msg, idx) {
    return `
    <h4 class="mt-4" id="msgSubject-${msg.messageNumber}">
    <a href="${ctx}/entry/${msg.href}">${msg.subject}</a></h4>
    <div id="msgCreateDate-${msg.messageNumber}">${ new Intl.DateTimeFormat('en-US').format(new Date(msg.createDate))}</div>
    <div id="msgBodyBegin-${msg.messageNumber}">${msg.body}</div>
    `;
}

document.querySelector('#content').addEventListener('load', makeRequest());
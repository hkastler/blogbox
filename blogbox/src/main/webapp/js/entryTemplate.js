var baseHref = Blogbox.pathArray[Blogbox.paLen - 2];
var href = Blogbox.pathArray[Blogbox.paLen - 1];
var ctx = Blogbox.ctx;

function entryHtml(msg) {
    return `<article itemprop="blogPost" itemscope="itemscope" itemtype="https://schema.org/BlogPosting">
                <meta itemprop="mainEntityOfPage" content="/${msg.href}" />
                <a name="top" id="top"></a>
                <div itemprop="name headline">
                    <h3 class="mt-4" id="msgSubject">
                        ${msg.subject}
                    </h3>
                </div>
                <div id="msgCreateDate">
                    <time datetime="${msg.createDate}" itemprop="datePublished">${msg.createDate}</time>
                </div>
                <div id="msgBody" itemprop="articleBody">
                    ${msg.body}
                </div>
            </article>`;
}

function navHtml(prev, next) {
    var navHtml = `<nav aria-label="Navigation" itemscope="itemscope" itemtype="https://schema.org/SiteNavigationElement">
    <div id="nav">`
    if (prev.length != 0) {
        navHtml += navLink(prev[0], prev[1], "prev");
        navHtml += `&lt; ${prev[1].substring(0, 10)}...</a>`;
    }
    if (next.length != 0) {
        navHtml += navLink(next[0], next[1], "next");
        navHtml += `${next[1].substring(0, 10)}... &gt;</a>`;
    }
    navHtml += `</div></nav>`;
    return navHtml;
}

function navLink(href, title, pos) {
    return `<a href="${ctx}/${baseHref}/${href}" class="btn btn-primary" id="nav-${pos}" title="${title}">`;
}

function getRequestUrl() {
    let restUrl = `//${location.host}${ctx}/rest/srvc/entry/${href}/refs`;
    return restUrl;
}

function processResponse(data) {
    entry(data[0], data[1], data[2]);
}

function entry(msg, next, prev) {
    let container = document.querySelector("#entry");
    container.innerHTML = entryHtml(msg);

    container = document.querySelector("#navContainer");
    container.innerHTML = navHtml(prev, next);
}
document.querySelector("#content").addEventListener('load', Blogbox.get(getRequestUrl(), processResponse));
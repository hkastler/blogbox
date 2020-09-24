class BlogEntry {
    constructor(dataCtx, displayCtx, baseHref, href) {
        this.dataCtx = dataCtx;
        this.displayCtx = displayCtx;
        this.baseHref = baseHref;
        this.href = href;
     }

    entryHtml(msg) {
        return `<article aria-label="Article" itemscope="True" itemprop="blogPost" itemtype="https://schema.org/BlogPosting">
                    <a name="top" id="top"></a>
                    <div itemprop="name headline"><h3 class="mt-4" id="msgSubject">${msg.subject}</h3></div>
                    <div id="msgCreateDate">
                        <time datetime="${msg.createDate}" itemprop="datePublished">${msg.createDate}</time>
                    </div>
                    <div id="msgBody" itemprop="articleBody">
                        ${msg.body}
                    </div>
                </article>`;
    }

    navHtml(prev, next) {
        var navHtml = `<nav aria-label="Navigation" itemscope="True" itemtype="https://schema.org/SiteNavigationElement"><div id="nav">`
        if (prev.length != 0 && prev[0] !== null) {
            navHtml += this.navLink(prev[0], prev[1], "prev");
            navHtml += `&lt; ${prev[1].substring(0, 10)}...</a>`;
        }
        if (next.length != 0 && next[0] !== null) {
            navHtml += this.navLink(next[0], next[1], "next");
            navHtml += `${next[1].substring(0, 10)}... &gt;</a>`;
        }
        navHtml += `</div></nav>`;
        return navHtml;
    }

    navLinkDecorator(eventHandler) {
        var as = document.querySelectorAll("[id^='nav-']");
        for (var i = 0; i < as.length; i++) {
            var a = as[i];
            a.addEventListener('click', eventHandler);
        }
    }

    navLink(href, title, pos) {
        return `<a href="${this.displayCtx}/${this.baseHref}/${href}" class="btn btn-primary" id="nav-${pos}" title="${title}">`;
    }

    getRequestUrl() {
        let restUrl = `//${location.host}${this.dataCtx}/rest/srvc/entry/${this.href}/refs`;
        return restUrl;
    }

    processResponse(data) {
        let msg = data[0];
        this.entry(msg);
        let nav = data[1];
        let next = [];
        let prev = [];
        if(nav.length === 2){
            next = nav[0];
            prev = nav[1];
        }else if(nav.length === 1){
            let navMsgNum = nav[0][2];
            let msgNumOfNext = (msg.messageNumber - 1);
            if(navMsgNum === msgNumOfNext){
                next = nav[0];
            }else {
                prev = nav[0];
            }
        }
        this.nav(prev, next);
    }
    entry(msg) {
        let container = document.getElementById("entry");
        container.innerHTML = this.entryHtml(msg);
    }
    nav(prev, next){
        let container = document.getElementById("navContainer");
        container.innerHTML = this.navHtml(prev, next);
    }
}
export default BlogEntry;

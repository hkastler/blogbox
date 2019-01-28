class BlogEntry {
    constructor(ctx, baseHref, href) {
        this.ctx = ctx,
        this.baseHref = baseHref,
        this.href = href
     }

    entryHtml(msg) {
        return `<article itemprop="blogPost" itemscope="itemscope" itemtype="https://schema.org/BlogPosting">
                    <meta itemprop="mainEntityOfPage" content="/${msg.href}" />
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
        var navHtml = `<nav aria-label="Navigation" itemscope="itemscope" itemtype="https://schema.org/SiteNavigationElement"><div id="nav">`
        if (prev.length != 0) {
            navHtml += this.navLink(prev[0], prev[1], "prev");
            navHtml += `&lt; ${prev[1].substring(0, 10)}...</a>`;
        }
        if (next.length != 0) {
            navHtml += this.navLink(next[0], next[1], "next");
            navHtml += `${next[1].substring(0, 10)}... &gt;</a>`;
        }
        navHtml += `</div></nav>`;
        return navHtml;
    }

    navLink(href, title, pos) {
        return `<a href="${this.ctx}/${this.baseHref}/${href}" class="btn btn-primary" id="nav-${pos}" title="${title}">`;
    }

    getRequestUrl() {
        let restUrl = `//${location.host}${this.ctx}/rest/srvc/entry/${this.href}/refs`;
        return restUrl;
    }

    processResponse(data) {
        this.entry(data[0], data[1], data[2]);
    }

    entry(msg, next, prev) {
        console.log(next);
        let container = document.querySelector("#entry");
        container.innerHTML = this.entryHtml(msg);
        container = document.querySelector("#navContainer");
        container.innerHTML = this.navHtml(prev, next);
        console.log(this.navHtml(prev, next));
    }

}


class Paginator {
    constructor(page, pageSize, numberOfItems, ctx, restCtx) {
        this.page = page;
        this.pageSize = pageSize;
        this.numberOfItems = numberOfItems;
        this.ctx = ctx;
        this.restCtx = restCtx;
    }

    calcNumberOfPages() {
        return Math.ceil(this.numberOfItems / this.pageSize);
    }

    hasNextPage() {
        return (this.page * this.pageSize) + 1 <= this.numberOfItems;
    }

    hasPreviousPage() {
        return this.page - 1 > 0;
    }

    init(req) {
        if (location.search.length == 0) {
            if ((req.pathArray.length - 3) > 0) {
                this.page = parseInt(req.pathArray[req.pathArray.length - 3]);
                this.pageSize = parseInt(req.pathArray[req.pathArray.length - 1]);
            }
        } else {
            let requestPage = req.getRequestParameter("page");
            let reqPageSize = req.getRequestParameter("pageSize");
            this.page = (null !== requestPage) ? requestPage : 1;
            this.pageSize = (null !== reqPageSize) ? reqPageSize : 4;
        }
    }

    paginatorLiElem(liClazz, id, href, dataPage, dataPageSize, aClazz, label) {
        let liElem = document.createElement("li");
        liElem.className = liClazz;
        let aElem = document.createElement("a");
        aElem.id = id;
        aElem.href = href;
        aElem.className = aClazz;
        aElem.innerHTML = label;
        aElem.setAttribute("data-page", dataPage);
        aElem.setAttribute("data-pageSize", dataPageSize);
        liElem.appendChild(aElem);
        return liElem ;
    }

    getPaginator(paginatorConfig) {

        let position = paginatorConfig.position
        let outcome = paginatorConfig.outcome;
        let container = document.createElement("nav");
        container.setAttribute("aria-label","Navigation");
        container.setAttribute("itemscope","True");
        container.setAttribute("itemtype","https://schema.org/SiteNavigationElement");
        let pgContainer = document.createElement("ul");
        container.appendChild(pgContainer);
        pgContainer.className = "pagination " + paginatorConfig.pgClassName;
        pgContainer.id = "paginator-" + position;

        let thisPage = parseInt(this.page);
        let prevPage = thisPage - 1;
        let nextPage = thisPage + 1;

        let pageVarStr = "/page/";
        let pageSizeVarStr = "/pageSize/";
        if (window.location.search.length > 0) {
            pageVarStr = "?page=";
            pageSizeVarStr = "&pageSize=";
        }

        //previous
        if (this.calcNumberOfPages() > 1) {
            let prevLink = `${outcome}${pageVarStr}${prevPage}${pageSizeVarStr}${this.pageSize}`
            if (this.hasPreviousPage() === false) {
                prevLink = `#`;
            }
            let backArrow =  this.paginatorLiElem("previous page-item",
                `navback-arrow-${position}`,
                `${prevLink}`,
                `${prevPage}`,
                `${this.pageSize}`,
                "page-link",
                "&larr;");
            let backText = this.paginatorLiElem("previous page-item",
                `navback-text-${position}`,
                `${prevLink}`,
                `${prevPage}`,
                `${this.pageSize}`,
                "page-link d-none d-sm-block",
                "Previous");
            pgContainer.appendChild(backArrow);
            pgContainer.appendChild(backText);
        }//previous

        let dotThreshold = 12;
        //pages or dots
        for (let i = 1; i <= this.calcNumberOfPages(); i++) {

            let iIsPageOrAdjacent = (i === thisPage) || (i === prevPage) || (i === nextPage);

            let showLinkedLi = (this.calcNumberOfPages() < dotThreshold) || (
                (i === 1) || (i === this.calcNumberOfPages()) || iIsPageOrAdjacent
            );
            let idField = `paginatorPage-${i}`;

            if (showLinkedLi) {
                let linkedLi = this.paginatorLiElem(`${thisPage === i ? 'active' : ''} page-item`,
                    `${idField}`,
                    `${outcome}${pageVarStr}${i}${pageSizeVarStr}${this.pageSize}`,
                    `${i}`,
                    `${this.pageSize}`,
                    "page-link",
                    `${i}`);
                pgContainer.appendChild(linkedLi);
            }
            let isDotThreshold = this.calcNumberOfPages() > dotThreshold;
            let isDotShow = (!showLinkedLi && (i === 2 || i === this.calcNumberOfPages() - 1));
            if (isDotThreshold && isDotShow) {
                let dotLi = document.createElement("li");
                dotLi.id = idField;
                dotLi.className = "disabled page-item";
                let dotA = document.createElement("a");
                dotA.className = "disabled";
                dotA.innerHTML = "..";
                dotLi.appendChild(dotA);
                pgContainer.appendChild(dotLi);
            }
        }//pages

        //next
        if (this.calcNumberOfPages() > 1) {
            let nextLink = `${outcome}${pageVarStr}${this.page + 1}${pageSizeVarStr}${this.pageSize}`;
            if (this.hasNextPage() === false) {
                nextLink = `#`;
            }
            let nextText = this.paginatorLiElem(`next page-item`,
                `navForward-Text-${position}`,
                `${nextLink}`,
                `${nextPage}`,
                `${this.pageSize}`,
                "page-link d-none d-sm-block",
                `Next`);
            let nextArrow = this.paginatorLiElem(`next page-item`,
                `navForward-Arrow-${position}`,
                `${nextLink}`,
                `${nextPage}`,
                `${this.pageSize}`,
                "page-link",
                `&rarr;`);
            pgContainer.appendChild(nextText);
            pgContainer.appendChild(nextArrow);
        }

        return container;
    }
    
    paginate() {
        let container = document.querySelector("#pg-top");
        let paginatorConfig = {
            position: "top",
            outcome: this.getOutcome(this.ctx),
            pgClassName: "justify-content-center"
        };
        container.innerHTML = "";
        container.appendChild(this.getPaginator(paginatorConfig));

        container = document.querySelector("#pg-bottom");
        paginatorConfig = {
            position: "bottom",
            outcome: this.getOutcome(this.ctx),
            pgClassName: "justify-content-center"
        };
        container.innerHTML = "";
        container.appendChild(this.getPaginator(paginatorConfig));

    };

    getRequestUrl() {
        let restUrl = `//${location.host}${this.restCtx}/rest/srvc/count`;
        return restUrl;
    }

    processResponse(data) {
        this.numberOfItems = parseInt(data);
        this.paginate();
    }

    getOutcome(ctx) {
        return (window.location.search.length === 0) ? ctx : window.location.pathname;
    }

    linkDecorator(paginatorEventHandler) {
        var as = document.querySelectorAll("[id^='paginator'] a");
        for (var i = 0; i < as.length; i++) {
            var a = as[i];
            a.addEventListener('click', paginatorEventHandler);
        }
    }

}
export default Paginator;


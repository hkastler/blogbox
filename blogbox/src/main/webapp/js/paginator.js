class Paginator {
    constructor(page, pageSize, numberOfItems, ctx) {
        this.page = page;
        this.pageSize = pageSize;
        this.numberOfItems = numberOfItems;
        this.ctx = ctx;
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

    init(req){
        if(location.search.length == 0){
            if ((req.pathArray.length - 3) > 0) {
                this.page = parseInt(req.pathArray[req.pathArray.length - 3]);
                this.pageSize = parseInt(req.pathArray[req.pathArray.length - 1]);
            }
        }else{
            let requestPage = req.getRequestParameter("page");
            let reqPageSize = req.getRequestParameter("pageSize");
            this.page = (null !== requestPage) ? requestPage : 1 ;
            this.pageSize = (null !== reqPageSize) ? reqPageSize : 4 ;
        }
    }

    paginatorLiHtml(liClazz, id, href, dataPage, dataPageSize, aClazz, label){
        return `<li class="${liClazz}"><a id="${id}" href="${href}" data-page="${dataPage}" data-pageSize="${dataPageSize}" class="${aClazz}">${label}</a></li>`;
    }

    getPaginatorHtml(paginatorConfig) {
        let position = paginatorConfig.position
        let outcome = paginatorConfig.outcome;
        let paginatorHtml = `<ul class="pagination justify-content-center" id="paginator-${position}">`

        let thisPage = parseInt(this.page);
        let prevPage = thisPage - 1;
        let nextPage = thisPage + 1;

        let pageVarStr = "/page/";
        let pageSizeVarStr = "/pageSize/";
        if(window.location.search.length > 0){
            pageVarStr = "?page=";
            pageSizeVarStr = "&pageSize=";
        }

        //previous
        if (this.calcNumberOfPages() > 1) {
            let prevLink = `${outcome}${pageVarStr}${prevPage}${pageSizeVarStr}${this.pageSize}`
            if (this.hasPreviousPage() === false) {
                prevLink = `javascript:void(0);`;
            }
            paginatorHtml += this.paginatorLiHtml("previous page-item", 
                                                    `navback-arrow-${position}`,
                                                    `${prevLink}`,
                                                    `${prevPage}`,
                                                    `${this.pageSize}`,
                                                    "page-link",
                                                    "&larr;" );
             paginatorHtml += this.paginatorLiHtml("previous page-item", 
                                                    `navback-text-${position}`,
                                                    `${prevLink}`,
                                                    `${prevPage}`,
                                                    `${this.pageSize}`,
                                                    "page-link d-none d-sm-block",
                                                    "Previous" );
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
                paginatorHtml += this.paginatorLiHtml(`${thisPage === i ? 'active' : ''} page-item`, 
                                    `${idField}`,
                                    `${outcome}${pageVarStr}${i}${pageSizeVarStr}${this.pageSize}`,
                                    `${i}`,
                                    `${this.pageSize}`,
                                    "page-link",
                                    `${i}` );
            }
            let isDotThreshold = this.calcNumberOfPages() > dotThreshold;
            let isDotShow = (!showLinkedLi && (i === 2 || i === this.calcNumberOfPages() - 1));
            if ( isDotThreshold && isDotShow ){
                paginatorHtml += `<li class="disabled page-item" id="${idField}">
                                            <a>
                                                ..
                                            </a>
                                        </li>`
            }
        }//pages

        //next
        if (this.calcNumberOfPages() > 1) {
            let nextLink = `${outcome}${pageVarStr}${this.page + 1}${pageSizeVarStr}${this.pageSize}`;
            if (this.hasNextPage() === false) {
                nextLink = `javascript:void(0);`;
            }
            paginatorHtml += this.paginatorLiHtml(`next page-item`, 
                                    `navForward-Text-${position}`,
                                    `${nextLink}`,
                                    `${nextPage}`,
                                    `${this.pageSize}`,
                                    "page-link d-none d-sm-block",
                                    `Next` );
            paginatorHtml += this.paginatorLiHtml(`next page-item`, 
                                    `navForward-Arrow-${position}`,
                                    `${nextLink}`,
                                    `${nextPage}`,
                                    `${this.pageSize}`,
                                    "page-link",
                                    `&rarr;` );
        }
        paginatorHtml += `</ul>`;

        return paginatorHtml
    }

    paginate(){
        let container = document.querySelector("#pg-top");
        let paginatorConfig = {
            position : "top",
            outcome : this.getOutcome(this.ctx)
        };
        container.innerHTML = this.getPaginatorHtml(paginatorConfig);
    
        container = document.querySelector("#pg-bottom");
        paginatorConfig = {
            position : "bottom",
            outcome : this.getOutcome(this.ctx)
        };
        container.innerHTML = this.getPaginatorHtml(paginatorConfig);
        
    };

    getRequestUrl() {
        let restUrl = `//${location.host}${this.ctx}/rest/srvc/count`;
        return restUrl;
    }

    processResponse(data) {
      console.log(data);
      this.numberOfItems = parseInt(data);
      this.paginate();
    }

    getOutcome(ctx){
        return (window.location.search.length === 0) ? ctx : window.location.pathname;
    }

};
export default Paginator;


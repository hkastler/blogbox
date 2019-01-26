class Paginator {
    constructor(page, pageSize, numberOfItems) {
        this.page = page;
        this.pageSize = pageSize;
        this.numberOfItems = numberOfItems;
        this.numberOfPages = this.calcNumberOfPages();
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

    paginatorLiHtml(liClazz, id, href, dataPage, dataPageSize, aClazz, label){
        return `<li class="${liClazz}"><a id="${id}" href="${href}"
            data-page="${dataPage}"
            data-pageSize="${dataPageSize}" 
            class="${aClazz}">${label}</a></li>`;
    }

    getPaginatorHtml(props) {
        let position = props.get("position");
        let outcome = props.get("outcome");
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
        if (this.numberOfPages > 1) {
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
        for (let i = 1; i <= this.numberOfPages; i++) {

            let iIsPageOrAdjacent = (i === thisPage) || (i === prevPage) || (i === nextPage);

            let showLinkedLi = (this.numberOfPages < dotThreshold) || (
                (i === 1) || (i === this.numberOfPages) || iIsPageOrAdjacent
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
            let isDotThreshold = this.numberOfPages > dotThreshold;
            let isDotShow = (!showLinkedLi && (i === 2 || i === this.numberOfPages - 1));
            if ( isDotThreshold && isDotShow ){
                paginatorHtml += `<li class="disabled page-item" id="${idField}">
                                            <a>
                                                ..
                                            </a>
                                        </li>`
            }
        }//pages

        //next
        if (this.numberOfPages > 1) {
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
};
var paginatorRequest;
function getRequestUrl() {
    var restUrl = `//${location.host}${ctx}/rest/srvc/count`;
    return restUrl;
}

function get(url) {
    paginatorRequest = new XMLHttpRequest();

    if (!paginatorRequest) {
        return false;
    }
    paginatorRequest.onreadystatechange = processResponse;
    paginatorRequest.open('GET', url);
    paginatorRequest.send();
}
var numberOfItems = "0";
function processResponse() {
    if (paginatorRequest.readyState === XMLHttpRequest.DONE) {
        if (paginatorRequest.status === 200) {
            numberOfItems = JSON.parse(paginatorRequest.responseText);
            paginate();
        } else {
            console.log('There was a problem with the request.');
        }
    }
}

function getOutcome(){
    return (window.location.search.length === 0) ? ctx : window.location.pathname;
}

function paginate() {
    let paginator = new Paginator(page, pageSize, parseInt(numberOfItems));
    let container = document.querySelector("#paginator_top");
    let props = new Map();
    props.set("position", "top");
    props.set("outcome", getOutcome());
    container.innerHTML = paginator.getPaginatorHtml(props);

    container = document.querySelector("#paginator_bottom");
    paginatorDiv = document.createElement("div");
    props.set("position", "bottom");
    container.innerHTML = paginator.getPaginatorHtml(props);
}
document.querySelector("#content").addEventListener('load', get(getRequestUrl()));
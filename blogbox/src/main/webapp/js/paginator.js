let Paginator = class {
    constructor(page, pageSize, numberOfItems) {
        this.page = page;
        this.pageSize = pageSize;
        this.numberOfItems = numberOfItems;
        this.numberOfPages = this.calcNumberOfPages();
    }

    calcNumberOfPages() {
        return this.numberOfPages = Math.ceil(this.numberOfItems / this.pageSize);
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

    getPaginatorHtml(position, outcome) {
        let paginatorHtml = `<ul class="pagination justify-content-center" id="paginator-${position}">`

        let thisPage = parseInt(this.page);
        let prevPage = thisPage - 1;
        let nextPage = thisPage + 1;

        //previous
        if (this.numberOfPages > 1) {
            let prevLink = `${outcome}/page/${prevPage}/pageSize/${this.pageSize}`
            if (this.hasPreviousPage() === false) {
                prevLink = `javascript:void(0);`;
            }
            paginatorHtml += this.paginatorLiHtml("previous page-item", 
                                                    `navBack-Arrow-${position}`,
                                                    `${prevLink}`,
                                                    `${prevPage}`,
                                                    `${this.pageSize}`,
                                                    "page-link",
                                                    "&larr;" );
             paginatorHtml += this.paginatorLiHtml("previous page-item", 
                                                    `navBack-Text-${position}`,
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
                                    `${outcome}/page/${i}/pageSize/${this.pageSize}`,
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
            let nextLink = `${outcome}/page/${this.page + 1}/pageSize/${this.pageSize}`;
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

function paginate() {
    let paginator = new Paginator(page, pageSize, parseInt(numberOfItems));
    let container = document.querySelector("#paginator_top");
    container.innerHTML = paginator.getPaginatorHtml("top", "/blog");

    container = document.querySelector("#paginator_bottom");
    paginatorDiv = document.createElement("div");
    container.innerHTML = paginator.getPaginatorHtml("bottom", "/blog");
}
document.querySelector("#content").addEventListener('load', get(getRequestUrl()));
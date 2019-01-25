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

    getPageFirstItem() {
        return this.calcPageFirstItem(this.page, this.pageSize);
    }

    calcPageFirstItem(page, pageSize) {
        var pageFirstItem = ((page * pageSize) - pageSize) + 1;
        if (pageFirstItem < 0) {
            pageFirstItem = 1;
        };
        return pageFirstItem;
    }

    getPageLastItem() {
        return this.calcPageLastItem(this.getPageFirstItem(), this.pageSize, this.numberOfItems);
    }

    calcPageLastItem(firstPageItem, pageSize, numberOfItems) {

        var lastItemIndex = (firstPageItem - 1) + pageSize;
        var count = numberOfItems;

        if (lastItemIndex > count) {
            lastItemIndex = count;
        }
        if (lastItemIndex < 0) {
            lastItemIndex = 0;
        }

        return lastItemIndex;
    }

    hasNextPage() {
        return (this.page * this.pageSize) + 1 <= this.numberOfItems;
    }

    hasPreviousPage() {
        return this.page - 1 > 0;
    }


    getPaginatorHtml(position, fragment, outcome) {
        var paginatorString = `<ul class="pagination justify-content-center" id="paginator-${position}">`

        var thisPage = parseInt(this.page);
        var prevPage = thisPage - 1;
        var nextPage = thisPage + 1;

        //previous
        if (this.numberOfPages > 1) {
            var prevLink = `${outcome}/page/${prevPage}/pageSize/${this.pageSize}`
            if (this.hasPreviousPage() === false) {
                var prevLink = `javascript:void(0);`;
            }
            paginatorString += `
                <li class="previous page-item">
                    <a id="navBack-Arrow-${position}" href="${prevLink}"
                        data-disabled="${this.hasPreviousPage() === false}" 
                        data-fragment="${fragment}" 
                        data-page="${prevPage}"
                        data-pageSize="${this.pageSize}" class="page-link">
                        &larr;
                    </a>
                </li>
                <li class="previous page-item">
                    <a id="navBack-Text-${position}" href="${prevLink}"
                        data-disabled="${this.hasPreviousPage() === false}"
                        data-fragment="${fragment}"
                        data-page="${prevPage}"
                        data-pageSize="${this.pageSize}" class="page-link d-none d-sm-block">
                        Previous
                    </a>
                </li>`
        }//previous

        for (var i = 1; i <= this.numberOfPages; i++) {

            var iIsPageOrAdjacent = (i === thisPage) || (i === prevPage) || (i === nextPage);

            var showLinkedLi = (this.numberOfPages < 13) || (
                (i === 1) || (i === this.numberOfPages) || iIsPageOrAdjacent
            );
            var idField = `paginatorPage-${i}`;

            if (showLinkedLi) {
                paginatorString += `<li class="${thisPage === i ? 'active' : ''} page-item" id="${idField}">
                                        <a href="${outcome}/page/${i}/pageSize/${this.pageSize}"
                                            data-fragment="${fragment}" 
                                            data-page="${i}"
                                            data-pageSize="${this.pageSize}" 
                                            class="page-link">
                                            ${i}
                                        </a>
                                    </li>`;
            }
            var dotThreshold = this.numberOfPages > 12;
            var dotShow = (!showLinkedLi && (i === 2 || i === this.numberOfPages - 1));
            if ( dotThreshold && dotShow ){
                paginatorString += `<li class="disabled page-item" id="${idField}">
                                            <a>
                                                ..
                                            </a>
                                        </li>`
            }
        }

        if (this.numberOfPages > 1) {

            var nextLink = `${outcome}/page/${this.page + 1}/pageSize/${this.pageSize}`;
            if (this.hasNextPage() === false) {
                nextLink = `javascript:void(0);`;
            }

            paginatorString += `<li class="next page-item">
                                    <a id="navForward-Text-${position}" href="${nextLink}" 
                                    data-disabled="${this.hasNextPage() === false}"
                                    data-fragment="${fragment}" 
                                    data-page="${nextPage}" 
                                    data-pageSize="${this.pageSize}"
                                    class="page-link d-none d-sm-block">
                                        Next
                                    </a>
                                </li>
                        
                                <li class="next page-item">
                                    <a id="navForward-Arrow-${position}" href="${nextLink}" 
                                    data-disabled="${this.hasNextPage() === false}"
                                    data-fragment="${fragment}" 
                                    data-page="${nextPage}" 
                                    data-pageSize="${this.pageSize}"
                                    class="page-link">
                                        &rarr;
                                    </a>
                                </li>`;
        }
        paginatorString += `</ul>`;

        return paginatorString
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
    container.innerHTML = paginator.getPaginatorHtml("top", "top", "/blog");

    container = document.querySelector("#paginator_bottom");
    paginatorDiv = document.createElement("div");
    container.innerHTML = paginator.getPaginatorHtml("bottom", "bottom", "/blog");
}
document.querySelector("#content").addEventListener('load', get(getRequestUrl()));
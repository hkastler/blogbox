class RequestManager {
    constructor(){
        this.pathArray = window.location.pathname.split('/');
        this.ctx = "/";
        if (this.pathArray.length > 1) {
            this.ctx = this.ctx.concat(this.pathArray[1]);
        }
    }
    
    get(url, onLoadCallback, onLoadEndCallback) {
        let xhr = new XMLHttpRequest();
        xhr.onload = function () {
            let data = JSON.parse(this.responseText);
            onLoadCallback(data);
        }
        xhr.open("GET", url, true);
        xhr.onloadend = function(){
            onLoadEndCallback();
        }
        xhr.onerror = function (e) {
            console.error(xhr.statusText);
        };
        xhr.send();
    }
    getRequestParameter(parameterName) {
        var result = null,
            tmp = [];
        location.search
            .substr(1)
            .split("&")
            .forEach(function (item) {
                tmp = item.split("=");
                if (tmp[0] === parameterName) result = decodeURIComponent(tmp[1]);
            });
        return result;
    }
}




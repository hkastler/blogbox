var Blogbox = {}
Blogbox.pathArray = window.location.pathname.split('/');
Blogbox.paLen = Blogbox.pathArray.length;
Blogbox.ctx = "/";
if(Blogbox.pathArray.length > 1){
    Blogbox.ctx = Blogbox.ctx.concat(Blogbox.pathArray[1]);
}
Blogbox.page = 1;
Blogbox.pageSize = 4;

let requestPage = findGetParameter("page");
if(null !== requestPage){
    Blogbox.page = requestPage;
    Blogbox.pageSize = findGetParameter("pageSize");
}else{
    if ((Blogbox.paLen - 3) > 0) {
        Blogbox.page = parseInt(Blogbox.pathArray[Blogbox.paLen - 3]);
        Blogbox.pageSize = parseInt(Blogbox.pathArray[Blogbox.paLen - 1]);
    }
}
if (isNaN(Blogbox.pageSize)){
    Blogbox.pageSize = 1;
}
if (isNaN(Blogbox.page)){
    Blogbox.page = 4;
}
function findGetParameter(parameterName) {
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

 Blogbox.get = function(url, callback) {
    let xhr = new XMLHttpRequest();
    xhr.onload = function() {
        let data = JSON.parse(this.responseText);
        callback(data);
    }
    xhr.open("GET" , url, true);
    xhr.onerror = function (e) {
        console.error(xhr.statusText);
    };
    xhr.send();
}
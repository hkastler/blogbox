var pathArray = window.location.pathname.split('/');
var paLen = pathArray.length;
var ctx = "/";
if(pathArray.length > 1){
    ctx = ctx.concat(pathArray[1]);
}
var page = 1;
var pageSize = 4;

var requestPage = findGetParameter("page");
if(null !== requestPage){
    page = requestPage;
    pageSize = findGetParameter("pageSize");
}else{
    if ((paLen - 3) > 0) {
        page = parseInt(pathArray[paLen - 3]);
        pageSize = parseInt(pathArray[paLen - 1]);
    }
}
if (isNaN(pageSize)){
    pageSize = 1;
}
if (isNaN(page)){
    page = 4;
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
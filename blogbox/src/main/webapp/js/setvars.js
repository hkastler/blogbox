var pathArray = window.location.pathname.split('/');
var paLen = pathArray.length;
var ctx = "/";
if(pathArray.length > 1){
    ctx = ctx.concat(pathArray[1]);
}
var page = 1;
var pageSize = 4;
if ((paLen - 3) > 0) {
    page = parseInt(pathArray[paLen - 3]);
    pageSize = parseInt(pathArray[paLen - 1]);
}
if (isNaN(pageSize)) {
    pageSize = 1;
}

if (isNaN(page)) {
    page = 4;
}


var context = "/".concat(pathArray[1]);
var styleSheetLink = document.createElement('link');
styleSheetLink.rel = 'stylesheet';
styleSheetLink.href = 'https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css';
styleSheetLink.type = 'text/css';
var godefer = document.getElementsByTagName('link')[0];
godefer.parentNode.insertBefore(styleSheetLink, godefer);

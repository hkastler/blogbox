class BlogEntryDecorator {
    constructor(msg){
        this.msg = msg;
    }
   

    lintMsgBody(){
        this.msg.body = this.msg.body.replace(/(<br ?\/?>)+/gi, '');
    }

    decorate(){;
        this.lintMsgBody();
    }
}

export default BlogEntryDecorator;
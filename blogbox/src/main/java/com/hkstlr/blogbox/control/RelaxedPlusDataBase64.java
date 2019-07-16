
package com.hkstlr.blogbox.control;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

public class RelaxedPlusDataBase64 extends Whitelist {

    public RelaxedPlusDataBase64() {
        //copied from Whitelist.relaxed()
        addTags("a", "b", "blockquote", "br", "caption", "cite", "code", "col",
                "colgroup", "dd", "div", "dl", "dt", "em", "font", "h1", "h2", "h3", "h4", "h5", "h6",
                "i", "img", "li", "ol", "p", "pre", "q", "small", "strike", "strong",
                "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u",
                "ul");
        
        addAttributes("a", "href", "title");
        addAttributes("blockquote", "cite");
        addAttributes("col", "span", "width");
        addAttributes("colgroup", "span", "width");
        addAttributes("div", "style");
        addAttributes("div", "class");
        addAttributes("img", "align", "alt", "height", "src", "title", "width");
        addAttributes("ol", "start", "type");
        addAttributes("q", "cite");
        addAttributes("table", "summary", "width");
        addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width");
        addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width");
        addAttributes("ul", "type");
        
        addProtocols("a", "href", "ftp", "http", "https", "mailto");
        addProtocols("blockquote", "cite", "http", "https");
        addProtocols("cite", "cite", "http", "https");
        addProtocols("img", "src", "http", "https");
        addProtocols("q", "cite", "http", "https");
        
        addProtocols("img", "src", "http", "https", "data", "cid");
        addProtocols("href", "http", "https", "data", "#");
        
       
        addAttributes("font", "size");
        addAttributes("font", "color");
    }

    protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
        return ("a".equals(tagName)
                && "href".equals(attr.getKey())
                && attr.getValue().startsWith("data:"))
                || super.isSafeAttribute(tagName, el, attr);
    }
}

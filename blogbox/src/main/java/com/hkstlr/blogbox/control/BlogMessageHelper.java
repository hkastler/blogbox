
package com.hkstlr.blogbox.control;

/**
 *
 * @author henry.kastler
 */
public class BlogMessageHelper {

    private BlogMessageHelper() {
        super();
    }

    /**
     * Create replace img tags with &lt;image&gt;
     * for blog browsing
     */
    public static String bodyForBlogEntries(String body) {
        String lBody = body;
        lBody = imagePlaceholder(lBody);
        lBody = pdfPlaceholder(lBody);
        return lBody;
    }
    
    public static String imagePlaceholder(String body){
        String regex = "<img[^>]+>|<img>";
        String replacement = "&lt;image&gt;";
        return body.replaceAll(regex, replacement);
    }
    
    public static String pdfPlaceholder(String body){
        String regex = "<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1";
        String replacement = "<a ";
        return body.replaceAll(regex, replacement);
    }
   

}

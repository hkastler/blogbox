
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
        String regex = "<img[^>]+>|<img>";
        String replacement = "&lt;image&gt;";
        return body.replaceAll(regex, replacement);
    }
    
   

}

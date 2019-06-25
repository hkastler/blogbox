
package com.hkstlr.blogbox.control;

import static com.hkstlr.blogbox.entities.BlogMessage.TITLE_SEPARATOR;

import java.util.StringTokenizer;

import javax.validation.constraints.NotNull;

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
    
    /**
     * Create href for blog, based on title or text
     */
    public static String createHref(@NotNull String str2href, @NotNull Integer numberOfWordsInUrl) {

        // Use title (minus non-alphanumeric characters)
        StringBuilder base = new StringBuilder();
        if (!str2href.isEmpty()) {
            base.append(StringChanger.replaceNonAlphanumeric(str2href, ' ').trim());
        }
       
        if (base.length() > 0) {
            StringTokenizer toker = new StringTokenizer(base.toString());
            StringBuilder tmp = new StringBuilder();
            int count = 0;
            while (toker.hasMoreTokens() && count < numberOfWordsInUrl) {
                String s = toker.nextToken();
                s = s.toLowerCase();
                if (tmp.length() == 0) {
                    tmp.append(s);
                } else {

                    tmp.append(TITLE_SEPARATOR).append(s);
                }
                count++;
            }
            base = tmp;
        }
        return base.toString();
    }

}

package de.jcup.asciidoctoreditor;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A very simple html output parser - works only with asciidoc generated HTML
 * output.
 * 
 * @author albert
 *
 */
public class AsciidoctorHTMLOutputParser {

    // regular expression: <\s*img\s*src\s*=\s*\"([^\"]*)\"
    private static final Pattern pattern = Pattern.compile("<\\s*img\\s*src\\s*=\\s*\\\"([^\\\"]*)\\\"");

    /**
     * Finds all text content inside "<img src=".*"; - 
     * 
     * @param html
     * @return
     */
    public Set<String> findImageSourcePathes(String html) {
        Set<String> pathes = new LinkedHashSet<>();
        if (html==null) {
            return pathes;
        }
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            pathes.add(matcher.group(1));
        }
        return pathes;
    }
}

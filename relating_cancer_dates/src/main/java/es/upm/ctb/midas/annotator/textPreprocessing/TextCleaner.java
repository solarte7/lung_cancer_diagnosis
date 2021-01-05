package es.upm.ctb.midas.annotator.textPreprocessing;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import org.apache.commons.lang.StringEscapeUtils;

public class TextCleaner {
	
	private static String sanitizeText(String s) {

        String sanitize = s;

        sanitize = removeAccents(sanitize);
        sanitize = removeControlCaracters(sanitize);
        sanitize = removeHtmlTags(sanitize);
        sanitize = removeNonLatin(sanitize);
        sanitize = replaceVerticalWhiteSpace(sanitize);
        sanitize = sanitize.trim();

        return sanitize;

    }

    private static String removeAccents(String string) {
        //Somehow not exceptional solution FIXME
        string = string.replace('ñ', '\001');
        string = string.replace('Ñ', '\002');
        string = Normalizer.normalize(string, Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        string = string.replace('\001', 'ñ');
        string = string.replace('\002', 'Ñ');
        return string;
    }

    private static String removeNonLatin(String string) {
        return string.replaceAll("[^\\p{InLatin-1Supplement}\\p{InBasic_Latin}]+", "");
    }

    private static String removeControlCaracters(String string) {
        return string.replaceAll("[\\p{Cntrl}&&\\S]+", "")             //We want to preserve white space
                .replaceAll("[^\\u0000-\\uFFFF]", "\uFFFD");    // Replace all 4 byte characters
    }

    private static String replaceVerticalWhiteSpace(String string) {
        return string.replaceAll("\\v", "\n");//Could be System.lineSeparator()
    }

    private static String removeHtmlTags(String string) {
        string = StringEscapeUtils.unescapeHtml(string);
        string = string.replaceAll("</*\\s*br\\s*/*\\s*>", "\n");//Line breaks to new lines
        return string.replaceAll("<[^>]{1,25}>", "");//Max 25 character for tag name
    }

}

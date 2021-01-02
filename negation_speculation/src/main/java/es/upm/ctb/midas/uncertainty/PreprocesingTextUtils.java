package es.upm.ctb.midas.uncertainty;

import java.text.Normalizer;
import java.util.stream.Stream;

import org.apache.commons.lang.StringEscapeUtils;


public class PreprocesingTextUtils {
	
	 public static String removeAccents(String string) {
	        //Somehow not exceptional solution FIXME
	        string = string.replace('ñ', '\001');
	        string = string.replace('Ñ', '\002');
	        string = Normalizer.normalize(string, Normalizer.Form.NFD)
	                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	        string = string.replace('\001', 'ñ');
	        string = string.replace('\002', 'Ñ');
	        return string;
	    }
	 
	 public static String removeNonLatin(String string) {
	        return string.replaceAll("[^\\p{InLatin-1Supplement}\\p{InBasic_Latin}]+", "");
	 }

	 
	 public static String removeControlCaracters(String string) {
	        return string.replaceAll("[\\p{Cntrl}&&\\S]+", "") 			//We want to preserve white space
	                .replaceAll("[^\\u0000-\\uFFFF]", "\uFFFD");	// Replace all 4 byte characters
	 }

	 private static String replaceVerticalWhiteSpace(String string) {
	        return string.replaceAll("\\v", " ");//Could be System.lineSeparator()
	 }

	 private static String removeHtmlTags(String string) {
	        string = StringEscapeUtils.unescapeHtml(string);
	        string = string.replaceAll("</*\\s*br\\s*/*\\s*>", " ");//Line breaks to new lines
	        return string.replaceAll("<[^>]{1,50}>", "");//Max 25 character for tag name
	 }
	 
	 private static String removeDoubleQuotations (String string) {
		  return string.replace("\"", "");
		 
	 }
	 
	 private static String removeQuotations (String string) {
		 String text =  string.replace("\'", "");
		 return text;
	 }
	 
	 private static String removeAsterisc (String string) {
		 String text =  string.replace("*", "");
		 return text;
	 }
	 
	 public static String cleanText (String  input) {
	        
	     String text= input;

	     text = removeAccents(text);
	     text = removeControlCaracters(text);
	     text = removeHtmlTags(text);
	     text = removeNonLatin(text);
	     text = replaceVerticalWhiteSpace(text);
	     
	     text= removeDoubleQuotations (text);
	     text= removeQuotations (text);
	     text= removeAsterisc(text);
	     text = text.trim();

	           
	     return text;   
	}//end 
	 
	 public static void main(String args []) {
		 String text="\"abcd  abc\' abcd*";
		 text = PreprocesingTextUtils.cleanText(text);
		 System.out.println(text);
		 
		 String x="\"abcd";
		 String z=x.replace("\"", "");
		 System.out.println(z);
	 }

}

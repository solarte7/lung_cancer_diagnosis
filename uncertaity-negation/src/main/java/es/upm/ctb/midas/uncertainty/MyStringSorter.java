package es.upm.ctb.midas.uncertainty;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author kdd
 */
public class MyStringSorter {
    
    public MyStringSorter(){
        
    }
    public ArrayList<String> sortRules(ArrayList<String> unsortedRules) {

		try {
                	// Sort the negation rules by length to make sure
        	        // that longest rules match first.
			String temp = "";
                	for (int i = 0; i<unsortedRules.size()-1; i++) {
        	                for (int j = i+1; j<unsortedRules.size(); j++) {
	                                String a = (String) unsortedRules.get(i);
                                	String b = (String) unsortedRules.get(j);
                                        //System.out.println ("comparando " + a + "  con  " + b);
                                        
                                        
                                        
                        	        if (a.trim().length()<b.trim().length()) {
                	                        // Sorting into descending order by lebgth of string.
                                	        unsortedRules.set(i, b);
                        	                unsortedRules.set(j, a);
                	                }
                                        
        	                }
	                }
		}
		catch (Exception e) {
			System.out.println(e);
                        return null;
		}
		return unsortedRules;
	}//end method
        
        
     public  void sortAscendingLenght(ArrayList<String> strs) {
        strs.sort(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return ( t1.length() - s.length() );
            }
        });
    }
}

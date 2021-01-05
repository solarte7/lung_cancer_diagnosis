package es.upm.ctb.midas.annotator.validation;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files



//import es.upm.ctb.midas.clikes.negation.NegatedPosition;



public class TextFileLoaderTmp {

	
	///======================================================================================================== ////////////////////////
	
	public TextFileLoaderTmp() {		
		
	}
	
	public void readFile(String filePath) {
		ArrayList<String> lines =new ArrayList<String>();
		try {
		      File myObj = new File(filePath);
		      Scanner myReader = new Scanner(myObj);
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();		       
		        lines.add(data);
		        //System.out.println(data);
		      }
		      myReader.close();
		      System.out.println("Filters loaded");
		      
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		
		configureLines(lines);
		
	}
	
	
	
	
public void configureLines(ArrayList list){        
        
        //System.out.print("\n \n \n\n \n  ********************************************************\n \n \n \n");
        
        for (int i=0; i<list.size();i++){
            String   line = (String) list.get(i);
            String line_words[]= line.split("\t");
            //System.out.println ("Line: " + i + "\t"+ line);
           
            String ehr = line_words[0];
            String date  =line_words[2];
            int dif = Math.abs(Integer.parseInt(line_words[3]));
           // System.out.println (ehr +  "    " +  date + "    "  + dif);
            insertData(ehr.trim(), date.trim(), dif);
        } 
    }


public void insertData(String ehr, String date, int dif) {
	MySQLValidation database = new MySQLValidation();
	if (dif<=31) {
			String sql ="UPDATE patient_firstdx_final"
				+ "  SET date = '" + date + "' "  
				+ " WHERE ehr = '" + ehr + "' ";
		database.insertData(sql);
			//System.out.println(sql);
	}
	//database.closeConnection();
}
	
		
	public static void main(String a[]) {
		TextFileLoaderTmp reader=new TextFileLoaderTmp();
		String data= "/home/kdd/resultado.txt";
		
		//reader.readFile(data);
		
	}
}



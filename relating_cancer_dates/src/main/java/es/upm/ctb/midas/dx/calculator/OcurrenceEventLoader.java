package es.upm.ctb.midas.dx.calculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class OcurrenceEventLoader {
	
	public OcurrenceEventLoader() {
	
						
					
	}//end method
	
	public ArrayList<String> loadFile() {
		ArrayList<String> result = new ArrayList<String>();
		try {
			String file = getClass().getClassLoader().getResource("event.txt").getPath();
			
			BufferedReader bf = new BufferedReader(new FileReader(file));
			
			String line="";
			int i=0;
			
				while ((line = bf.readLine())!=null) {
					i++;					
					String word=line.trim();
					word = word.toLowerCase();
					if (word.contains("=") == false){		
						result.add(word);		
						System.out.println (word);
				    }
					
				}
		 }
		catch(Exception e){
			System.out.println ("Error: No se puede abrir el archivo");
		}
		
		return result;
	}//end method
	
	public static void main(String args[]) {
		OcurrenceEventLoader loader = new OcurrenceEventLoader();
		loader.loadFile();
	}

}

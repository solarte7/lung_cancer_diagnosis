package es.upm.ctb.midas.uncertainty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Z_CuesLoaderDict {
	
	public  ArrayList<String> loadCues(String path) {
		 ArrayList<String> dictionaryList = new ArrayList<String>();
		 File file = new File(path);
			try {
				BufferedReader bf = new BufferedReader(new FileReader(file));
				
				String line="";
				int i=0;
				try {
					while ((line = bf.readLine())!=null) {
						i++;
						
						if (line.charAt(0) != '=') {
							  String word= line.trim();						  
							  dictionaryList.add(word);
						
						}
					 }//while
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
			System.out.println("Dictionary succesfully: " + dictionaryList.size());
			
		return dictionaryList;
		
	}
	
	public  HashMap<String, String> loadCuesMap(String path) {
		 HashMap<String, String> map1 = new  HashMap<String, String>();
		 File file = new File(path);
			try {
				BufferedReader bf = new BufferedReader(new FileReader(file));
				
				String line="";
				int i=0;
				try {
					while ((line = bf.readLine())!=null) {
						i++;
						
						if (line.charAt(0) != '=') {
							  String word= line.trim();						  
							  map1.put(word, word);
						
						}
					 }//while
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
			System.out.println("Map Dictionary succesfully: " + map1.size());
			
		return map1;
		
	}

}

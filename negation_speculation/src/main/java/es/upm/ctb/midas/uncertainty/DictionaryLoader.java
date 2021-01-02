package es.upm.ctb.midas.uncertainty;
import java.io.BufferedReader;
import java.io.File;  // Import the File class
import java.io.FileInputStream;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner; // Import the Scanner class to read text files

import cz.cuni.mff.ufal.udpipe.Model;
import cz.cuni.mff.ufal.udpipe.Pipeline;


public class DictionaryLoader {
	HashMap<String, String> map1;
	ArrayList<String> dictionaryList;
	
	public DictionaryLoader() {
		map1 = new HashMap<String, String>();
		dictionaryList = new ArrayList<String>();
	}
	/**
	 * Load file from sources
	 */
	public ArrayList<String>  loadDictionary() {
		 ClassLoader classLoader = getClass().getClassLoader();
		 File file = new File(classLoader.getResource("entries.txt").getFile());
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
		
	}//end method
	
	public Pipeline loadUDPipe() {
		ClassLoader classLoader = getClass().getClassLoader();
		String input="horizontal";
		String output="matxin";
		String path = classLoader.getResource("spanish-ancora-ud-2.1-20180111.udpipe").getFile();
		Model model = Model.load(path);
		System.out.println("UDpipe Model Loaded");
		Pipeline pipeline = new Pipeline(model, input, Pipeline.getDEFAULT(), Pipeline.getDEFAULT(), output);
			
		return pipeline;
	}
	
		
	/*	
	public void sortDictionary() {
		ArrayList <String> list1 = new ArrayList<String>();
		MyStringSorter sorter = new MyStringSorter();
		
		//sacamos la lista de terminos del mapa
		for (String key : dictionaryList) {
		    				    
	    }//for
		//printDictionary (list1);
		ArrayList <String> orderedList = sorter.sortRules(list1);
		printDictionary(orderedList);
	}//end */
	
	/**
	 * print
	 * @param list
	 */
	public void printList () {
		System.out.println("\n ==============================================");
		for(String term: dictionaryList) {
			System.out.println(term.toLowerCase().trim());
		}//for
		System.out.println("\n ==============================================");
	}
	
	public static void main(String a[]) throws FileNotFoundException, IOException {
		String file1= "/home/kdd/uncertanty_spanish.txt";
		DictionaryLoader loader = new DictionaryLoader();
		
		
		loader.loadDictionary();		
		loader.printList();
		
		//loader.sortDictionary();
		//loader.loadUDPipe();
	}

}

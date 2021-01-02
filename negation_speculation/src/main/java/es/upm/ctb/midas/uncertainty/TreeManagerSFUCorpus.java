package es.upm.ctb.midas.uncertainty;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TreeManagerSFUCorpus {
	String head="";
	public TreeManagerSFUCorpus() {
		// TODO Auto-generated constructor stub	
		head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	}
	
	//load
	public void loadFile() {
		 File xml= new File("/home/kdd/Descargas/corpus_SFU/SFU/coches/no_1_3.tbf.xml");
		NodeList nodeList1;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			//Document documento = builder.parse(new File("concesionario.xml"));
			Document documento = builder.parse(xml);
			NodeList nodeList = documento.getElementsByTagName("sentence");
			
			
            System.out.println("Elemento raiz:" + documento.getDocumentElement().getNodeName());
            
            //Recorre las sentencias
            for (int i = 0; i < nodeList.getLength(); i++) {
            	String sentence1="";
                Node nodo = nodeList.item(i); 
                System.out.println("\n " );
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                	System.out.println("name " + nodo.getNodeName());
                	Element e = (Element) nodo;
                	
                	 NodeList hijos = e.getChildNodes();
                	
                	 //Recorre los nodos de cada sentencia
                	 for (int j = 0; j < hijos.getLength(); j++) {
                         // Obtengo al hijo actual
                         Node hijo = hijos.item(j);
                         // Compruebo si es un nodo
                         if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                             // Muestro el contenido
                             System.out.println("Propiedad: " + hijo.getNodeName() + ", Valor: " + hijo.getTextContent());
                            
                            if (hijo.getNodeName().length() <= 3){
                             //hijo.getUserData(arg0)
	                            try {
		                            NamedNodeMap map1= hijo.getAttributes();
		                            Node nodexc = map1.getNamedItem("wd");
		                            System.out.println("nieto: " + nodexc.getNodeName() + ", Valor: " + nodexc.getTextContent());
		                            sentence1 = sentence1 + " "  + nodexc.getTextContent();
	                            }
	                            catch(Exception exp) {
	                            	
	                            }
                            }
                            else {
                            	//Sentencias negadas
                            	NodeList negatedList = hijo.getChildNodes();
                            	
                           	 //Recorre los nodos de cada sentencia
                           	 	for (int jj = 0; jj < negatedList.getLength(); jj++) {
                                    // Obtengo al hijo actual
                                    Node hijoN =negatedList.item(jj);
                                    System.out.println ("jj " + hijoN.getNodeName());
                                    /*
                                    if (hijoN.getNodeName().length() < 3){
                                    	
                                    	sentence1 = sentence1 + " "  + hijoN.getTextContent();
                                    }
                                    else {
                                    	NodeList scopedList = hijoN.getChildNodes();
                                    	
                                    	for (int jjj = 0; jjj < scopedList.getLength(); jjj++) {
                                            // Obtengo al hijo actual
                                            Node hijoScope =scopedList.item(jjj);
                                            
                                            if (hijoScope.getNodeName().length() <= 3){
                                            	sentence1 = sentence1 + " "  + hijoScope.getTextContent();
                                            }
                                            else {
                                            	NodeList eventList = hijoScope.getChildNodes();
                                               	for (int zz = 0; zz < scopedList.getLength(); zz++) {
                                               		Node hijoEvent =eventList .item(zz);
                                               		
                                               		if (hijoEvent.getNodeName().length() <= 3){
                                                    	sentence1 = sentence1 + " "  + hijoEvent.getTextContent();
                                                    }
                                               	}
                                            }
                                     } 
                                    }*/
                                    	
                                }
                            	 
                            }
                             
                         }
               
                    }//for
                	 System.out.println("Sentence: " +  sentence1 + "\n");
                }
                
            }

		     
		} catch (Exception ex1) {
			System.out.println ("Error Parsing XML. -> TreeManager.java");
			System.out.println (ex1.getMessage());
		}
   
	

 }
	
	public ArrayList <String> loadFile2(String fileName) {
		  File archivo = null;
	      FileReader fr = null;
	      BufferedReader br = null;
	      ArrayList <String> sentences = new  ArrayList  <String>();
	      

	      try {
	         // Apertura del fichero y creacion de BufferedReader para poder
	         // hacer una lectura comoda (disponer del metodo readLine()).
	         archivo = new File (fileName);
	         fr = new FileReader (archivo);
	         br = new BufferedReader(fr);

	         // Lectura del fichero
	         String linea;
	         String sent="";
	         int i=0;
	         while((linea=br.readLine())!=null) {
	            
	         if (linea.contains("<sentence")) {
	        	 //System.out.println(linea);
	        	 i++;
	        	 sent=  i + "==> ";
	        	 if (linea.contains("<sentence complex=")) {
	        		 sent= "NEG " + i + "==> " ;
	        	 }
	        	 
	         }
	        	         
	         if (linea.contains("</senten")) {
	        	 //System.out.println(linea);
	        	 sentences.add(sent);
	        	 System.out.println(sent);
	        	 sent="";
	        	 
	        	 
	         }
	         
	         if (linea.contains("wd=")) {
	        	 String a[]= linea.split("wd=");
	        	// System.out.println(linea);
	        	 String temp= a[1].replaceAll("\"", "");
	        	 temp = temp.replace("/","");
	        	 temp = temp.replaceAll(">","");
	        	 temp = temp.trim();
	        	 sent = sent + " " + temp;
	         }
	       }
	            
	      }
	      catch(Exception e){
	         e.printStackTrace();
	      }finally{
	         // En el finally cerramos el fichero, para asegurarnos
	         // que se cierra tanto si todo va bien como si salta 
	         // una excepcion.
	         try{                    
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	         }
	      }
	      
	      return sentences;
	}
	
	public static void main(String a[]) {
		TreeManagerSFUCorpus manager  = new TreeManagerSFUCorpus();
		manager.loadFile2("/home/kdd/Descargas/corpus_SFU/SFU/coches/no_1_6.tbf.xml");
	}
	
}

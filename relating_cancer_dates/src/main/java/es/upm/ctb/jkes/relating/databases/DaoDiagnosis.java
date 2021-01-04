package es.upm.ctb.jkes.relating.databases;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import es.upm.ctb.jkes.relating.Annotation;


public class DaoDiagnosis {

	MySQLPatient mysqlConnection;
	
	 public DaoDiagnosis(){
	    mysqlConnection = new MySQLPatient();
	    	       
	 }
	 
	 public ArrayList <Integer> getSentences() {
		 
	     ArrayList <Integer>result= new ArrayList<Integer>();
		 String sql_select="select distinct sentence_id from patient_diagnosis_dates"; 
		 try{
        	Connection conn =mysqlConnection.getConnection();   
            Statement sentencia = conn.createStatement();
            ResultSet rs = sentencia.executeQuery(sql_select);
            //
            
            while(rs.next()){
            	TextDocument doc = new TextDocument();
            	int id=rs.getInt(1) ;             
                
                result.add(id);
                //System.out.println (id);
             }
		 }
           catch(Exception e) {
        	   System.out.println ("Error accessing paatient database, check connection");
           }
		 return result;
	 
	 }//end
	 
 public ArrayList <Annotation> getSentenceAnnotations(int sentenceId) {
		 
	     ArrayList <Annotation>result= new ArrayList<Annotation>();
		 
	     String sql_select=" select id, ehr, document_id, document_type, category, sentence, " +
	                       " entity, entity_value, normalized, negated, speculated " +
	                       " from patient_diagnosis_dates " +
	                       " where sentence_id = " + sentenceId +
	                       " AND length(negated)<1 and length(speculated)<1";
	                       
	                       
				     
		 try{
        	Connection conn =mysqlConnection.getConnection();   
            Statement sentencia = conn.createStatement();
            ResultSet rs = sentencia.executeQuery(sql_select);
            //
            
            while(rs.next()){
            	Annotation ann = new Annotation();
            	
            	int id=rs.getInt(1) ;    
            	int ehr=rs.getInt(2) ;
            	int docId= rs.getInt(3);
            	String docType= rs.getString(4);
            	String category= rs.getString(5);
            	String sentence= rs.getString(6);
            	String entity= rs.getString(7);
            	String entityValue= rs.getString(8);
            	String normalized = rs.getString(9);
            	String negated = rs.getString(10);
            	String speculated = rs.getString(11);
            	
            	ann.setId(id);
            	ann.setDocumentId(docId);
            	ann.setEhr(ehr);
            	ann.setDocumentType(docType);
            	ann.setCategory(category);
            	ann.setSentence(sentence);
            	ann.setEntity(entity);
            	ann.setEntityValue(entityValue);
            	ann.setNormalized(normalized);
            	
            	if (negated.trim().length()<1 && speculated.trim().length()<1) {
            		result.add(ann);
            	}
                
                //System.out.println (id);
             }
		 }
           catch(Exception e) {
        	   System.out.println ("Error accessing paatient database, check connection");
           }
		 return result;
	 
	 }//end
	   	 
	//======================================================================================================    
	    public ArrayList <TextDocument> getDocuments(int ehr) {
	    	String sql_select;
	    	sql_select="SELECT id, ehr, category, date, text FROM  document where  ehr = " + ehr ; 
	    	ArrayList <TextDocument> list1 = new  ArrayList <TextDocument>();
	    	
	    	
	        //System.out.println("SQL= " + sql_select);
	         try{
	        	 Connection conn =mysqlConnection.getConnection();   
	            Statement sentencia = conn.createStatement();
	            ResultSet tabla = sentencia.executeQuery(sql_select);
	            //
	            
	            while(tabla.next()){
	            	TextDocument doc = new TextDocument();
	            	String id=tabla.getString(1) + "_" + tabla.getInt(2) +  "_" + tabla.getString(3)+ "_" + tabla.getString(4);
	                doc.setId(id);
	                doc.setCreationDate(tabla.getString(4));
	                doc.setTextDoc(tabla.getString(5));
	                
	                list1.add(doc);
	                
	             }
	   
	            
	         }
	         catch(SQLException e){ 
	        	 
	        	 System.out.println(e); 
	        	 
	        }
	         catch(Exception e){ 
	        	 System.out.println(e); 
	        	 
	        }
	         
	         return list1;  
	}//end
			
	
   
	  //======================================================================================================    
	    public ArrayList <TextDocument> getDocuments(String type, int cod1, int cod2 ) {
	    	String sql_select;
	    	
	    	
	    	sql_select="SELECT id, texto_normalizado, estado  FROM  document WHERE  tipo = '" + type +"' "  + 
	    	" AND codigo >= " +  cod1 + " AND codigo <" + cod2; 
	    	ArrayList <TextDocument> list1 = new  ArrayList <TextDocument>();
	    	
	    	
	        //System.out.println("SQL= " + sql_select);
	         try{
	        	Connection conn =mysqlConnection.getConnection();   
	            Statement sentencia = conn.createStatement();
	            ResultSet tabla = sentencia.executeQuery(sql_select);
	            //
	            
	            while(tabla.next()){
	            	TextDocument doc = new TextDocument();
	            	String id=tabla.getString(1) + "_" + type+ "_" + tabla.getInt(3);
	                doc.setId(id);
	                //doc.setCreationDate(new Date().toString());
	                doc.setTextDoc(tabla.getString(2));	                
	                list1.add(doc);
	                
	             }
	            System.out.println("Querying " + list1.size() +  " documents"); 
	            
	         }
	         catch(SQLException e){ 
	        	 
	        	 System.out.println(e); 
	        	 
	        }
	         catch(Exception e){ 
	        	 System.out.println(e); 
	        	 
	        }
	         
	         return list1;  
	}//end
			    
	    
	    
	
	    
	    public ArrayList <Integer> getEhrs() {
	     	
	    	//String sql_select ="SELECT distinct ehr from document where ehr > 509248 order by ehr";
	    	String sql_select ="SELECT distinct ehr from document order by ehr desc";
	    	ArrayList <Integer> list1 = new  ArrayList <Integer>();
	    	
	    	
	        //System.out.println("SQL= " + sql_select);
	         try{
	        	 Connection conn =mysqlConnection.getConnection();   
	            Statement sentencia = conn.createStatement();
	            ResultSet tabla = sentencia.executeQuery(sql_select);
	            //
	            int i=0;
	            
	            while(tabla.next()){
	            	TextDocument doc = new TextDocument();
	            	int data = tabla.getInt(1);
	                list1.add(data);
	                i++;
	                System.out.println(i + ": " + data);
	                
	             }
	   
	            
	         }
	         catch(SQLException e){ 
	        	 
	        	 System.out.println(e); 
	        	 
	        }
	         catch(Exception e){ 
	        	 System.out.println(e); 
	        	 
	        }
	         
	         return list1;  
	}//end

	    
	 /*
	  * ================================================================================================
	  * ================================================================================================
	  * 
	  */
		
	    public ArrayList <TextDocument> getOneDocument(int idDoc) {
	    	String sql_select;
	    	//sql_select="SELECT id, ehr, text  FROM  document where text like '%Carboplatino%' or  text like '%cisplatino%' limit 10;";
	    	//sql_select="SELECT id, ehr, category, date, text  FROM  document where length (text) >= 5 and text like '%fumador%' limit 50"; 
	    	sql_select="SELECT id, ehr, category, date, text FROM  document where   id = " + idDoc; 
	    	ArrayList <TextDocument> list1 = new  ArrayList <TextDocument>();
	    	
	    	
	        //System.out.println("SQL= " + sql_select);
	         try{
	        	 Connection conn =mysqlConnection.getConnection();   
	            Statement sentencia = conn.createStatement();
	            ResultSet tabla = sentencia.executeQuery(sql_select);
	            //
	            
	            while(tabla.next()){
	            	TextDocument doc = new TextDocument();
	            	String id=tabla.getString(1) + "_" + tabla.getInt(2) +  "_" + tabla.getString(3)+ "_" + tabla.getString(4);
	                doc.setId(id);
	                doc.setCreationDate(tabla.getString(4));
	                doc.setTextDoc(tabla.getString(5));
	                
	                list1.add(doc);
	                
	             }
	   
	            
	         }
	         catch(SQLException e){ 
	        	 
	        	 System.out.println(e); 
	        	 
	        }
	         catch(Exception e){ 
	        	 System.out.println(e); 
	        	 
	        }
	         
	         return list1;  
	}//end
	    
	    
	    
	public ArrayList <Integer> getDocumentsId(int ehr ) {
	    	 
	    	String sql_select = "SELECT distinct id from document where ehr ="  + ehr;
	    	ArrayList <Integer> list1 = new  ArrayList <Integer>();
	    	
	    	
	        //System.out.println("SQL= " + sql_select);
	         try{
	        	 Connection conn =mysqlConnection.getConnection();   
	            Statement sentencia = conn.createStatement();
	            ResultSet tabla = sentencia.executeQuery(sql_select);
	            //
	            int i=0;
	            
	            while(tabla.next()){
	            	TextDocument doc = new TextDocument();
	            	int data = tabla.getInt(1);
	                list1.add(data);
	                i++;
	                //System.out.println(i + ": " + data);
	                
	             }
	   
	            
	         }
	         catch(SQLException e){ 
	        	 
	        	 System.out.println(e); 
	        	 
	        }
	         catch(Exception e){ 
	        	 System.out.println(e); 
	        	 
	        }
	         
	         return list1;  
	}//end
 
	    
   //=======================================================================================================
	
	    
	public ArrayList <String> getAllDocumentsIds() {
	    	 ArrayList <String> list1 = new  ArrayList <String>();
	    	String sql_select;
	        sql_select="select distinct sentence_id  from documents";
	        System.out.println("SQL" + sql_select);
	         try{
	        	 Connection conn =mysqlConnection.getConnection(); 
	            Statement sentencia = conn.createStatement();
	            ResultSet tabla = sentencia.executeQuery(sql_select);
	            //
	            while(tabla.next()){
	            	              
	              list1.add(tabla.getString(1));
	              
	            }
	            
	         }
	         catch(SQLException e){ 
	        	 
	        	 System.out.println(e); 
	        	 
	        }
	         catch(Exception e){ 
	        	 System.out.println(e); 
	        	 
	        }
	         
	         return list1;  
   }
	    
	
	/**
	 * Load a map of documents
	 * @return
	 */
	public HashMap<Integer, TextDocument> loadDocumentsMap() {
    	String sql_select;
    	sql_select="SELECT distinct id, texto, estado, tipo FROM document ORDER BY id" ; 
    	HashMap<Integer, TextDocument> map1 = new  HashMap<Integer, TextDocument>();
    	   	
    	
        //System.out.println("SQL= " + sql_select);
         try{        	 
	        	Connection conn =mysqlConnection.getConnection();           
	            Statement sentencia = conn.createStatement();
	            ResultSet tabla = sentencia.executeQuery(sql_select);
            //
            
            while(tabla.next()){
            	TextDocument doc = new TextDocument();
            	int key = tabla.getInt(1);            
            	doc.setUnivalleId(key);
            	
            	doc.setTextDoc(tabla.getString(2));
            	doc.setEstado(tabla.getString(3));
            	doc.setTipo(tabla.getString(4));
                
            	    
                map1.put(key, doc);
                //System.out.println("key = " + key + "  " + doc.getTipo());
                
             }
   
            
         }
         catch(SQLException e){ 
        	 
        	 System.out.println(e); 
        	 
         }
         catch(Exception e){ 
        	 System.out.println(e); 
        	 
         }
         
         return map1;  
	}//end
	
	
	
	public void closeConnection() {
		 mysqlConnection.closeConnection();
	 }
	 

	    public static void main(String a[]) {
	    		    	
	    	DaoDiagnosis dao = new DaoDiagnosis();
	    	//dao.loadDocumentsMap();
	    	dao.getSentences();
	    	
	    }

	}//fin clase



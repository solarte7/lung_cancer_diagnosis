package es.upm.ctb.jkes.relating.databases;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import es.upm.ctb.jkes.relating.Annotation;
import es.upm.ctb.jkes.relating.Link;



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
        	   System.out.println ("Error accessing the Patient database [check connection]");
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
        	   System.out.println ("Error accessing the patient database, check connection");
           }
		 return result;
	 
	 }//end
 
 
 public ArrayList <String> getEvents() {
	 
     ArrayList <String>result= new ArrayList<String>();
	 String sql_select=" SELECT  DISTINCT entity_value from patient_diagnosis_dates" +
                        " WHERE entity = 'event'" ; 
	 try{
    	Connection conn =mysqlConnection.getConnection();   
        Statement sentencia = conn.createStatement();
        ResultSet rs = sentencia.executeQuery(sql_select);
        //
        
        while(rs.next()){
        	TextDocument doc = new TextDocument();
        	String event =rs.getString(1) ;             
            
            result.add(event);
            //System.out.println (id);
         }
	 }
       catch(Exception e) {
    	   System.out.println ("Error accessing the patient database, check connection");
       }
	 return result;
 
 }//end
 
 
 public ArrayList <String> getPatients() {
	 
     ArrayList <String>result= new ArrayList<String>();
	 String sql_select=" SELECT  DISTINCT ehr from patients" ;
                       
	 try{
    	Connection conn =mysqlConnection.getConnection();   
        Statement sentencia = conn.createStatement();
        ResultSet rs = sentencia.executeQuery(sql_select);
        //
        
        while(rs.next()){
        	TextDocument doc = new TextDocument();
        	String event =rs.getString(1) ;             
            
            result.add(event);
            //System.out.println (id);
         }
	 }
       catch(Exception e) {
    	   System.out.println ("Error accessing the patient database, check connection");
       }
	 return result;
 
 }//end
 
public ArrayList <String> getPriorityNotes() {
	 
     ArrayList <String>result= new ArrayList<String>();
	 String sql_select=" SELECT  DISTINCT category from clinial_notes_prority" ;
                       
	 try{
    	Connection conn =mysqlConnection.getConnection();   
        Statement sentencia = conn.createStatement();
        ResultSet rs = sentencia.executeQuery(sql_select);
        //
        
        while(rs.next()){
        	
        	String category =rs.getString(1) ;             
            
            result.add(category);
            //System.out.println (id);
         }
	 }
       catch(Exception e) {
    	   System.out.println ("Error accessing the patient database, check connection");
       }
	 return result;
 
 }//end


public ArrayList <String> getPriorityUMLS() {
	 
    ArrayList <String>result= new ArrayList<String>();
	 String sql_select=" SELECT  DISTINCT umls from clinial_umls_codes" ;
                      
	 try{
   	Connection conn =mysqlConnection.getConnection();   
       Statement sentencia = conn.createStatement();
       ResultSet rs = sentencia.executeQuery(sql_select);
       //
       
       while(rs.next()){
       	
       	String category =rs.getString(1) ;             
           
           result.add(category);
           //System.out.println (id);
        }
	 }
      catch(Exception e) {
   	   System.out.println ("Error accessing the patient database, check connection");
      }
	 return result;

}//end
 
 public void saveLinks(ArrayList<Link> links) {	 

     for (Link link: links) {
    	 String sql = "INSERT INTO links (id, sentence_id, sentence, cancer_entity, date, event, normalized)  " + 
   		      "  VALUES (";
    	 
    	sql= sql + link.getId()+ ", ";
 	    sql= sql + link.getSentenceId()+ ", '";
 	    sql= sql + link.getSentence ()+ "', '";
 	    sql= sql + link.getCancerEntity() + "', '";
 	    sql= sql + link.getDate() + "', '";
 	    sql= sql + link.getEvent() + "', ";
 	    sql= sql + link.getNormalized() + "' )";	
 	  	
		Connection  conn = mysqlConnection.getConnection();
		Statement statment;
		try {
			statment = conn.createStatement();
			statment.executeUpdate(sql);
		 } catch (SQLException e) {
			
		 }
		
		
     }//for
     
  }
 
  public String getDianosisDate (ArrayList<String> priorityNotes, String patient) {
	  String result= "";
	  String in=this.getListNotes(priorityNotes);
	  String sql_select=" SELECT normalized from links " +
	                       " WHERE category IN (" + in + ") " +
			               " AND patient_id = '" + patient + "'" +
	                       " ORDER BY normalized ASC";
	  
	  try{
		   	Connection conn =mysqlConnection.getConnection();   
		       Statement sentencia = conn.createStatement();
		       ResultSet rs = sentencia.executeQuery(sql_select);
		       //
		       int count=0;
		       while(rs.next() && count<1){
		       	
		       	result= rs.getString(1) ;             
		           
		        
		          count ++;
		        }
			 }
		      catch(Exception e) {
		   	   System.out.println ("Error accessing the patient database, check connection");
		      }
			 return result;

	       
  }
  
  public String getDianosisDate (String patient) {
	  String result= "";
	 
	  String sql_select=" SELECT normalized from links " +
	                    " WHERE patient ='" + patient + "'" +
	                    " ORDER BY normalized ASC";
	  
	  try{
		   	Connection conn =mysqlConnection.getConnection();   
		       Statement sentencia = conn.createStatement();
		       ResultSet rs = sentencia.executeQuery(sql_select);
		       //
		       int count=0;
		       while(rs.next() && count<1){
		       	
		       	result= rs.getString(1) ;             
		           
		        
		          count ++;
		        }
			 }
		      catch(Exception e) {
		   	   System.out.println ("Error accessing the patient database, check connection");
		      }
			 return result;

	       
  }
  
  
  public String getListNotes(ArrayList<String> priorityNotes) {
	  String in = "";
	  int size= priorityNotes.size();
	  int x=0;
	  for (String note: priorityNotes) {
		 
		  if (x< (size-1)) {
			  in  = in + note + ", ";
		  }
		  else {
			  in  = in + note ;
		  }
		  
		  x++;
		 
	  }
	  return in;
  }
	
  
  public String getDiagnosis (ArrayList<String> priorityUMLS, String patient) {
	  
	  String result= "";
	  
	  for (int i=0; i< priorityUMLS.size(); i++ ) {
		  String umls = priorityUMLS.get(i);
		  String sql_select=" SELECT cancer_entity from links " +
                            " WHERE cancer_entity = '" + umls + "'" +
				            " AND patient = '" + patient + "'";
		  
		  try{
			   	Connection conn =mysqlConnection.getConnection();   
			       Statement sentencia = conn.createStatement();
			       ResultSet rs = sentencia.executeQuery(sql_select);
			       //
			       int count=0;
			       while(rs.next()){
			    	count ++;
			       	result= rs.getString(1) ;             
			         
			        }
			       
			       if (count ==1) {
						  i=priorityUMLS.size();
				   }
				 }
			      catch(Exception e) {
			   	   System.out.println ("Error accessing the patient database, check connection");
			      }
		  
		  			 
	  }//for
	 
	  
	  return result;
	  

	       
  }
	  
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



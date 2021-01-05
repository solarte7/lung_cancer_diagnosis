package es.upm.ctb.midas.dx.calculator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DaoPatient {
	MySqlPatient mysqlPatient;
	SimpleDateFormat dateFormat;
	Connection conn;
	
	public DaoPatient() {
		
		dateFormat= new SimpleDateFormat("yyyy-MM-dd");
		mysqlPatient = new MySqlPatient();
    	conn =mysqlPatient.getConnection();  
	}
	
	 public ArrayList <DxAnnotation> getDxAnnotations(String  ehr) {
	    	ArrayList <DxAnnotation> list1 = new ArrayList  <DxAnnotation> ();
	    	String sql_select;
	    	sql_select="SELECT document, EHR, category, subcategory, sentence, dateValue,  " +
	    	           " day, month, year,  " +
	    			   " dx1, dx1_squamous, dx2, dx2_squamous, cancer_description, documentDate, date " + 
	    			   " FROM patient_has_dx_annotation where  ehr = '" + ehr + "'" ; 
	    	
	    		    	
	       
	         try{
	        	
	            Statement sentencia = conn.createStatement();
	            ResultSet resultSet = sentencia.executeQuery(sql_select);
	            //
	           // System.out.println("SQL= " + sql_select);
	            while(resultSet.next()){
	            		            		                
	            	DxAnnotation ann = new DxAnnotation();
	            	
	            	//ann.setId(resultSet.getInt("id"));
	            	ann.setDocumentId(resultSet.getInt("document"));
	            	ann.setEhr(resultSet.getString("EHR"));
	            	
	            	ann.setCategory(resultSet.getString("category"));
	            	ann.setSubCategory(resultSet.getString("subcategory"));
	            	ann.setSentence(resultSet.getString("sentence"));	
	            	//ann.setSection(resultSet.getString("section"));
	            	
	            	//===========fecha DateValue =======================
	            	String dateValueString = resultSet.getString("dateValue");
	            	ann.setDateValue(dateValueString);
	            	try {
	            		Date dateAnnot = dateFormat.parse(dateValueString.trim());		            	
	            		ann.setDateValueNew(dateAnnot);
	            		
	            		ann.setDateDay(Integer.parseInt(resultSet.getString("day")));
		            	ann.setDateMonth(Integer.parseInt(resultSet.getString("month"))); 
		            	ann.setDateYear(Integer.parseInt(resultSet.getString("year")));
	            	}catch(Exception e) {
	               
	            	}
	            	//==================================================
	            	        	
	            	            	
	            	ann.setDescription((resultSet.getString("cancer_description")));
	            	
	            	//======================== Document Date =============================
	            	String documentString = resultSet.getString("documentDate");
	            	ann.setDocumentDate(documentString);	            	
	            	
	            	//System.out.println(" doc date: " + docDateString);
	            	Date date01 = dateFormat.parse(documentString.trim());		            	
	            	ann.setDocumentDateNew(date01);
	            	
	            	//=======================================================
	            	
	            	//======================== Date =============================
	            	String dateString1 = resultSet.getString("date");
	            	ann.setDate(dateString1);	            	
	            	
	            	//System.out.println(" doc date: " + docDateString);
	            	Date date1 = dateFormat.parse(dateString1.trim());		            	
	            	ann.setDateNew(date1);
	            	
	            	//=======================================================
	             	
	            	           	            	
	            	//System.out.println("oki3");
	                list1.add(ann);
	                
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
	 
	 public void saveDisambiguatedAnnotation(DxAnnotation ann) {
		
	    String sql_insert = "INSERT INTO disambiguated_annotation (document, EHR, " + 
		                " category, subcategory, sentence, dateValue, "
		                + "  documentDate, documentDateNew, "
		                + " date, dateNew) " +
		                " VALUES (" + ann.getDocumentId() + ", '" + ann.getEhr()+ "',  '" +
		                ann.getCategory() + "', '" + ann.getSubCategory() + "', '" +  ann.getSentence() + "', '" +
		                ann.getDateValue() + "', '" +     
		                ann.getDocumentDate() + "', '" + dateFormat.format(ann.getDocumentDateNew()) +  "', '" +
		                ann.getDate()+ "', '" +  dateFormat.format(ann.getDateNew()) + "')";
	    
	    try{
        	
	    	Statement sentencia = conn.createStatement();
	    	sentencia.executeUpdate(sql_insert);
	    	//System.out.println("Guardando");
	    }
	    catch(Exception e){
	    	
	    }
	    	
	 }
	 
	 /**
	  * COnsulta solo anotaciones de juicio clinico
	  * @param ehr
	  * @return
	  */ 
	 //=======================================================================================================
	 public ArrayList <DxAnnotation> getClinicalJudgmentlAnnotations (String ehr) {
	    	ArrayList <DxAnnotation> list1 = new ArrayList  <DxAnnotation> ();
	    	String sql_select;
	    	/*sql_select="(SELECT document, EHR, category, subcategory, sentence, dateValue,  " +
	    	           " documentDate, date, dateNew " + 
	    			   " FROM disambiguated_annotation where  EHR = '" + ehr + "' " + 
	    	           " AND subcategory like 'jui%' ) " +
	    			   
	    			   " UNION " +
							    	           
						" (SELECT document, EHR, category, subcategory, sentence, dateValue,  " +
						" documentDate, date, dateNew " + 
						" FROM disambiguated_annotation " +
						" WHERE (sentence like 'JC%' OR sentence like  'juicio%')" +
						" AND  EHR = '"  + ehr + "' )" +
						" ORDER BY dateNew asc;";*/
	    	
	    	sql_select = "SELECT document, EHR, category, subcategory, sentence, dateValue, " + 
	    	        " documentDate, date, dateNew " + 
	    			" FROM  disambiguated_annotation" + 
	    			" where  ehr = '" + ehr +  "' " + 
	    			" and (sentence like '%diagnosticado%' or sentence like '%diagnosticada%' "
	    			+ "  or  sentence like '%diagnostico%' or sentence like '%Dx%' " + 
	    			"    or  sentence like '%se diagnostica%' or  sentence like '%diagnóstico%' " + 
	    			"    or subcategory like '%oncologia medica%'\n" + 
	    			"    or subcategory like '%juicio%' ) " + 
	    		    " order by dateNew;";
	    	          
	    	          
	    	try {

	            Statement sentencia = conn.createStatement();
	            ResultSet resultSet = sentencia.executeQuery(sql_select);
	            
	          	while(resultSet.next()){	            		            		                
	            	DxAnnotation ann = new DxAnnotation();
	            	ann.setDocumentId(resultSet.getInt("document"));
	            	ann.setEhr(resultSet.getString("EHR"));
	            	ann.setCategory(resultSet.getString("category"));
	            	ann.setSubCategory(resultSet.getString("subcategory"));
	            	ann.setSentence(resultSet.getString("sentence"));
	            	ann.setDateValue(resultSet.getString("dateValue"));
	            	ann.setDocumentDate(resultSet.getString("documentDate"));
	            	ann.setDate(resultSet.getString("date"));
	            	ann.setDateNew(resultSet.getDate("dateNew"));
	            	
	            	list1.add(ann);
	            	
	            }
	    	}
	    	catch(Exception e) {
	    		
	    	}
	    
	   //System.out.println("Size " +  list1.size()); 
	   //System.out.println("Size " +  list1.get(0).getSubCategory()); 
	   return list1;
	}//end
	 
	 
	 //==================================================================================================================
	 
	 public ArrayList <DxAnnotation> getClinicalJudgmentlAnnotations1 (String ehr) {
	    	ArrayList <DxAnnotation> list1 = new ArrayList  <DxAnnotation> ();
	    	String sql_select="(SELECT document, EHR, category, subcategory, sentence, date " + 
	    			          " FROM disambiguated_annotation " + 
	    			          " WHERE  EHR =  '" +  ehr + "' " + 
				    		  " AND  (sentence like '%oncologia%' or sentence like '%oncologica%' " + 
				    			"    or sentence like 'oncología' or sentence like 'JC:%'" + 
				    			"    or sentence like '%diagnosticado%' " + 
				    			"    or sentence like '%se diagnostica%'  " + 
				    			"    or sentence like '%se diagnóstico%'  " + 
				    			"    or subcategory like '%oncologia medica%'" + 
				    			"    or subcategory like '%juicio%'" + 
				    			"    or subcategory like '%oncologia medica%')) " +
						    			
			    			" UNION " + 
			    			" (SELECT document, EHR, category, subcategory, sentence, date " + 
	    			          " FROM disambiguated_annotation_tnm " + 
	    			          " WHERE  EHR = '" + ehr + "' " +
				    		  " AND  (sentence like 'diagnóstico%'" + 
				    			"    or sentence like '%diagnosticado%' " + 
				    			"    or sentence like '%se diagnostica%'  " + 
				    			"    or subcategory like '%oncologia medica%'" + 
				    			"    or subcategory like '%juicio%'))" + 				    				    		
			    			" ORDER BY date asc; ";
	    	
	    	 
	    	//System.out.println("SQL " + sql_select);
	    	          
	    	try {

	            Statement sentencia = conn.createStatement();
	            ResultSet resultSet = sentencia.executeQuery(sql_select);
	            
	          	while(resultSet.next()){	 
	          		
	            	DxAnnotation ann = new DxAnnotation();
	            	ann.setDocumentId(resultSet.getInt("document"));
	            	ann.setEhr(resultSet.getString("EHR"));
	            	ann.setCategory(resultSet.getString("category"));
	            	ann.setSubCategory(resultSet.getString("subcategory"));
	            	ann.setSentence(resultSet.getString("sentence"));
	                //ann.setDocumentDate(resultSet.getString("documentDate"));
	            	ann.setDate(resultSet.getString("date"));
	            	
	            	
	            	list1.add(ann);
	            	
	            }
	    	}
	    	catch(Exception e) {
	    		System.out.println("Error " + e.getMessage());
	    	}
	    
	   System.out.println("Size " +  list1.size()); 
	   //System.out.println("Size " +  list1.get(0).getSubCategory()); 
	   return list1;
	}//end
	 
	 
	 //================================================= TNM ====================================================================
	 
	 /**
	  * 
	  * @param ehr
	  * @return
	  */
	 
	 
	 
	 public ArrayList <DxAnnotation> getJudgtment_TNM_Annotations (String ehr) {
	    	ArrayList <DxAnnotation> list1 = new ArrayList  <DxAnnotation> ();
	    	String sql_select;
	    	/*sql_select="(SELECT document, EHR, category, subcategory, sentence, dateValue,  " +
	    	           " documentDate, date, dateNew " + 
	    			   " FROM patient_has_stage_annotation where  EHR = '" + ehr + "' " + 
	    	           " AND subcategory like 'jui%' ) " +
	    			   
	    			   " UNION " +
							    	           
						" (SELECT document, EHR, category, subcategory, sentence, dateValue,  " +
						" documentDate, date, dateNew " + 
						" FROM patient_has_stage_annotation " +
						" WHERE (sentence like 'JC%' OR sentence like  'juicio%')" +
						" AND  EHR = '"  + ehr + "' )" +
						" ORDER BY dateNew asc;";*/
	    	
	    	
	    	sql_select = "SELECT document, EHR, category, subcategory, sentence, dateValue, " + 
	    	        " documentDate, date, dateNew " + 
	    			" FROM  disambiguated_annotation_tnm" + 
	    			" where  ehr = '" + ehr +  "' " + 
	    			" and (sentence like '%diagnosticado%' or sentence like '%diagnosticada%' "
	    			+ "  or  sentence like '%diagnostico%' or sentence like '%Dx%' " + 
	    			"    or  sentence like '%se diagnostica%' or  sentence like '%diagnóstico%' " + 
	    			"    or subcategory like '%oncologia medica%'\n" + 
	    			"    or subcategory like '%juicio%' ) " + 
	    			 
	    			"order by dateNew;";
	    	          
	    	          
	    	try {

	            Statement sentencia = conn.createStatement();
	            ResultSet resultSet = sentencia.executeQuery(sql_select);
	            
	          	while(resultSet.next()){	            		            		                
	            	DxAnnotation ann = new DxAnnotation();
	            	ann.setDocumentId(resultSet.getInt("document"));
	            	ann.setEhr(resultSet.getString("EHR"));
	            	ann.setCategory(resultSet.getString("category"));
	            	ann.setSubCategory(resultSet.getString("subcategory"));
	            	ann.setSentence(resultSet.getString("sentence"));
	            	ann.setDateValue(resultSet.getString("dateValue"));
	            	ann.setDocumentDate(resultSet.getString("documentDate"));
	            	ann.setDate(resultSet.getString("date"));
	            	ann.setDateNew(resultSet.getDate("dateNew"));
	            	
	            	list1.add(ann);
	            	
	            }
	    	}
	    	catch(Exception e) {
	    		
	    	}
	    
	   //System.out.println("Size " +  list1.size()); 
	   //System.out.println("Size " +  list1.get(0).getSubCategory()); 
	   return list1;
	}
	 
	 
	 //=======================================================================================================
	
	 /**
	  * Consulta Todas las anotaciones del ehr
	  * @param ehr
	  * @return
	  */
	//=======================================================================================================
		 public ArrayList <DxAnnotation> getEhrAnnotations (String ehr) {
		    	ArrayList <DxAnnotation> list1 = new ArrayList  <DxAnnotation> ();
		    	String sql_select;
		    	/*sql_select="SELECT document, EHR, category, subcategory, sentence, dateValue,  " +
		    	           " documentDate, date, dateNew " + 
		    			   " FROM disambiguated_annotation where  EHR = '" + ehr + "' " + 		    	        
		    	           " ORDER BY dateNew asc";*/
		    	
		    	sql_select="SELECT document, EHR, category, subcategory, sentence, dateValue,  " +
		    	           " documentDate, date " + 
		    			   " FROM patient_has_dx_annotation where  EHR = '" + ehr + "' " + 		    	        
		    	           " ORDER BY date asc";
		    	          
		    	try {

		            Statement sentencia = conn.createStatement();
		            ResultSet resultSet = sentencia.executeQuery(sql_select);
		            
		          	while(resultSet.next()){	            		            		                
		            	DxAnnotation ann = new DxAnnotation();
		            	ann.setDocumentId(resultSet.getInt("document"));
		            	ann.setEhr(resultSet.getString("EHR"));
		            	ann.setCategory(resultSet.getString("category"));
		            	ann.setSubCategory(resultSet.getString("subcategory"));
		            	ann.setSentence(resultSet.getString("sentence"));
		            	ann.setDateValue(resultSet.getString("dateValue"));
		            	ann.setDocumentDate(resultSet.getString("documentDate"));
		            	ann.setDate(resultSet.getString("date"));
		            	//ann.setDateNew(resultSet.getDate("dateNew"));
		            	ann.setDateNew(resultSet.getDate("date"));
		            	
		            	list1.add(ann);
		            	
		            }
		    	}
		    	catch(Exception e) {
		    		
		    	}
		    
		   //System.out.println("Size " +  list1.size()); 
		   //System.out.println("Size " +  list1.get(0).getSubCategory()); 
		   return list1;
	}//endmethod
		 
  public void saveDiagnosisDate (String ehr, DxAnnotation d) {
	  
			
		    String sql_insert = "INSERT INTO diagnosis_date (ehr, date, category, subcategory, sentence) " + 
			                " VALUES ('" + ehr + "', '" + d.getDateNew() + "', '" +
		    		        d.getCategory() + "', '" + d.getSubCategory() + "', '" +
		    		        d.getSentence() +     "' )";
		    //System.out.println(sql_insert);
		    try{
	        	
		    	Statement sentencia = conn.createStatement();
		    	sentencia.executeUpdate(sql_insert);
		    	
		    	System.out.println("Guardando");
		    }
		    catch(Exception e){
		    	System.out.println("Errror al guardar en la BD");
		    }
		    	
		
  }
  
  //=================================================================================================
  ///=================================================================================================
  ///////  =============================== TNM Methods================================================= ////
  public ArrayList <DxAnnotation> getTNMAnnotations(String  ehr) {
  	ArrayList <DxAnnotation> list1 = new ArrayList  <DxAnnotation> ();
  	String sql_select;
  	sql_select="SELECT document, EHR, category, subcategory, sentence, dateValue,  " +
  	           " day, month, year,  " +
  			   " documentDate, date " + 
  			   " FROM patient_has_stage_annotation where  ehr = '" + ehr + "'" +
  			   " AND length(sentence) > 25"; 
  	
  		  	
       try{
      	
          Statement sentencia = conn.createStatement();
          ResultSet resultSet = sentencia.executeQuery(sql_select);
          //
        // System.out.println("SQL= " + sql_select);
          while(resultSet.next()){
          		            		                
          	DxAnnotation ann = new DxAnnotation();
          	//System.out.println("oki");
          	//ann.setId(resultSet.getInt("id"));
          	ann.setDocumentId(resultSet.getInt("document"));
          	ann.setEhr(resultSet.getString("EHR"));
          	
          	
          	
	    	
          	
          	ann.setCategory(resultSet.getString("category"));
          	ann.setSubCategory(resultSet.getString("subcategory"));
          	ann.setSentence(resultSet.getString("sentence"));	
          	//ann.setSection(resultSet.getString("section"));
          	
          	//===========fecha DateValue =======================
          	String dateValueString = resultSet.getString("dateValue");
          	ann.setDateValue(dateValueString);
          	try {
          		Date dateAnnot = dateFormat.parse(dateValueString.trim());		            	
          		ann.setDateValueNew(dateAnnot);
          		
          		ann.setDateDay(Integer.parseInt(resultSet.getString("day")));
	            	ann.setDateMonth(Integer.parseInt(resultSet.getString("month"))); 
	            	ann.setDateYear(Integer.parseInt(resultSet.getString("year")));
          	}catch(Exception e) {
             
          	}
          	//==================================================
          	        	
          	            	
          	          	
          	//======================== Document Date =============================
          	String documentString = resultSet.getString("documentDate");
          	ann.setDocumentDate(documentString);	            	
          	
          	//System.out.println(" doc date: " + docDateString);
          	Date date01 = dateFormat.parse(documentString.trim());		            	
          	ann.setDocumentDateNew(date01);
          	
          	//=======================================================
          	
          	//======================== Date =============================
          	String dateString1 = resultSet.getString("date");
          	ann.setDate(dateString1);	            	
          	
          	//System.out.println(" doc date: " + docDateString);
          	Date date1 = dateFormat.parse(dateString1.trim());		            	
          	ann.setDateNew(date1);
          	
          	//=======================================================
           	
          	           	            	
          	//System.out.println("oki3");
              list1.add(ann);
              
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

  public void saveDisambiguatedAnnotationsTNM(DxAnnotation ann) {
		
	    String sql_insert = "INSERT INTO disambiguated_annotation_tnm (document, EHR, " + 
		                " category, subcategory, sentence, dateValue, "
		                + "  documentDate, documentDateNew, "
		                + " date, dateNew) " +
		                " VALUES (" + ann.getDocumentId() + ", '" + ann.getEhr()+ "',  '" +
		                ann.getCategory() + "', '" + ann.getSubCategory() + "', '" +  ann.getSentence() + "', '" +
		                ann.getDateValue() + "', '" +     
		                ann.getDocumentDate() + "', '" + dateFormat.format(ann.getDocumentDateNew()) +  "', '" +
		                ann.getDate()+ "', '" +  dateFormat.format(ann.getDateNew()) + "')";
	    
	    try{
      	
	    	Statement sentencia = conn.createStatement();
	    	sentencia.executeUpdate(sql_insert);
	    	System.out.println("Guardando");
	    }
	    catch(Exception e){
	    	System.out.println("Error Guardando");
	    }
	    	
	 }
  
  /**
   * 
   * @param ehr
   * @return
   */
  //=======================================================================================================
	 public ArrayList <DxAnnotation> getDisAmbiguatedAnnotationsTnm (String ehr) {
	    	ArrayList <DxAnnotation> list1 = new ArrayList  <DxAnnotation> ();
	    	String sql_select;
	    	sql_select="(SELECT document, EHR, category, subcategory, sentence, dateValue,  " +
	    	           " documentDate, date, dateNew " + 
	    			   " FROM disambiguated_annotation_tnm where  EHR = '" + ehr + "' " + 
	    	           " AND subcategory like 'jui%' ) " +
	    			   
	    			   " UNION " +
							    	           
						" (SELECT document, EHR, category, subcategory, sentence, dateValue,  " +
						" documentDate, date, dateNew " + 
						" FROM disambiguated_annotation_tnm " +
						" WHERE (sentence like 'JC%' OR sentence like  'juicio%')" +
						" AND  EHR = '"  + ehr + "' )" +
						" ORDER BY dateNew asc;";
	    	        
	    System.out.println(sql_select);
	    	          
	    	try {

	            Statement sentencia = conn.createStatement();
	            ResultSet resultSet = sentencia.executeQuery(sql_select);
	            
	          	while(resultSet.next()){	            		            		                
	            	DxAnnotation ann = new DxAnnotation();
	            	ann.setDocumentId(resultSet.getInt("document"));
	            	ann.setEhr(resultSet.getString("EHR"));
	            	ann.setCategory(resultSet.getString("category"));
	            	ann.setSubCategory(resultSet.getString("subcategory"));
	            	ann.setSentence(resultSet.getString("sentence"));
	            	ann.setDateValue(resultSet.getString("dateValue"));
	            	ann.setDocumentDate(resultSet.getString("documentDate"));
	            	ann.setDate(resultSet.getString("date"));
	            	ann.setDateNew(resultSet.getDate("dateNew"));
	            	
	            	list1.add(ann);
	            	
	            }
	    	}
	    	catch(Exception e) {
	    		
	    	}
	    
	   System.out.println("Size " +  list1.size()); 
	   System.out.println("Size " +  list1.get(0).getSubCategory()); 
	   return list1;
	}
	 
	 /**
	  * Get EHRs
	  * 
	  * @return
	  */
	 public ArrayList <String> getAllEhr() {
		 ArrayList <String> list1 = new ArrayList  <String> ();
	    	String sql_select;
	    	sql_select="SELECT distinct EHR " + 
	    			   " FROM patient_firstdx_annotation"; 
	    	        
	    System.out.println(sql_select);
	    	          
	    	try {

	            Statement sentencia = conn.createStatement();
	            ResultSet resultSet = sentencia.executeQuery(sql_select);
	            
	          	while(resultSet.next()){	            		            		                
	            	String ehr= resultSet.getString("EHR");
	            	list1.add(ehr);
	            	
	            }
	    	}
	    	catch(Exception e) {
	    		
	    	}
	    
	   System.out.println("Size " +  list1.size()); 
	   
	   return list1;
		 
	 }//end
  
  ///// ==================================================================================  ////
	 
	 public void closeConnection() {
		 mysqlPatient.closeConnection();
	 }
	 

	 /**
	  * Main
	  * @param a
	  */
	    public static void main(String a[]) {
	    		    	
	    	DaoPatient dao = new DaoPatient();
	    	//dao.getDxAnnotations("3561");
	    	//dao.getClinicalTrialAnnotations("2536158");
	    	//dao.getTNMAnnotations("385734");
	    	ArrayList <DxAnnotation> list1 = dao.getJudgtment_TNM_Annotations("1007602");
	    	if (list1.size()> 0 ) {
	    		System.out.println("Date " + list1.get(0).getDate());	    	
	    	}
	    	else {
	    		System.out.println(" No found");
	    	}
	    	
	    }

	}//fin clase

			


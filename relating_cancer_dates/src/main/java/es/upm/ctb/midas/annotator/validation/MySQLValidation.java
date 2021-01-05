package es.upm.ctb.midas.annotator.validation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


import es.upm.ctb.midas.annotator.validation.*;


public class MySQLValidation {
	String user="";
	String password="";
	java.sql.Connection conn;

	public MySQLValidation() {
		this.user="user";
		this.password="user";
		connectDataBase();
	}
	
	public  void connectDataBase() {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String db="jdbc:mysql://localhost:3306/testing";
			this.conn = (java.sql.Connection) DriverManager.getConnection(db ,user, password);
			System.out.println("Connected to MySQL Succesfully [Validation DB]");
						
			
		}
		 
		catch(Exception e) {
			System.out.println("Error connecting Data base: " +  e.getMessage());
			
		}
	}
	
	
	public java.sql.Connection getConection() {
		return this.conn;
	}
	
	public void closeConnection() {
		try {
			this.conn.close();
			//System.out.println("Data base connection closed");
			
		} catch (SQLException e) {
			
			System.out.println("Error al cerrar la Base de datos: "  );
		}
		
	}
	
	 
   public void insertData(String sql) {
	   
       Statement statment;
	try {
		statment = conn.createStatement();
		int numFilas = statment.executeUpdate(sql);
		System.out.println("sql " + sql + numFilas);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       
   }
    
   public HashMap<Integer, String> getDianosticClikes() {
	   //ArrayList <Diagnostic> list1 = new ArrayList<Diagnostic>();
	   HashMap<Integer, String> map1= new HashMap<Integer, String>();
	   String sql_select;
	   sql_select="SELECT EHR, date from patient_firstdx_final"; 
	   
	    try{
	    	
	    	Statement sentencia = conn.createStatement();
	    	ResultSet tabla = sentencia.executeQuery(sql_select);
	            //
	            
	    	while(tabla.next()){
	    		  try {
			          int ehr = tabla.getInt(1);
			          String date = tabla.getString(2);
			          date = date.substring(0,10);
			          date = date.trim();
			      
			         
			          map1.put(ehr, date);
			          
	    		  } catch(Exception e) {
			    	  System.out.println(" error " + e.getMessage()); 
			      }
	          
	        }
	      
	    }
	    catch(SQLException e){ 		        	 
	        	 System.out.println(e);
	        	
	        	 
	    }
	    
	   return  map1;
   }//end
   
   
   public  HashMap<Integer, String> getDianosticHospital() {
	   //ArrayList <Diagnostic> list1 = new ArrayList<Diagnostic>();
	   HashMap<Integer, String> map1= new HashMap<Integer, String>();
		  
	   String sql_select;
	   sql_select="SELECT ehr, fecha_diagnostico from datos_de_hospital"; 
	   
	    try{
	    	
	    	Statement sentencia = conn.createStatement();
	    	ResultSet tabla = sentencia.executeQuery(sql_select);
	            //
	            
	    	while(tabla.next()){
	    		  try {
			          int ehr = tabla.getInt(1);
			          String date = tabla.getString(2);
			          date = date.substring(0,10);
			          date = date.trim();
			         		          
	        
			         // Date cleanedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);			 
			         // System.out.println(ehr +  " [" + date + "] \t  [" + cleanedDate + "]" );
			         
			          map1.put(ehr, date);
			          
	    		  } catch(Exception e) {
			    	  System.out.println(" error " + e.getMessage()); 
			      }
	          
	        }
	      
	    }
	    catch(SQLException e){ 		        	 
	        	 System.out.println(e);
	        	
	        	 
	    }
	    
	    return map1;
	   
   }
   
   public  HashMap<Integer, String> getDianosticReal() {
	   //ArrayList <Diagnostic> list1 = new ArrayList<Diagnostic>();
	   HashMap<Integer, String> map1= new HashMap<Integer, String>();
		  
	   String sql_select;
	   sql_select="SELECT ehr, fecha_diagnostico from datos_real"; 
	   
	    try{
	    	
	    	Statement sentencia = conn.createStatement();
	    	ResultSet tabla = sentencia.executeQuery(sql_select);
	            //
	            
	    	while(tabla.next()){
	    		  try {
			          int ehr = tabla.getInt(1);
			          String date = tabla.getString(2);
			          date = date.substring(0,10);
			          date = date.trim();
			         		          
	        
			        //Date cleanedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);			 
			         //System.out.println(ehr +  " [" + date + "] \t  [" + cleanedDate + "]" );
			         
			          map1.put(ehr, date);
			          
	    		  } catch(Exception e) {
			    	  System.out.println(" error " + e.getMessage()); 
			      }
	          
	        }
	      
	    }
	    catch(SQLException e){ 		        	 
	        	 System.out.println(e);
	        	
	        	 
	    }
	    
	    return map1;
	   
   }//
   
   public HashMap<Integer, Date> getDianosticDatesOsw(String table) {
	   //ArrayList <Diagnostic> list1 = new ArrayList<Diagnostic>();
	   HashMap<Integer, Date> map1= new HashMap<Integer, Date>();
	   String sql_select;
	   sql_select="SELECT EHR, date from " +  table; 
	   
	    try{
	    	
	    	Statement sentencia = conn.createStatement();
	    	ResultSet tabla = sentencia.executeQuery(sql_select);
	            //
	            
	    	while(tabla.next()){
	    		  try {
			          int ehr = tabla.getInt(1);
			          Date date = tabla.getDate(2);
			         // date = date.substring(0,10);
			          //date = date.trim();
			      
			         
			          map1.put(ehr, date);
			          
	    		  } catch(Exception e) {
			    	  System.out.println(" error " + e.getMessage()); 
			      }
	          
	        }
	      
	    }
	    catch(SQLException e){ 		        	 
	        	 System.out.println(e);
	        	
	        	 
	    }
	    
	   return  map1;
   }
    
    
    
	public static void main(String args[]) {
		MySQLValidation mySqlDB= new MySQLValidation();
		System.out.println("ok validation"  );	
	    mySqlDB.getDianosticClikes();
		
		//mySqlDB.getDianosticHospital();
		mySqlDB.getDianosticReal();
		//mySqlDB.closeConnection();
	}
	
}
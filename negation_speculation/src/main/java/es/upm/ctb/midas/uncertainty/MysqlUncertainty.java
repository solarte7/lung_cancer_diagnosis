package es.upm.ctb.midas.uncertainty;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;





public class MysqlUncertainty  {
	
	private static MysqlUncertainty  instance;
	String user="oswaldo";
	String password="oswaldo";
	String db="jdbc:mysql://localhost:3306/IASIS_document_20200311?" + 
			  "useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true";
	
	Connection connection;

	
	
	public  MysqlUncertainty () {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");			
			this.connection = (java.sql.Connection) DriverManager.getConnection(db ,user, password);
			System.out.println("Connected to MySQL Succesfully...");
			
		}		 
		catch(Exception e) {
			System.out.println("Error connecting to local data base: " +  e.getMessage());
			
		}
	
		
	}
	
	public static MysqlUncertainty  getInstance() throws SQLException {
		
        if (instance == null) {
            instance = new MysqlUncertainty ();
        } 
        
        return instance;
    }//end 
	
	
	public ArrayList <String> getSentences(String table1) {
		String sql = "SELECT distinct sentence_id, sentence sentence FROM " +  table1;
		
		ArrayList <String> list1 = new  ArrayList <String>();
    	
    	
        //System.out.println("SQL= " + sql_select);
         try{
        	Connection conn =this.getConnection();   
            Statement sentencia = conn.createStatement();
            ResultSet tabla = sentencia.executeQuery(sql);
            //
            
            while(tabla.next()){
            	String id=tabla.getString(2);
                //System.out.println(id);               
                list1.add(id);
                
             }
         }
         catch(Exception e) {
        	 
         }
   
         System.out.println("List size: " + list1.size());  
        return list1;    
         
	}//end method

	public Connection getConnection() {
        return this.connection;
    }
	
	public void saveScopes(ArrayList<Scope>  scopeList, String annotator) {
		String sql= "INSERT INTO scope (begin, end, cue,  sentence, sentence_id, file_path, cue_type, annotator, scope_text) " +
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)" ;
		try {    
			Connection  conn;
			PreparedStatement ps;
			conn = MysqlUncertainty.getInstance().getConnection();			
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql); 
		
			for(Scope scope : scopeList) {
				int begin = scope.getBegin();
				int end = scope.getEnd();
				String sentence = scope.getSentence();
				ps.setInt(1, begin);				   
				ps.setInt(2, end);
				ps.setString(3, scope.getCue());				
				ps.setString(4, sentence);
				ps.setInt(5, scope.getSentenceId());
				ps.setString(6, scope.getFilePath());
				ps.setString(7, scope.getType());
				ps.setString(8, annotator);
				String scopeText="";
				try {
					 scopeText= sentence.substring(begin, end) ;
					 ps.setString(9, scopeText);
				  }
				  catch (Exception e) {
					// TODO: handle exception
				}
		        ps.addBatch();

			}//end for
		
			System.out.println(" Saving ....  "   );
		    int num[]=  ps.executeBatch();
		    conn.commit();	
		    System.out.println("\n \n **********  Scopes saved OK  **********  "   );	   
		  			
		    ps.clearBatch();
		   
		}catch (Exception e) {
			 System.out.println(" Error Saving ....  "   );	 
		}
				
	}//end
	
	
	
	public void closeConnection() {
		try {
			this.connection.close();
			System.out.println("Data base connection closed");
			
		} catch (SQLException e) {
			
			System.out.println("Error al cerrar la Base de datos: "  );
		}
		
	}
	
	
	public static void main(String args[]) {
		java.sql.Connection conn;
		MysqlUncertainty  mySqlDB= new MysqlUncertainty ();
		mySqlDB.getConnection();	
		mySqlDB.getSentences("umls");
		//mySqlDB.closeConnection();
	}
	
}
package es.upm.ctb.jkes.relating.databases;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;

/*This class implements Singleton Pattern, to have unique instance of database
 * 
 *   
 **/

public class MySQLPatient {
	
	
	
	String user="oswaldo";
	String password="oswaldo";
	String db="jdbc:mysql://localhost:3306/IASIS_patient_20200311?" + 
			  "useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true";
	
	Connection connection;

	
	
	public  MySQLPatient() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");			
			this.connection = (java.sql.Connection) DriverManager.getConnection(db ,user, password);
			System.out.println("Connected to MySQL Succesfully... Patient");
			
		}		 
		catch(Exception e) {
			System.out.println("Error connecting to local data base: " +  e.getMessage());
			
		}
	
		
	}
	
	

	public Connection getConnection() {
        return this.connection;
    }
	
	
	
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
		MySQLPatient mySqlDB= new MySQLPatient();
		mySqlDB.getConnection();	
		//mySqlDB.closeConnection();
	}
	
}


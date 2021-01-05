package es.upm.ctb.midas.dx.calculator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySqlDxCalculator {
	
		        String url, user, password;
		        Connection conn;
		        Statement statment;
		        ResultSet resultSet;
		        public MySqlDxCalculator(){
		            url="jdbc:mysql://localhost:3306/IASIS_document_20200311";
		            user="oswaldo";
		            password="oswaldo";
		            connect();
		        }

		        public Connection connect(){
		            try {
		            // Se carga el driver
		            Class.forName("com.mysql.cj.jdbc.Driver");
		            //System.out.println( "Driver Cargado" );
		            } catch( Exception e ) {
		                System.out.println( "No se pudo cargar el driver." );
		            }

		            try{
		                     //Crear el objeto de conexion a la base de datos
		                     conn = DriverManager.getConnection(url, user, password);
		                     System.out.println( "Conexion Abierta" );
		                     return conn;
		                  //Crear objeto Statement para realizar queries a la base de datos
		             } catch( Exception e ) {
		                System.out.println( "No se pudo abrir." );
		                return null;
		             }

		        }//end connectar

		        public void closeConnection(){
		            try{
		                 this.conn.close();
		            } catch( Exception e ) {
		                System.out.println( "No se pudo cerrar." );
		            }
		        }
		        
		        public Connection getConnection() {
		        	return this.conn;
		        }


			public static void main (String a []) {
				MySqlDxCalculator f= new MySqlDxCalculator();
				Connection c= f.getConnection();
				System.out.println( "okk;k" );
				//f.closeConnection();
		    }

		

	}




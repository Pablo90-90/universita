package mypackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connessione {
//classe connessione per creare la connessione al DB con metodo statico lanciabile una volta sola

	public static Connection getCon() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new SQLException("Driver MySQL non trovato", e);
		}
		//inserimento url,user e pass
		//ho inserito un driver JDBC aggiornato nella cartella lib
		String url = "jdbc:mysql://localhost:3306/universita?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
		String user = "root";
		String pass = "root";

		return DriverManager.getConnection(url, user, pass);
	}
}

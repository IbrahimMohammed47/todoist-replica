package context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class SQLConnection {

	private static SQLConnection dbIsntance;
	private static Connection con;

	private SQLConnection(Dotenv dotenv) throws SQLException {		
			con = DriverManager.getConnection(
					dotenv.get("SQL_URL"), 
					dotenv.get("SQL_USERNAME"), 
					dotenv.get("SQL_PASSWORD"));
			System.out.println("Connected to SQL :)");
	}

	public static SQLConnection getInstance(Dotenv dotenv) throws SQLException {
		if (dbIsntance == null) {
			dbIsntance = new SQLConnection(dotenv);
		}
		return dbIsntance;
		
	}

	public Connection getConnection() {
		return con;
	}

}
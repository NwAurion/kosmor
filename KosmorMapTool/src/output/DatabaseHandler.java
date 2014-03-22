package output;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

import processing.Planet;


public class DatabaseHandler {
	String url; // = "jdbc:mysql://localhost:3306/";
	String dbName; // = "kosmor";
	String driver; // = "com.mysql.jdbc.Driver";
	String userName; // = "root";
	String password; // = "root";
	Connection conn;

	public DatabaseHandler(String url, String dbName, String driver,
			String userName, String password) {
		this.url = url;
		this.dbName = dbName;
		this.driver = driver;
		this.userName = userName;
		this.password = password;
	}

	public void connect() {
		System.out.println("Trying to connect to " + this.url);
		try {
			Class.forName(this.driver).newInstance();
			this.conn = DriverManager
					.getConnection(this.url + this.dbName,
							this.userName, this.password);
			System.out.println("Connected to the database");
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(LinkedList<Planet> planetList) {
		for (Planet planet : planetList) {
			this.write(planet);
		}
	}


	public void write(Planet planet) {
		try {
			PreparedStatement pst = this.conn
					.prepareStatement("INSERT planet VALUES(?, ?, ? ,?)");
			pst.setInt(1, planet.getId());
			pst.setString(2, planet.getName());
			pst.setString(3, planet.getHouse());
			pst.setString(4, planet.getOwner());
			pst.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			this.conn.close();
			System.out.println("Disconnected from database");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

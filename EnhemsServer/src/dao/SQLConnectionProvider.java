package dao;


import java.sql.Connection;

/**
 * Pohrana veza prema bazi podataka u ThreadLocal object. ThreadLocal je zapravo
 * mapa ciji su kljucevi identifikator dretve koji radi operaciju nad mapom.
 * 
 * @author marcupic
 *
 */
public class SQLConnectionProvider {

	private static ThreadLocal<Connection> connections = new ThreadLocal<>();
	
	/**
	 * Postavi vezu za trenutnu dretvu (ili obrisi zapis iz mape ako je argument <code>null</code>).
	 * 
	 * @param con veza prema bazi
	 */
	public static void setConnection(Connection con) {
		if(con==null) {
			connections.remove();
		} else {
			connections.set(con);
		}
	}
	
	/**
	 * Dohvati vezu koju trenutna dretva (pozivatelj) smije koristiti.
	 * 
	 * @return vezu prema bazi podataka
	 */
	public static Connection getConnection() {
		Connection con = connections.get();
		return con;
	}
	
	
}


package enhems;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Used for logging errors in application during runtime.
 * For debugging purposes.
 * 
 * @author TeoToplak
 *
 */
public class MyLogger {

	/**
	 * Used logger
	 */
	private static Logger myLogger;
	
	/**
	 * Method used for logging messages when exceptions happen
	 * @param msg message
	 * @param ex excpetion or other error
	 */
	public static void log(String msg, Throwable ex) {
		if(myLogger == null) {
			createLogger();
		}
		myLogger.log(Level.SEVERE, msg, ex);
	}
	
	/**
	 * Configures logger to output file
	 */
	private static void createLogger() {
		myLogger = Logger.getLogger("ehnhemsLog");
		FileHandler fh=null;
		try {
			fh = new FileHandler("./data/enhemsLog.log", true);
		} catch (Exception e) {
			Utilities.showErrorDialog("Error", 
					"Nastala je greška prilikom dohvata log datoteke! "
					+ "Provjerite da se u direktoriju aplikacije nalazi prikladni folder \"data\".",
					null, e);
		}
		SimpleFormatter sf = new SimpleFormatter();
		fh.setFormatter(sf);
		myLogger.addHandler(fh);
		fh.setLevel(Level.ALL);
		myLogger.setLevel(Level.ALL);
		
	}
}

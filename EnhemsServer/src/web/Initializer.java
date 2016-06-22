package web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.http.client.utils.DateUtils;
import org.apache.log4j.Logger;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import dao.EnhemsDB;
import dao.SQLConnectionProvider;
import graphjob.GraphJob;
import web.Model.TokenRep;
import web.Servlets.LoginServlet;

public class Initializer implements ServletContextListener {

	private ScheduledExecutorService scheduler;
	final static Logger logger = Logger.getLogger(Initializer.class);

    /**
     * On web application start, if necessary, creates folder for graphs, also
     * starts timers for graph creation and user expiration check
     *
     * @param sce ServletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	
    	sce.getServletContext().setAttribute("enhems.dbpool", EnhemsDB.initDataSource());
    	
    	scheduler = Executors.newScheduledThreadPool(2);
    	
        if (!Files.isDirectory(Paths.get("C:\\pictures"))) {
            new File("C:\\pictures").mkdir();
        }
        
        scheduler.scheduleAtFixedRate(new GraphTask(), 0, 1, TimeUnit.HOURS);
        scheduler.scheduleAtFixedRate(new TokenTask(), 0, 1, TimeUnit.DAYS);
    }
    
    class GraphTask implements Runnable {
        @Override
        public void run() {
        	try {
				Connection con = EnhemsDB.getDatasource().getConnection();
				SQLConnectionProvider.setConnection(con);
			} catch (SQLException e) {
				e.printStackTrace();
			}
            try {
            	logger.info("Executing graphtask");
                GraphJob.Execute(new Timestamp(System.currentTimeMillis()));
            } catch (IOException | SQLException ex) {
            	logger.fatal("IO problem with graph task");
            } catch (ClassNotFoundException ex) {
            	logger.fatal("Exception during graph task");
            }
            SQLConnectionProvider.setConnection(null);
        }
    }
    
    class TokenTask implements Runnable {
    	
        @Override
        public void run() {
            TokenRep.ExpirationCheck();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    	

    	scheduler.shutdownNow();
    	try {
			scheduler.awaitTermination(90, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	DateUtils.clearThreadLocal();
    	try {
			AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	// This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                logger.info(String.format("deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
            	logger.info(String.format("Error deregistering jdbc driver: %s", driver));
            }

        }
    	sce.getServletContext().setAttribute("enhems.dbpool", null);
    }

}

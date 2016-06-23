package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.sql.DataSource;

import org.apache.log4j.Logger;


@WebFilter(filterName="f1",urlPatterns={"/*"})
public class ConnectionSetterFilter implements Filter {
	
	Logger logger = Logger.getLogger(ConnectionSetterFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	@Override
	public void destroy() {
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		DataSource ds = (DataSource)request.getServletContext().getAttribute("enhems.dbpool");
		Connection con = null;
		try {
			logger.info("Getting connection, datasource:"+ds.toString());
			con = ds.getConnection();
			logger.info("Get connection succesfull");
		} catch (SQLException e) {
			throw new IOException("Baza podataka nije dostupna.", e);
		}
		SQLConnectionProvider.setConnection(con);
		try {
			chain.doFilter(request, response);
		} finally {
			SQLConnectionProvider.setConnection(null);
			try { con.close(); logger.info("Killed connection"); } catch(SQLException ignorable) {}
		}
	}
	
}

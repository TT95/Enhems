/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;


import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author Stjepan
 */
public class EnhemsDB {
    
    private static DataSource datasource;
    
    public static DataSource getDatasource() {
		return datasource;
	}

    public static DataSource initDataSource() {
    	BasicDataSource  ds= new BasicDataSource ();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://161.53.68.191:3306/enhems");
        ds.setUsername("enhems");
        ds.setPassword("07338562");
        datasource=ds;
        return ds;
    }
}

package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dao.models.Unit;
import dao.models.User;
import graphjob.DataProcessor;
import web.Model.QueryBuilder;

public class SQLDao {

	public static List<Unit> getUnitNames() {

		List<Unit> units = new ArrayList<>();
		Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("SELECT * FROM enhems.unit_names;");
			try {
				ResultSet rs = pst.executeQuery();
				try {
					while(rs!=null && rs.next()) {
						Unit unit = new Unit();
						unit.setId(rs.getInt(1));
						unit.setName(rs.getString(2));
						units.add(unit);

					}
				} finally {
					try { rs.close(); } catch(Exception ignorable) {}
				}
			} finally {
				try { pst.close(); } catch(Exception ignorable) {}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return units;
	}
	
	
	public static Unit getUnit(String name) {

		Unit unit = new Unit();
		Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("SELECT * FROM enhems.unit_names WHERE Unit_Name='"+name+"'");
			try {
				ResultSet rs = pst.executeQuery();
				try {
					while(rs!=null && rs.next()) {
						unit.setId(rs.getInt(1));
						unit.setName(rs.getString(2));
					}
				} finally {
					try { rs.close(); } catch(Exception ignorable) {}
				}
			} finally {
				try { pst.close(); } catch(Exception ignorable) {}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return unit;
	}

	public static User getUser(int id) {

		String username=null;
		Set<Unit> units = new LinkedHashSet<>();
		
		Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("SELECT * FROM enhems.username_table2 JOIN enhems.unit_names "
					+ "ON enhems.username_table2.Unit_ID=enhems.unit_names.Unit_ID WHERE User_ID=" + id + ";");
			try {
				ResultSet rs = pst.executeQuery();
				try {
					while(rs!=null && rs.next()) {
						username = rs.getString("Username");
						Unit unit = new Unit(rs.getInt("Unit_ID"), rs.getString("Unit_Name"));
						units.add(unit);
					}
				} finally {
					try { rs.close(); } catch(Exception ignorable) {}
				}
			} finally {
				try { pst.close(); } catch(Exception ignorable) {}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return new User(username, id, units);
	}
	
	
	public static User getUser(String username) {

		Integer id = null;
		Set<Unit> units = new LinkedHashSet<>();
		
		Connection con = SQLConnectionProvider.getConnection();

		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("SELECT * FROM enhems.username_table2 JOIN enhems.unit_names "
					+ "ON enhems.username_table2.Unit_ID=enhems.unit_names.Unit_ID "
					+ "WHERE trim(TRAILING char(13) FROM Username) LIKE '" + username + "';");
			try {

				ResultSet rs = pst.executeQuery();

				try {
					while(rs!=null && rs.next()) {
						id = rs.getInt("User_ID");
						units.add(new Unit(rs.getInt("Unit_ID"), rs.getString("Unit_Name")));
					}
				} finally {
					try { rs.close(); } catch(Exception ignorable) {}
				}
			} finally {
				try { pst.close(); } catch(Exception ignorable) {}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return new User(username, id, units);
	}
	
	public static Map<Integer, Double> UnitQuery(String sqlStatement, Timestamp current) {
		Connection con = SQLConnectionProvider.getConnection();
		Map<Integer, Double> graphData=null;
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sqlStatement);
			try {
				ResultSet rs = pst.executeQuery();
				try {
					if (!rs.first()) {
						return null;
					}
					graphData =  DataProcessor.Process(rs, current);
				} finally {
					try { rs.close(); } catch(Exception ignorable) {}
				}
			} finally {
				try { pst.close(); } catch(Exception ignorable) {}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return graphData;
	}
	
	public static String getOccupancyForUser(int userId) {

		String jsonData=null;
		Connection con = SQLConnectionProvider.getConnection();

		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("SELECT user_occupancy_profile FROM enhems.user_occupancy "
					+ "WHERE User_ID=" + userId + ";");
			try {

				ResultSet rs = pst.executeQuery();

				try {
					if (rs.first()) {
		                jsonData = rs.getString(1);
		            } else {
		                throw new SQLException();
		            }
				} finally {
					try { rs.close(); } catch(Exception ignorable) {}
				}
			} finally {
				try { pst.close(); } catch(Exception ignorable) {}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return jsonData;
	}
	
	
	public static void setOccupancyForUser(int userId, String jsonData) {

		Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("UPDATE enhems.user_occupancy SET user_occupancy_profile='"
		+ jsonData + "' WHERE User_ID=" + userId + ";");
			try {
				
				pst.executeUpdate();

			} finally {
				try { pst.close(); } catch(Exception ignorable) {}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void setSetpoint(int userId, int setpoint, String unitName) throws SQLException {

		Unit unit = SQLDao.getUnit(unitName);
		Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("INSERT INTO enhems.slave_setpoint (User_ID,s_setpoint,Unit_ID) VALUES ("
		+ userId + "," + setpoint + "," + unit.getId() + ");");
			try {
				
				pst.executeUpdate();

			} finally {
				try { pst.close(); } catch(Exception ignorable) {}
			}
		} catch(SQLException ex) {
			throw ex;
		}
	}
	
	public static void setFCspeed(int userId, int fcspeed, String unitName) throws SQLException {

		Unit unit = SQLDao.getUnit(unitName);
		Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("INSERT INTO enhems.fan_speed_limit (User_ID,fan_speed_limit,Unit_ID)"
            		+ " VALUES (" + userId + "," + fcspeed + "," + unit.getId() + ");");
			try {
				
				pst.executeUpdate();

			} finally {
				try { pst.close(); } catch(Exception ignorable ) { }
			}
		} catch(SQLException ex) {
			throw ex;
		}
	}
	
	public static String[] attributeValues(String roomName) {
		
		String[] attributes = new String[]{"Tqax", "Hzgb", "CO2zgb", "s_setpoint",
				"Op_mode", "Q", "fan_speed_limit", "Tf"};
        String[] currentValues = new String[attributes.length];
        Unit unit = SQLDao.getUnit(roomName);
        
        int i = 0;
        String[] queries = QueryBuilder.BuildQueries(unit.getId(), attributes);
        
        Connection con = SQLConnectionProvider.getConnection();
		PreparedStatement pst = null;
		
        for (String query : queries) {
        	
    		try {
    			pst = con.prepareStatement(query);
    			try {

    				ResultSet currentValue = pst.executeQuery();
    				try {
    					
    	                   if (currentValue.first()) {
    	                        String value=currentValue.getString(1);
    	                        if (value==null || value.isEmpty() || value.equals("0")) { 
    	                        	//this if was added later to code for purpose of controling fcspeed, it can be zero
    	                        	if(!attributes[i].equals("fan_speed_limit")) {
    	                        		currentValues[i++]="---";
    	                        		continue;
    	                        	}
    	                        }
    	                        switch (i) {
    	                            case 0:
    	                                currentValues[i++] = String.valueOf(currentValue.getDouble(1)) + "°C";
    	                                break;
    	                            case 1:
    	                                currentValues[i++] = String.valueOf(currentValue.getInt(1)) + "%";
    	                                break;
    	                            case 2:
    	                                currentValues[i++] = String.valueOf(currentValue.getInt(1)) + "ppm";
    	                                break;
    	                            case 3:
    	                                currentValues[i++] = String.valueOf(currentValue.getInt(1)) + "°C";
    	                                break;
    	                            case 4:
    	                                if (currentValue.getInt(1) != 1) {
    	                                    currentValues[i++] = "---";
    	                                } else {
    	                                    currentValues[i++] = "1";
    	                                }
    	                                break;
    	                            case 5:
    	                                if (currentValue.getDouble(1) >= 0.5) {
    	                                    currentValues[i++] = "1";
    	                                } else {
    	                                    currentValues[i++] = "---";
    	                                }
    	                                break;
    	                            case 6:
    	                                currentValues[i++] = String.valueOf(currentValue.getInt(1));
    	                                break;
    	                            case 7:
    	                                currentValues[i++] = String.valueOf(currentValue.getInt(1)) + "°C";
    	                                break;
    	                        }
    	                    } else {
    	                    	//this was made because there is possibility there is no row in 
    	                    	//database for this attribute
    	                    	//whole method should remodeled! bad coding
    	                    	if(attributes[i].equals("fan_speed_limit")) {
    	                    		 currentValues[i++] = "0";
    	                    	}
    	                    	// if there is no attribute for specific unit "---" will be sent
    	                    	else {
    	                    		currentValues[i++] = "---";
    	                    	}
    	                }
    					
    				} finally {
    					try { currentValue.close(); } catch(Exception ignorable) {}
    				}
    			} finally {
    				try { pst.close(); } catch(Exception ignorable) {}
    			}
    		} catch(Exception ex) {
    			ex.printStackTrace();
    		}
        	
        }
        return currentValues;
	}
	
	
}

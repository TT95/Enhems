/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.Model;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import dao.SQLDao;
import dao.models.Unit;

/**
 *
 * @author Stjepan
 */
public class QueryBuilder {

    /**
     * @param unit ResultSet containing one unit
     * @param end execution time timestamp
     * @return Array of queries for given unit, and timestamp
     * @throws SQLException
     */
    @SuppressWarnings("deprecation")
	static public String[] BuildQueries(Unit unit, Timestamp end) throws SQLException {
        String[] atributes;
        int id = unit.getId();
        String name = unit.getName();
        String table;
        if (name.endsWith("-S") || name.endsWith("-N")) {
            atributes = new String[]{"P"};
            table = "enhems.heat_meass";
        } else if (name.startsWith("Power")) {
            atributes = new String[]{"Ptot"};
            table = "enhems.power_meass";
        } else if (name.endsWith("FER")) {
            atributes = new String[]{"Idir", "Idiff"};
            table = "enhems.meteo_meass";
        } else {
            atributes = new String[]{"Tqax", "Hzgb", "CO2zgb"};
            table = "enhems.measured_vars";
        }
        String[] queries = new String[atributes.length];
        int i = 0;
        Timestamp start = new Timestamp(end.getTime() - (6 * 24 * 3600 * 1000));
        start = new Timestamp(start.getYear(), start.getMonth(), start.getDate(), 0, 0, 0, 0);
        for (String atribute : atributes) {
            String whereClause = " WHERE Unit_ID=" + id + " AND Timestamp BETWEEN'" + start.toString() + "' AND '" + end.toString() + "' AND " + atribute + " IS NOT NULL AND " + atribute + "!=0";
            queries[i++] = "SELECT Timestamp," + atribute + " FROM " + table + whereClause + " ORDER BY Timestamp ASC;";
        }
        return queries;
    }

    /**
     * @param roomID id of the room for which to build the queries
     * @param atributes array of attributes
     * @return Array of queries for given user and attribute array
     */
    static public String[] BuildQueries(Unit unit, String[] atributes) {
        String[] queries = new String[atributes.length];
        String table;
        String whereClause;
        int i = 0;
        for (String atribute : atributes) {
            switch (atribute) {
            	case "Tf":
                table = "enhems.heat_meass";
                whereClause = " WHERE Unit_ID = " + matchingMediumForRoom(unit.getName());
                break;
                case "s_setpoint":
                    table = "enhems.slave_setpoint";
                    whereClause = " WHERE Unit_ID = " + unit.getId();
                    break;
                case "fan_speed_limit":
                    table = "enhems.fan_speed_limit";
                    whereClause = " WHERE Unit_ID = " + unit.getId();
                    break;
                case "Q":
                    table = "enhems.heat_meass";
                    whereClause = " WHERE Unit_ID = 41";
                    break;
                default:
                    table = atribute.equals("Op_mode") ? "enhems.op_modes" : "enhems.measured_vars";
                    whereClause = " WHERE Unit_ID =" + unit.getId();             
                    break;
            }
            queries[i++] = "SELECT " + atribute + " FROM " + table + whereClause + " ORDER BY Timestamp DESC LIMIT 1;";
        }
        return queries;
    }
    
    /**
     * Returns name of unit which has medium temperature for given room.
     * (Kalorimetar koji ima ID 41 mjeri snagu soba s  IDjem [3,4...,15], a ID 42 je za sobe 1,2 i [16,17...,23].)
     * @return
     */
    private static String matchingMediumForRoom(String roomName) {
    	
    	List<String> south = Arrays.asList("03","04","05","06","07","08","09","10","11","12",
    			"13","14","15");
    	String ninthLevel = "C9";
    	
    	String level = roomName.split("-")[0];
    	String position = roomName.split("-")[1].substring(0, 2);
    	
    	if(south.contains(position)) {
    		if(level.equals(ninthLevel)) {
    			return "41";
    		} else {
    			return "42";
    		}
    	} else {
    		if(level.equals(ninthLevel)) {
    			return "43";
    		} else {
    			return "44";
    		}
    	}
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web.Model;

import java.sql.SQLException;
import java.sql.Timestamp;

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
    static public String[] BuildQueries(int roomID, String[] atributes) {
        String[] queries = new String[atributes.length];
        String table;
        String whereClause;
        int i = 0;
        for (String atribute : atributes) {
            switch (atribute) {
                case "s_setpoint":
                    table = "enhems.slave_setpoint";
                    whereClause = " WHERE Unit_ID = " + roomID;
                    break;
                case "fan_speed_limit":
                    table = "enhems.fan_speed_limit";
                    whereClause = " WHERE Unit_ID = " + roomID;
                    break;
                case "Q":
                    table = "enhems.heat_meass";
                    whereClause = " WHERE Unit_ID = 41";
                    break;
                default:
                    table = atribute.equals("Op_mode") ? "enhems.op_modes" : "enhems.measured_vars";
                    whereClause = " WHERE Unit_ID =" + roomID;             
                    break;
            }
            queries[i++] = "SELECT " + atribute + " FROM " + table + whereClause + " ORDER BY Timestamp DESC LIMIT 1;";
        }
        return queries;
    }
}

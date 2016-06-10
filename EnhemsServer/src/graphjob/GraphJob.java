/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphjob;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dao.SQLDao;
import dao.models.Unit;
import web.Model.QueryBuilder;

/**
 *
 * @author Stjepan
 */
public class GraphJob {

    /**
     * Executes the graphjob, generates all graphs
     *
     * @param current execution time timestamp
     * @throws java.io.IOException
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
	public static void Execute(Timestamp current) throws IOException, SQLException, ClassNotFoundException {
		Map<Integer, Double> graphData;
		Map<Integer, Double> graphDataL24;
		@SuppressWarnings("unchecked")
		Map<Integer, Double>[] graphDatas =  new Map[2];

		List<Unit> units = SQLDao.getUnitNames();
		for(Unit unit : units) {	
			String[] queries = QueryBuilder.BuildQueries(unit, current);
			for (String query : queries) {
					graphData = SQLDao.UnitQuery(query, current);
					if(graphData == null) {
						continue;
					}
					int size = graphData.size();
					graphDataL24 = Skip(graphData, size - 24);
					graphDatas[0] = graphData;
					graphDatas[1] = graphDataL24;
					String seriesname = GetSeriesName(query);
					Map<Double, String>[] xLabels = GetXLabels(current);
					String[] filenames = GetFileNames(query);
					for (int i = 0; i < 2; i++) {
						try (Graph graph = new Graph(graphDatas[i], seriesname, xLabels[i])) {
							graph.Save(filenames[i]);
						}
					}
					graphData.clear();
					graphDataL24.clear();
				
			}
		}
	}


    /**
     * @param query SQL query for some attribute
     * @return the series name for given SQL query
     */
    private static String GetSeriesName(String query) {
        if (query.contains("Tqax")) {
            return "Temperatura[°C]";
        } else if (query.contains("Hzgb")) {
            return "Vlažnost[%]";
        } else if (query.contains("CO2zgb")) {
            return "CO2[ppm]";
        } else if (query.contains("Ptot")) {
            return "Snaga[kW]";
        } else if (query.contains("Idir")) {
            return "Direktno solarno zračenje[W]";
        } else if (query.contains("Idiff")) {
            return "Difuzno solarno zračenje[W]";
        } else {
            return "Snaga[W]";
        }
    }

    /**
     * @param lastKey key of the last value in graph data
     * @return array of X-axis labels for last week graph[0] and last 24 hour
     * graph[1]
     */
    @SuppressWarnings("deprecation")
	private static Map<Double, String>[] GetXLabels(Timestamp current) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
		Map<Double, String>[] xLabels = new HashMap[]{new HashMap(), new HashMap()};
        int firstDay = (current.getDay() + 1) % 7;
        int firstHour = (current.getHours() + 1) % 24;
        double i = 0;
        for (int j = 0; j < 7; j++) {
            int day = (firstDay + j) % 7;
            switch (day) {
                case 0:
                    xLabels[0].put(i, "Ned");
                    break;
                case 1:
                    xLabels[0].put(i, "Pon");
                    break;
                case 2:
                    xLabels[0].put(i, "Uto");
                    break;
                case 3:
                    xLabels[0].put(i, "Sri");
                    break;
                case 4:
                    xLabels[0].put(i, "Čet");
                    break;
                case 5:
                    xLabels[0].put(i, "Pet");
                    break;
                case 6:
                    xLabels[0].put(i, "Sub");
                    break;
            }
            i += 24;
        }
        for (int k = 0; k < 24; k++) {
            int hour = (firstHour + k) % 24;
            xLabels[1].put((double) k, String.valueOf(hour) + "h");
        }
        return xLabels;
    }

    /**
     * @param query SQL query for some attribute
     * @return array of filenames for last week graph[0] and last 24 hour
     * graph[1]
     */
    private static String[] GetFileNames(String query) {
        String id = query.split("Unit_ID=")[1].split(" ")[0];
        String atribute = query.split(",")[1].split(" ")[0];
        String filename = "C:\\pictures\\" + atribute + id;
        return new String[]{filename + "LW.png", filename + "L24.png"};
    }

    /**
     * @param <K> key type
     * @param <V> value type
     * @param source source map
     * @param offset number of elements to skip
     * @return new map without the skipped elements, equal to offset, from the
     * source map
     */
    private static <K, V> Map<K, V> Skip(Map<K, V> source, int offset) {
        Map<K, V> result = new LinkedHashMap<>();
        int i = 1;
        for (Map.Entry<K, V> entry : source.entrySet()) {
            if (i > offset) {
                result.put(entry.getKey(), entry.getValue());
            }
            i++;
        }
        return result;
    }
}

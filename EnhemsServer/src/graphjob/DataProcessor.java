/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphjob;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Stjepan
 */
public class DataProcessor {

    /**
     * @param data ResultSet of unprocessed data
     * @param current execution time timestamp
     * @return map of processed data, key->time xyy where x is week and yy hour,
     * value-> average value for given time key
     * @throws SQLException
     */
    @SuppressWarnings("deprecation")
	public static Map<Integer, Double> Process(ResultSet data, Timestamp current) throws SQLException {
        Map<Integer, Double> resultData = FillDefault(current);
        data.first();
        double sum = data.getDouble(2);
        int counter = 1;
        Timestamp control = data.getTimestamp(1);
        while (data.next()) {
            Timestamp time = data.getTimestamp(1);
            if (time.getHours() != control.getHours()) {
                resultData.put(control.getDay() * 100 + control.getHours(), sum / counter);
                control = time;
                sum = data.getDouble(2);
                counter = 1;
                continue;
            }
            sum += data.getDouble(2);
            counter++;
        }
        resultData.put(control.getDay() * 100 + control.getHours(), sum / counter);
        return resultData;
    }

    /**
     * @param time execution time timestamp
     * @return map filled with default values, zeroes
     */
    @SuppressWarnings("deprecation")
	private static Map<Integer, Double> FillDefault(Timestamp time) {
        Map<Integer, Double> defaultFill = new LinkedHashMap<>();
        int start = (time.getDay() + 1) % 7;
        for (int i = 0; i < 7; i++) {
            int hourRange = 24;
            if (i == 6) {
                hourRange = time.getHours() + 1;
            }
            for (int j = 0; j < hourRange; j++) {
                defaultFill.put(((start + i) % 7) * 100 + j, 0.0);
            }
        }
        return defaultFill;
    }
}

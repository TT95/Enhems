package fingerprintwifi;

import java.util.Map;

/**
 * Created by TeoLenovo on 6/1/2017.
 */
public class WifiLocation {

    private static String[] PATH_TO_FILES = new String[]{
            "hometest/RSSFingerPrintTeoDoma.txt",
            "hometest/RSSFingerPrintTomislavDoma.txt"
    };

    public static void main(String[] args) {
        locate(PATH_TO_FILES);
    }

    public static String locate(String[] pathToFiles) {
        Map<String, Map<String, Double>> fingerprintMeanMap = PreProcessor.meanValuesMap(pathToFiles);
        System.out.println(fingerprintMeanMap);
        return null;
    }
}

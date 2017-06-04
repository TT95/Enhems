package fingerprints;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TeoLenovo on 6/1/2017.
 */
public class WifiLocation {

    private static String[] PATH_TO_FILES = new String[]{
            WifiLocation.class.getClassLoader().getResource(
                    "fingerprints/hometest/RSSFingerPrintTeoDoma.txt").getPath(),
            WifiLocation.class.getClassLoader().getResource(
                    "fingerprints/hometest/RSSFingerPrintTomislavDoma.txt").getPath()
    };

    private static Map<String, Map<String, Double>> fingerprintMeanMap;

    private static Double SIGMA = 0.4;

    public static void main(String[] args) {

        Map<String,Integer> rsssUser = new HashMap<>();
        rsssUser.put("ec:8a:4c:92:57:10", -86);
        rsssUser.put("84:16:f9:01:e5:ae", -77);
        rsssUser.put("fc:b4:e6:7b:4f:f2", -84);
        rsssUser.put("cc:03:fa:e4:a0:8b", -55);
        rsssUser.put("78:96:82:20:df:f2", -64);
        rsssUser.put("70:5a:9e:a8:f6:3e", -82);
        rsssUser.put("8c:79:67:9d:63:1e", -86);
        rsssUser.put("88:f7:c7:4f:0d:bb", -74);
        rsssUser.put("00:18:9b:6c:c9:78", -84);
        String room = locate(rsssUser);
        System.out.println(room);

    }

    public static String locate(Map<String,Integer> rsssUser) {

        //initialize if doesnt exist
        //TODO put in server initializer
        if (fingerprintMeanMap == null) {
            fingerprintMeanMap = PreProcessor.meanValuesMap(PATH_TO_FILES);
        }


        Map<String, Double> likehoods = new HashMap<>();

        for (Map.Entry<String, Map<String, Double>> fingerprintMeanEntry : fingerprintMeanMap.entrySet()) {
            String room = fingerprintMeanEntry.getKey();
            Map<String, Double> meanValuesMap = fingerprintMeanEntry.getValue();
            likehoods.put(room, gaussKernel(rsssUser, meanValuesMap));
        }

        //find biggest number
        String matchingRoom = "";
        Double likehoodValue = 0.0;

        for (Map.Entry<String, Double> likehoodEntry : likehoods.entrySet()) {
            if (likehoodValue < likehoodEntry.getValue()) {
                likehoodValue = likehoodEntry.getValue();
                matchingRoom = likehoodEntry.getKey();
            }
        }
        System.out.println(likehoods);
        if (likehoodValue == 0.0) {
            return "Not fingerprint space!";
        }
        return matchingRoom;
    }

    /**
     * Method implements Gauss RBF function
     * @param rsssUser vector of rss's from user
     * @param rsssMean vector
     * @return likehood value
     */
    private static Double gaussKernel(Map<String,Integer> rsssUser, Map<String,Double> rsssMean) {
        Double likehood = 0.0;
        for (Map.Entry<String, Double> rssMeanEntry : rsssMean.entrySet()) {
            String mac = rssMeanEntry.getKey();
            Double rssMean = rssMeanEntry.getValue();
            Integer rssUser = rsssUser.get(mac);
            if (rssUser == null) {
                continue;
            }
            likehood +=
                    (1 / (Math.sqrt(2 * Math.PI) * SIGMA))
                            * Math.exp((-1) * ((Math.pow(rssMean - rssUser, 2)) / (2 * Math.pow(SIGMA, 2))));
        }
        return likehood;
    }

    /*
        Tinas room
        rsssUser.put("78:8c:54:03:14:d7", -84);
        rsssUser.put("14:60:80:78:40:d6", -81);
        rsssUser.put("70:5a:9e:a9:02:91", -84);
        rsssUser.put("fc:52:8d:76:52:a2", -83);
        rsssUser.put("fc:b4:e6:7b:4f:f2", -82);
        rsssUser.put("cc:03:fa:e4:a0:8b", -68);
        rsssUser.put("70:5a:9e:a8:f6:3e", -78);
        rsssUser.put("08:95:2a:5e:c2:83", -87);
        rsssUser.put("f4:cb:52:5b:91:b0", -84);
        rsssUser.put("b4:a5:ef:aa:b5:be", -83);
        rsssUser.put("2a:28:5d:40:07:24", -87);
        rsssUser.put("88:f7:c7:4f:0d:bb", -86);
        rsssUser.put("94:4a:0c:53:d5:d6", -82);
        rsssUser.put("00:18:9b:6c:c9:78", -85);

        Hall up
        rsssUser.put("ec:8a:4c:92:57:10", -86);
        rsssUser.put("84:16:f9:01:e5:ae", -77);
        rsssUser.put("fc:b4:e6:7b:4f:f2", -84);
        rsssUser.put("cc:03:fa:e4:a0:8b", -55);
        rsssUser.put("78:96:82:20:df:f2", -64);
        rsssUser.put("70:5a:9e:a8:f6:3e", -82);
        rsssUser.put("8c:79:67:9d:63:1e", -86);
        rsssUser.put("88:f7:c7:4f:0d:bb", -74);
        rsssUser.put("00:18:9b:6c:c9:78", -84);


     */


}

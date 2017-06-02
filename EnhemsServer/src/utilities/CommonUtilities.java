package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by TeoLenovo on 3/25/2017.
 */
public class CommonUtilities {


    private static final String propFileName = "serverConf.properties";
    private static Properties prop = new Properties();
    private static InputStream input = null;

    public static Properties getServerConf() {
        try {
            input = CommonUtilities.class.getClassLoader().getResourceAsStream(propFileName);
            prop.load(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return prop;
        }
    }

    public static String getPathToResourceFile(String propFileName) {
        return  CommonUtilities.class.getClassLoader().getResource(propFileName).getPath();
    }
}

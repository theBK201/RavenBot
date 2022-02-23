import java.io.*;
import java.util.Properties;

public class botToken {
    public String getPropValues() throws IOException {
        String token;
        Properties prop = new Properties();
        String configFile = "botToken.config";

        FileInputStream inputStream = new FileInputStream(configFile);
        prop.load(inputStream);
        token = prop.getProperty("token");
        return token;
    }
}

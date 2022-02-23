package dev.bk201.RavenBot;

import java.io.*;
import java.util.Properties;

public class botToken {
    public String getPropValues() throws IOException {
        String token;
        Properties prop = new Properties();
        String configFile = "botToken.config";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try(InputStream resourcesStream = loader.getResourceAsStream(configFile)) {
            prop.load(resourcesStream);
        }
        token = prop.getProperty("token");
        return token;
    }
}

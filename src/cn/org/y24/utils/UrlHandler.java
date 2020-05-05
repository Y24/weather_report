package cn.org.y24.utils;

import cn.org.y24.interfaces.IHandler;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UrlHandler implements IHandler<String, Map<String, String>, Boolean> {
    private BufferedReader reader;

    public BufferedReader getReader() {
        return reader;
    }

    @Override
    public Boolean handle(String target, Map<String, String> options) {
        try {
            if (reader != null)
                reader.close();
            final var connection = new URL(target).openConnection();
            options.forEach(connection::addRequestProperty);
            reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(),
                            StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void dispose() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader = null;
        }
    }
}

package mn.csm311.lab12.task3;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileConfigLoader {

    public AppConfig load(Path path) {
        // TODO 3.1: path null бол fail-fast (IllegalArgumentException)
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }

        Map<String, String> props = new HashMap<>();

        // TODO 3.2: try-with-resources ашиглан Reader-ыг нээх
        // Ингэснээр close() автоматаар дуудагдана (Асуудал B-г шийднэ)
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line, props);
            }
        } 
        // TODO 3.3: NoSuchFileException-ыг тусад нь барих (Асуудал D)
        catch (NoSuchFileException e) {
            throw new ConfigLoadException("config file not found: " + path, e);
        } 
        // TODO 3.4: Бусад IOException-ыг ConfigLoadException болгон хөрвүүлэх (Асуудал C)
        catch (IOException e) {
            throw new ConfigLoadException("failed to read config: " + path, e);
        }

        return new AppConfig(props);
    }

    private void parseLine(String line, Map<String, String> props) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) return;
        int eq = line.indexOf('=');
        if (eq <= 0) return;
        String key = line.substring(0, eq).trim();
        String value = line.substring(eq + 1).trim();
        props.put(key, value);
    }
}
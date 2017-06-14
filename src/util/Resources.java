package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Resources {

    private static ClassLoader classLoader = Resources.class.getClassLoader();

    public static String getResource(String path) {
        try {
            return Files.readAllLines(Paths.get(path)).stream().reduce("", (a, b) -> a + "\n" + b);
//        File file = new File(classLoader.getResource(path).getFile());
//        StringBuilder result = new StringBuilder("");
//        try (Scanner scanner = new Scanner(file)) {
//            while (scanner.hasNextLine()) {
//                String line = scanner.nextLine();
//                result.append(line).append("\n");
//            }
//            scanner.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

}

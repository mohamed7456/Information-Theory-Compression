import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class FileManager {
    public static void ensureDirs() {
        String[] dirs = {
            "output/codebooks/rgb",
            "output/reconstructed/rgb",
            "output/logs"
        };
        for (String dir : dirs) {
            File d = new File(dir);
            if (!d.exists()) {
                d.mkdirs();
            }
        }
    }

    public static void saveCSV(String path, List<String[]> rows) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String[] row : rows) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
    }

    public static void saveImage(BufferedImage img, String path) throws IOException {
        ImageProcessorVQ.saveImage(img, path);
    }
    public static void saveCodebook(double[][] codebook, String path) throws IOException {
        VectorQuantizer.saveCodebook(codebook, path);
    }
    public static double[][] loadCodebook(String path) throws IOException, ClassNotFoundException {
        return VectorQuantizer.loadCodebook(path);
    }
}
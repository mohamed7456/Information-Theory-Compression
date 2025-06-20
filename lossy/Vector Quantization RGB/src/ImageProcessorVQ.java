import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageProcessorVQ {
    public static BufferedImage readImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Error reading image from path: " + path);
            return null;
        }
    }

    public static double[][][][] divideIntoBlocks(BufferedImage image, int blockSize) {
        int croppedWidth = (image.getWidth() / blockSize) * blockSize;
        int croppedHeight = (image.getHeight() / blockSize) * blockSize;
        if (croppedWidth != image.getWidth() || croppedHeight != image.getHeight()) {
            image = image.getSubimage(0, 0, croppedWidth, croppedHeight);
        }
        int horiz = image.getWidth() / blockSize;
        int vert = image.getHeight() / blockSize;
        double[][][][] blocks = new double[vert][horiz][blockSize][blockSize];
        for (int by = 0; by < vert; by++) {
            for (int bx = 0; bx < horiz; bx++) {
                for (int y = 0; y < blockSize; y++) {
                    for (int x = 0; x < blockSize; x++) {
                        int pixel = image.getRGB(bx*blockSize + x, by*blockSize + y) & 0xFF;
                        blocks[by][bx][y][x] = pixel;
                    }
                }
            }
        }
        return blocks;
    }

    public static BufferedImage reconstructImageFromBlocks(double[][][][] blocks, int width, int height, int blockSize) {
        int horiz = width / blockSize;
        int vert = height / blockSize;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int by = 0; by < vert; by++) {
            for (int bx = 0; bx < horiz; bx++) {
                for (int y = 0; y < blockSize; y++) {
                    for (int x = 0; x < blockSize; x++) {
                        int gray = (int) blocks[by][bx][y][x];
                        int rgb = (gray << 16) | (gray << 8) | gray;
                        img.setRGB(bx*blockSize + x, by*blockSize + y, rgb);
                    }
                }
            }
        }
        return img;
    }

    public static void saveImage(BufferedImage image, String path) {
        try {
            String fmt = path.substring(path.lastIndexOf('.')+1);
            ImageIO.write(image, fmt, new File(path));
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

    public static BufferedImage resize(BufferedImage original, int maxW, int maxH) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        double aspectRatio = (double) originalWidth / originalHeight;
        int newWidth = maxW;
        int newHeight = maxH;

        if (originalWidth > originalHeight) {
            newHeight = (int) (maxW / aspectRatio);
        } else {
            newWidth = (int) (maxH * aspectRatio);
        }

        Image tmp = original.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newWidth, newHeight, original.getType());
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    public static double[][] getChannelMatrix(BufferedImage image, int channel) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] matrix = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int value = (channel == 0) ? (rgb >> 16) & 0xFF : (channel == 1) ? (rgb >> 8) & 0xFF : rgb & 0xFF;
                matrix[y][x] = value;
            }
        }
        return matrix;
    }
}

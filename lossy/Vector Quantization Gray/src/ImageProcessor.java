import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
 

public class ImageProcessor {
    // read image from giiven path
    public static BufferedImage readImage(String path) {
        try {
            File file = new File(path);
            return ImageIO.read(file);
        } 
        catch (IOException e) {
            System.err.println("Error reading image from path: " + path);
            return null;
        }
    }

    // conver RGB image into Grayscale  
    public static BufferedImage convertToGrayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
    
                int gray = (int)(0.299 * red + 0.587 * green + 0.114 * blue);
                int newPixel = new Color(gray, gray, gray).getRGB();
                grayImage.setRGB(x, y, newPixel);
            }
        }
    
        return grayImage;
    }


    // divide grayscale image into non-overlapping blocks
    /*
     * bx-> 0    1   2 ....
     * by  __________
     * 0 | 0,0  0,1|
     * 1 | 1,0  1,1|
     * 2  __________ 
     * :
     */
    public static double[][][][] divideIntoBlocks(BufferedImage image, int blockSize) {
        // ensure dimensions is divisible by block size or crop image
        int croppedWidth = (image.getWidth() / blockSize) * blockSize;
        int croppedHeight = (image.getHeight() / blockSize) * blockSize;
        if (croppedWidth != image.getWidth() || croppedHeight != image.getHeight()) {
            image = image.getSubimage(0, 0, croppedWidth, croppedHeight);
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int horizontalBlocks = width / blockSize;
        int verticalBlocks = height / blockSize;
        double[][][][] result = new double[verticalBlocks][horizontalBlocks][blockSize][blockSize];

        for (int by = 0; by < verticalBlocks; by++) {
            for (int bx = 0; bx < horizontalBlocks; bx++) {
                for (int y = 0; y < blockSize; y++) {
                    for (int x = 0; x < blockSize; x++) {
                        int pixelX = bx * blockSize + x;
                        int pixelY = by * blockSize + y;
    
                        int rgb = image.getRGB(pixelX, pixelY);
                        int gray = rgb & 0xFF;
    
                        result[by][bx][y][x] = gray;
                    }
                }
            }
        }
        return result;
    }
    
    // reconstruct image from blocks
    public static BufferedImage reconstructImageFromBlocks(double[][][][] blocks, int width, int height, int blockSize) {
        int horizontalBlocks = width / blockSize;
        int verticalBlocks = height / blockSize;
        BufferedImage reconstructedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int by = 0; by < verticalBlocks; by++) {
            for (int bx = 0; bx < horizontalBlocks; bx++) {
                for (int y = 0; y < blockSize; y++) {
                    for (int x = 0; x < blockSize; x++) {
                        int pixelX = bx * blockSize + x;
                        int pixelY = by * blockSize + y;

                        int gray = (int) blocks[by][bx][y][x];
                        int rgb = (gray << 16) | (gray << 8) | gray;
                        reconstructedImage.setRGB(pixelX, pixelY, rgb);
                    }
                }
            }
        }
        return reconstructedImage;
    }

    // save image
    public static void saveImage(BufferedImage image, String path) {
        try {
            File outputFile = new File(path);
            String format = path.substring(path.lastIndexOf(".") + 1);
            ImageIO.write(image, format, outputFile);
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

    public static double[][] getGrayscaleMatrix(BufferedImage grayImage) {
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        double[][] matrix = new double[height][width];
    
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = grayImage.getRGB(x, y) & 0xFF;
                matrix[y][x] = gray;
            }
        }
    
        return matrix;
    }
}

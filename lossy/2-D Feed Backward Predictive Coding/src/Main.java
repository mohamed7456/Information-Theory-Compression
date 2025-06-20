import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ImageProcessor imageProcessor = new ImageProcessor();
            Encoder encoder = new Encoder();
            Decoder decoder = new Decoder();

            System.out.println("--------------2-D Feed Backward Predictive Coding-------------");
            System.out.print("Enter image path: ");
            String imagePath = scanner.nextLine();

            System.out.println("1 for first-order, 2 for second-order, 3 for adaptive");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            String strChoice = "";
            switch (choice) {
                case 1 -> strChoice = "first-order";
                case 2 -> strChoice = "second-order";
                case 3 -> strChoice = "adaptive";
                default -> {
                    System.err.println("Invalid choice. Exiting...");
                    return;
                }
            }

            System.out.print("Enter number of quantization levels (e.g., 8, 16, 32): ");
            int quantizationLevels = scanner.nextInt();
            if (quantizationLevels <= 0) {
                System.err.println("Invalid quantization levels. Please enter a positive integer. Exiting...");
                return;
            }

            // load image
            BufferedImage image = imageProcessor.loadImage(imagePath);
            if (image == null) {
                System.err.println("Failed to load the image. Exiting...");
                return;
            }

            File inputFile = new File(imagePath);
            String inputFileName = inputFile.getName();
            String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + "_" + strChoice + "_" + quantizationLevels + ".png";
            String outputPath = "output/" + outputFileName;

            // convert to grayscale
            BufferedImage grayscaleImage = imageProcessor.convertToGrayscale(image);

            // convert to 2D array
            int[][] pixelArray = imageProcessor.convertTo2DArray(grayscaleImage);

            // encode
            System.out.println("Processing image with choice: " + strChoice + " and quantization levels: " + quantizationLevels);
            int[][] residuals = encoder.encode(pixelArray, choice, quantizationLevels);

            // decode
            int[][] reconstructedImage = decoder.decode(residuals, choice, quantizationLevels);

            // evaluation
            double mse = Utils.calculateMSE(pixelArray, reconstructedImage);
            System.out.println("Mean Squared Error (MSE): " + mse);

            int originalSize = pixelArray.length * pixelArray[0].length * 8;
            int encodedSize = residuals.length * residuals[0].length * (int) (Math.log(quantizationLevels) / Math.log(2));
            double compressionRatio = Utils.calculateCompressionRatio(originalSize, encodedSize);
            System.out.println("Compression Ratio: " + compressionRatio);

            // ensure output directory exists
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // save reconstructed image
            BufferedImage outputImage = imageProcessor.convertToBufferedImage(reconstructedImage, grayscaleImage.getWidth(), grayscaleImage.getHeight());
            imageProcessor.saveImage(outputImage, outputPath);

            System.out.println("Processed image saved to: " + outputPath);
        }
    }
}
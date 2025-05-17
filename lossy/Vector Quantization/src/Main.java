import java.awt.image.BufferedImage;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String inputPath;
        int blockSize;
        int codebookSize;
        String outputPath;
        try (Scanner scanner = new Scanner(System.in)) {
            // inputs
            System.out.print("Enter path to input image: ");
            inputPath = scanner.nextLine();
            System.out.print("Enter block size: ");
            blockSize = scanner.nextInt();
            System.out.print("Enter number of vectors in codebook: ");
            codebookSize = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter path to save output image: ");
            outputPath = scanner.nextLine();
        }

        // read img
        BufferedImage image = ImageProcessor.readImage(inputPath);
        if (image == null) {
            System.out.println("Failed to read image.");
            return;
        }

        BufferedImage grayImage = ImageProcessor.convertToGrayscale(image);
        int width = (grayImage.getWidth() / blockSize) * blockSize;
        int height = (grayImage.getHeight() / blockSize) * blockSize;
        BufferedImage croppedGray = grayImage.getSubimage(0, 0, width, height);

        double[][] originalMatrix = ImageProcessor.getGrayscaleMatrix(croppedGray);
        double[][][][] blocks = ImageProcessor.divideIntoBlocks(croppedGray, blockSize);

        // generate Codebook
        double[][] codebook = VectorQuantizer.generateCodebook(blocks, codebookSize);

        // compress 
        int[][] labels = VectorQuantizer.compress(blocks, codebook); 

        // decompress
        double[][][][] reconstructedBlocks = VectorQuantizer.decompress(labels, codebook, blockSize);

        // reconstruct img
        BufferedImage reconstructedImage = ImageProcessor.reconstructImageFromBlocks(reconstructedBlocks, width, height, blockSize);

        // save img
        ImageProcessor.saveImage(reconstructedImage, outputPath);

        // clculate reconstructed img matrix
        double[][] reconstructedMatrix = ImageProcessor.getGrayscaleMatrix(reconstructedImage);

        // MSE
        double mse = Utils.calculateMSE(originalMatrix, reconstructedMatrix);

        // compression ratio
        Utils.CompressionStats stats = Utils.calculateCompressionRatio(width, height, blockSize, codebookSize);
        System.err.print(" ");
        System.out.printf("Image compressed and saved to: %s\n", outputPath);
        System.out.printf("Mean Squared Error (MSE): %.2f\n", mse);
        System.out.printf("Original Size: %.2f bytes\n", stats.originalSizeBytes);
        System.out.printf("Compressed Size: %.2f bytes\n", stats.compressedSizeBytes);
        System.out.printf("Compression Ratio: %.2f\n", stats.ratio);
    }
}
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        final int blockSize = 2;
        final int codebookSize = 256;
        final int maxIterations = 20;
        final int MAX_BLOCKS = 50000;
        final String trainDir = "lossy/Vector Quantization RGB/Images/training_images";
        final String testDir = "lossy/Vector Quantization RGB/Images/test_images";

        try {
            FileManager.ensureDirs();

            // training
            System.out.println("Training...");
            System.out.println("Collecting blocks from training images...");
            List<double[][]> redBlocks   = new ArrayList<>();
            List<double[][]> greenBlocks = new ArrayList<>();
            List<double[][]> blueBlocks  = new ArrayList<>();

            File[] trainFiles = new File(trainDir).listFiles();
            if (trainFiles == null) throw new IOException("Training directory not found: " + trainDir);
            for (File f : trainFiles) {
                System.out.println("  - Loading " + f.getName());
                BufferedImage img = ImageProcessorVQ.readImage(f.getPath());

                img = ImageProcessorVQ.resize(img, 512, 512);

                double[][][][] rBlk = VectorQuantizer.extractChannelBlocks(img, blockSize, 0);
                double[][][][] gBlk = VectorQuantizer.extractChannelBlocks(img, blockSize, 1);
                double[][][][] bBlk = VectorQuantizer.extractChannelBlocks(img, blockSize, 2);
                for (double[][][] row : rBlk) for (double[][] blk : row) redBlocks.add(blk);
                for (double[][][] row : gBlk) for (double[][] blk : row) greenBlocks.add(blk);
                for (double[][][] row : bBlk) for (double[][] blk : row) blueBlocks.add(blk);
            }
            System.out.println("Collected R-blocks: " + redBlocks.size());
            System.out.println("Collected G-blocks: " + greenBlocks.size());
            System.out.println("Collected B-blocks: " + blueBlocks.size());

            if (redBlocks.size() > MAX_BLOCKS) {
                Collections.shuffle(redBlocks);
                redBlocks = redBlocks.subList(0, MAX_BLOCKS);
            }
            if (greenBlocks.size() > MAX_BLOCKS) {
                Collections.shuffle(greenBlocks);
                greenBlocks = greenBlocks.subList(0, MAX_BLOCKS);
            }
            if (blueBlocks.size() > MAX_BLOCKS) {
                Collections.shuffle(blueBlocks);
                blueBlocks = blueBlocks.subList(0, MAX_BLOCKS);
            }

            System.out.println("Sampled R-blocks: " + redBlocks.size());
            System.out.println("Sampled G-blocks: " + greenBlocks.size());
            System.out.println("Sampled B-blocks: " + blueBlocks.size());

            // convert lists to 4d arrays 
            double[][][][] rMerged = listTo4D(redBlocks, blockSize);
            double[][][][] gMerged = listTo4D(greenBlocks, blockSize);
            double[][][][] bMerged = listTo4D(blueBlocks, blockSize);

            // generate codebooks
            System.out.println("Generating R codebook...");
            double[][] rCodebook = VectorQuantizer.generateCodebook(rMerged, codebookSize, maxIterations);
            System.out.println("Generating G codebook...");
            double[][] gCodebook = VectorQuantizer.generateCodebook(gMerged, codebookSize, maxIterations);
            System.out.println("Generating B codebook...");
            double[][] bCodebook = VectorQuantizer.generateCodebook(bMerged, codebookSize, maxIterations);

            // save codebooks
            FileManager.saveCodebook(rCodebook, "output/codebooks/rgb/R.cb");
            FileManager.saveCodebook(gCodebook, "output/codebooks/rgb/G.cb");
            FileManager.saveCodebook(bCodebook, "output/codebooks/rgb/B.cb");
            System.out.println("Codebooks saved to output/codebooks/rgb/");

            // testing
            System.out.println("Processing test images...");
            List<String[]> logRows = new ArrayList<>();
            logRows.add(new String[]{"image","ratio","psnr"});

            File[] testFiles = new File(testDir).listFiles();
            if (testFiles == null) throw new IOException("Test directory not found: " + testDir);
            for (File f : testFiles) {
                String name = f.getName();
                System.out.println("  - " + name);
                BufferedImage orig = ImageProcessorVQ.readImage(f.getPath());
                orig = ImageProcessorVQ.resize(orig, 512, 512);

                // compress RGB
                Map<String,int[][]> labels = VectorQuantizer.compressRGB(orig, blockSize, Map.of("R", rCodebook, "G", gCodebook, "B", bCodebook));
                // decompress
                BufferedImage recon = VectorQuantizer.decompressRGB(labels, Map.of("R", rCodebook, "G", gCodebook, "B", bCodebook),blockSize);

                int reconWidth = recon.getWidth();
                int reconHeight = recon.getHeight();
                orig = orig.getSubimage(0, 0, reconWidth, reconHeight);

                // save
                String outPath = "output/reconstructed/rgb/" + name;
                FileManager.saveImage(recon, outPath);

                // evaluate 
                double mseR = Metrics.calculateMSE(
                    ImageProcessorVQ.getChannelMatrix(orig, 0),
                    ImageProcessorVQ.getChannelMatrix(recon, 0)
                );
                double mseG = Metrics.calculateMSE(
                    ImageProcessorVQ.getChannelMatrix(orig, 1),
                    ImageProcessorVQ.getChannelMatrix(recon, 1)
                );
                double mseB = Metrics.calculateMSE(
                    ImageProcessorVQ.getChannelMatrix(orig, 2),
                    ImageProcessorVQ.getChannelMatrix(recon, 2)
                );
                double avgMSE = (mseR + mseG + mseB) / 3;

                Metrics.CompressionStats stats = Metrics.calculateCompressionStats(
                    orig.getWidth(), orig.getHeight(), blockSize, codebookSize, avgMSE
                );

                System.out.printf("MSE (R)=%.3f, MSE (G)=%.3f, MSE (B)=%.3f%n", mseR, mseG, mseB);
                System.out.printf("Compression ratio=%.3f, PSNR=%.2f dB%n", stats.ratio, stats.psnr);
                logRows.add(new String[]{name, String.format("%.3f", stats.ratio),  String.format("%.2f", stats.psnr)});
            }

            // save csv log
            FileManager.saveCSV("output/logs/rgb_stats.csv", logRows);
            System.out.println("Logs saved to output/logs/rgb_stats.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double[][][][] listTo4D(List<double[][]> list, int blockSize) {
        int n = list.size();
        double[][][][] arr = new double[n][1][blockSize][blockSize];
        for (int i = 0; i < n; i++) {
            arr[i][0] = list.get(i);
        }
        return arr;
    }
}

public class Metrics {
    public static double calculateMSE(double[][] original, double[][] reconstructed) {
        int height = original.length;
        int width = original[0].length;
        double sum = 0.0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double diff = original[y][x] - reconstructed[y][x];
                sum += diff * diff;
            }
        }
        return sum / (width * height);
    }

    public static double calculatePSNR(double mse) {
        if (mse == 0) {
            return Double.POSITIVE_INFINITY;
        }
        double maxPixel = 255.0;
        return 10 * Math.log10((maxPixel * maxPixel) / mse);
    }

    public static class CompressionStats {
        public final double originalSizeBytes;
        public final double compressedSizeBytes;
        public final double ratio;
        public final double psnr;

        public CompressionStats(double originalSizeBytes, double compressedSizeBytes, double ratio, double psnr) {this.originalSizeBytes   = originalSizeBytes;
            this.compressedSizeBytes = compressedSizeBytes;
            this.ratio = ratio;
            this.psnr = psnr;
        }

        @Override
        public String toString() {
            return String.format( "Original Size: %.2f bytes, Compressed Size: %.2f bytes%n" + "Compression Ratio: %.3f, PSNR: %.2f dB", originalSizeBytes, compressedSizeBytes, ratio, psnr);
        }
    }


    public static CompressionStats calculateCompressionStats(int width, int height, int blockSize, int codebookSize, double mse) {
        int numBlocks = (width / blockSize) * (height / blockSize);
        int originalBits = width * height * 8;
        int labelBitsPerBlock = (int) Math.ceil(Math.log(codebookSize) / Math.log(2));
        int labelsBitsTotal = numBlocks * labelBitsPerBlock;
        int compressedBits = labelsBitsTotal;
        double originalBytes = originalBits / 8.0;
        double compressedBytes = compressedBits / 8.0;
        double ratio = originalBytes / compressedBytes;
        double psnr = calculatePSNR(mse);

        return new CompressionStats(originalBytes, compressedBytes, ratio, psnr);
    }
}

public class Utils {

    // calculate MSE
    public static double calculateMSE(double[][] original, double[][] reconstructed) {
        int height = original.length;
        int width = original[0].length; 

        double mse = 0.0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double diff = original[y][x] - reconstructed[y][x];
                mse += diff * diff;
            }
        }

        return mse / (width * height);
    }

    // calculate euclidean distance
    public static double euclideanDistance(double[] v1, double[] v2) {
        double sum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            double diff = v1[i] - v2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    // check if no change
    public static boolean areVectorsEqual(double[] v1, double[] v2) {
        double epsilon = 1e-6;
        if (v1.length != v2.length) return false;
        for (int i = 0; i < v1.length; i++) {
            if (Math.abs(v1[i] - v2[i]) > epsilon) return false;
        }
        return true;
    }


    public static class CompressionStats {
        public double originalSizeBytes;
        public double compressedSizeBytes;
        public double ratio;
    
        public CompressionStats(double originalSizeBytes, double compressedSizeBytes, double ratio) {
            this.originalSizeBytes = originalSizeBytes;
            this.compressedSizeBytes = compressedSizeBytes;
            this.ratio = ratio;
        }
    }

    // calculate compression ratio
    public static CompressionStats calculateCompressionRatio(int width, int height, int blockSize, int codebookSize) {
        int numBlocks = (width / blockSize) * (height / blockSize);
    
        // each pixel 8 bits (grayscale)
        int originalSizeBits = width * height * 8;
    
        // codebookSize x blockSize x blockSize x 8 bits
        int codebookSizeBits = codebookSize * blockSize * blockSize * 8;
    
        // bits required to label codebook entries
        int labelBits = (int) Math.ceil(Math.log(codebookSize) / Math.log(2));
    
        // total label bits for all blocks
        int labelsSizeBits = numBlocks * labelBits;
    
        // compressed size in bits
        int compressedSizeBits = codebookSizeBits + labelsSizeBits;
    
        // convert to bytes
        double originalSizeBytes = originalSizeBits / 8.0;
        double compressedSizeBytes = compressedSizeBits / 8.0;
        double ratio = originalSizeBytes / compressedSizeBytes;
    
        return new CompressionStats(originalSizeBytes, compressedSizeBytes, ratio);
    }
    
}
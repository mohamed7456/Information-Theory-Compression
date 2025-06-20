public class Utils {

    // predictor function 
    public static int predict(int[][] data, int i, int j, int predictorType) {
        int prediction = 0;
        switch (predictorType) {
            case 1 -> // first-order
                    prediction = data[i][j - 1];    // Pixel(i, j-1)

            case 2 -> // second-order
                    prediction = data[i - 1][j] + data[i][j - 1] - data[i - 1][j - 1]; // Pixel(i-1, j) + Pixel(i, j-1) - Pixel(i-1, j-1)

            case 3 -> { // adaptive
                int A = data[i][j - 1];
                int C = data[i - 1][j];
                int B = data[i - 1][j - 1];

                if (B <= Math.min(A, C)) {
                    prediction = Math.max(A, C);
                } else if (B >= Math.max(A, C)) {
                    prediction = Math.min(A, C);
                } else {
                    prediction = A + C - B;
                }
            }
            default -> {
            }
        }
        return prediction;
    }

    // calculate step size based on quantization levels
    public static double calculateStepSize(int quantizationLevels) {
        if (quantizationLevels <= 1) {
            // avoid division by zero and handle the no-quantization case
            return 255.0 * 2.0; 
        }
        // the error can range from -255 to +255
        int maxPixelValue = 255;
        double errorRange = 2.0 * maxPixelValue;
        return errorRange / (double) quantizationLevels;
    }

    // quantizer function 
    public static int quantizeError(int error, double stepSize) {
        if (stepSize < 1e-9) { // avoid division by zero
            return 0;
        }
        return (int) Math.round(error / stepSize);
    }

    // dequantizer function
    public static int dequantizeError(int quantizedIndex, double stepSize) {
        return (int) Math.round(quantizedIndex * stepSize);
    }


    // evaluation functions (no changes needed)
    public static double calculateMSE(int[][] original, int[][] reconstructed) {
        int rows = original.length;
        int cols = original[0].length;
        double mse = 0.0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int diff = original[i][j] - reconstructed[i][j];
                mse += diff * diff;
            }
        }
        mse /= (rows * cols);
        return mse;
    }

    public static double calculateCompressionRatio(int originalSize, int encodedSize) {
        if (encodedSize == 0) return Double.POSITIVE_INFINITY;
        return (double) originalSize / encodedSize;
    }
}
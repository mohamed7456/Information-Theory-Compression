public class Decoder {
    public int[][] decode(int[][] residuals, int predictorType, int quantizationLevels) {
        int rows = residuals.length;
        int cols = residuals[0].length;
        int[][] reconstructedImage = new int[rows][cols];

        double stepSize = Utils.calculateStepSize(quantizationLevels);

        // 1. Reconstruct the first row & column directly from the residuals
        for (int j = 0; j < cols; j++) {
            reconstructedImage[0][j] = residuals[0][j];
        }
        for (int i = 1; i < rows; i++) {
            reconstructedImage[i][0] = residuals[i][0];
        }

        // 2. Process the rest of the image
        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                // Predict the pixel value using previously reconstructed pixels
                int predictedValue = Utils.predict(reconstructedImage, i, j, predictorType);

                // Get the quantized error index from the residuals
                int quantizedErrorIndex = residuals[i][j];

                // Dequantize the index to get the reconstructed error
                int reconstructedError = Utils.dequantizeError(quantizedErrorIndex, stepSize);

                // Reconstruct the pixel value by adding the error to the prediction
                int reconstructedPixelValue = predictedValue + reconstructedError;

                // Ensure the final pixel value is within the valid [0, 255] range
                reconstructedPixelValue = Math.max(0, Math.min(255, reconstructedPixelValue));

                reconstructedImage[i][j] = reconstructedPixelValue;
            }
        }

        return reconstructedImage;
    }
}
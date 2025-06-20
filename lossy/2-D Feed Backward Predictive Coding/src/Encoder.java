public class Encoder {
    public int[][] encode(int[][] pixelArray, int predictorType, int quantizationLevels) {
        int rows = pixelArray.length;
        int cols = pixelArray[0].length;
        int[][] result = new int[rows][cols];
        int[][] decoded = new int[rows][cols];

        // initialize first row and column 
        for (int j = 0; j < cols; j++) {
            result[0][j] = pixelArray[0][j];
            decoded[0][j] = pixelArray[0][j];
        }
        for (int i = 1; i < rows; i++) {
            result[i][0] = pixelArray[i][0];
            decoded[i][0] = pixelArray[i][0];
        }

        double stepSize = Utils.calculateStepSize(quantizationLevels);

        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                // predict using already decoded values
                int predictedValue = Utils.predict(decoded, i, j, predictorType);

                // calculate prediction error
                int error = pixelArray[i][j] - predictedValue;

                // quantize the error to get a quantized index
                int quantizedErrorIndex = Utils.quantizeError(error, stepSize);
                result[i][j] = quantizedErrorIndex;

                
                // dequantize the error index back to an error value
                int reconstructedError = Utils.dequantizeError(quantizedErrorIndex, stepSize);

                // reconstruct the pixel value
                int reconstructedPixel = predictedValue + reconstructedError;

                // clip the value to the valid [0, 255] range
                decoded[i][j] = Math.max(0, Math.min(255, reconstructedPixel));
            }
        }

        return result;
    }
}
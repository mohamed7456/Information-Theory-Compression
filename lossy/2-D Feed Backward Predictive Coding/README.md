# 2-D Feed Backward Predictive Coding

This project implements 2-D Feed Backward Predictive Coding for grayscale image compression in Java. The algorithm predicts pixel values based on neighboring pixels, encodes the prediction error (residual), and quantizes the error for compression.

## Features

- First-order, second-order, and adaptive predictors
- Adjustable quantization levels
- Calculates Mean Squared Error (MSE) and compression ratio
- Reads and writes standard image formats (e.g., PNG)
- Command-line interface for user input

## File Structure

- `src/`
  - `Main.java` — Main entry point
  - `Encoder.java` — Encodes the image using predictive coding
  - `Decoder.java` — Decodes the quantized residuals
  - `ImageProcessor.java` — Image I/O and conversion utilities
  - `Utils.java` — Predictor functions, quantization, and evaluation metrics

## How to Run

1. **Compile the source files:**
    ```sh
    javac -d bin src/*.java
    ```

2. **Run the program:**
    ```sh
    java -cp bin Main
    ```

3. **Follow the prompts:**
    - Enter the path to the input image (e.g., `test_images/lena.png`)
    - Select predictor type: 1 (first-order), 2 (second-order), 3 (adaptive)
    - Enter the number of quantization levels (e.g., 8, 16, 32)
    - The processed image will be saved in the `output/` directory

## Example Output

```
--------------2-D Feed Backward Predictive Coding-------------
Enter image path: test_images/baboon.png
1 for first-order, 2 for second-order, 3 for adaptive
Enter your choice: 3
Enter number of quantization levels (e.g., 8, 16, 32): 16
Processing image with choice: adaptive and quantization levels: 16
Mean Squared Error (MSE): 42.37
Compression Ratio: 2.00
Processed image saved to: output/baboon_adaptive_16.png
```

## Notes

- Input images should be grayscale or will be converted to grayscale automatically.
- The output directory will be created if it does not exist.
- Higher quantization levels generally yield better quality but lower compression.

## License

This project is for academic and educational purposes.

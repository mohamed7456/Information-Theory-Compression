# Vector Quantization Image Compression

This project implements image compression using Vector Quantization (VQ) with the Linde–Buzo–Gray (LBG) algorithm. It processes grayscale images by dividing them into blocks, generating a codebook, compressing, and reconstructing the image.

## Workspace Structure

- `README.md`
- `output/` (compressed images)
- `src/`
    - `ImageProcessor.java`
    - `Main.java`
    - `Utils.java`
    - `VectorQuantizer.java`
- `test_images/` (input images)

## How to Run

1. Compile the source files:

    ```sh
    javac -d bin src/*.java
    ```

2. Run the program:

    ```sh
    java -cp bin Main
    ```

3. Follow the prompts:
    - Enter the path to the input image (e.g., `test_images/baboon.png`)
    - Enter the block size (e.g., `4`)
    - Enter the number of vectors in the codebook (e.g., `16`)
    - Enter the path to save the output image (e.g., `output/baboon_4_16.png`)

## Example Output

```
Enter path to input image: test_images/baboon.png
Enter block size: 4
Enter number of vectors in codebook: 16
Enter path to save output image: output/baboon_4_16.png
Image compressed and saved to: output/baboon_4_16.png
Mean Squared Error (MSE): 454.63
Original Size: 262144.00 bytes
Compressed Size: 8448.00 bytes
Compression Ratio: 31.03
``` 

## Main Components

- `src/Main.java`: Entry point, handles user input and workflow.
- `src/ImageProcessor.java`: Image reading, grayscale conversion, block division, and reconstruction.
- `src/VectorQuantizer.java`: Codebook generation, compression, and decompression.
- `src/Utils.java`: Utility functions for MSE and compression ratio calculations.




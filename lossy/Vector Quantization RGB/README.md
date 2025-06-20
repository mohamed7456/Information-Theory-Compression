# Vector Quantization RGB

This project implements Vector Quantization for RGB images as part of lossy image compression techniques.

## Functionality

This implementation performs the following steps:

- **Training Phase:**
  - Loads all training images and resizes them to a standard size.
  - Divides each image into small blocks for each color channel (R, G, B).
  - Collects a large set of blocks from all training images and samples them if necessary.
  - Uses the Linde–Buzo–Gray (LBG) algorithm (a variant of k-means) to generate a codebook for each channel, which represents typical block patterns.
  - Saves the codebooks for later use.

- **Compression Phase:**
  - For each test image, divides it into blocks and assigns each block to the closest codebook entry for each channel.
  - Stores the indices (labels) of the codebook entries instead of the raw pixel values, achieving compression.

- **Decompression Phase:**
  - Reconstructs the image by replacing each block label with the corresponding codebook block for each channel.

- **Evaluation:**
  - Calculates the Mean Squared Error (MSE) and Peak Signal-to-Noise Ratio (PSNR) for each reconstructed image.
  - Computes the compression ratio.
  - Saves reconstructed images and logs the results in a CSV file.

## Image Sources

All images used in this project (both training and test images) are collected from the internet and are used for educational and research purposes only.

## Directory Structure
- `Images/training_images/`: Contains training images for the vector quantization algorithm.
- `Images/test_images/`: Contains test images for evaluating the algorithm.
- `src/`: Source code for the implementation.

## Disclaimer
If you are the owner of any image and would like it to be removed, please contact the repository maintainer.

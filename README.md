# Information Theory & Compression

This repository contains Java implementations of various lossless and lossy compression algorithms, developed as part of an Information Theory course. The code is organized by algorithm and includes both source code and example files for testing and demonstration.

## Table of Contents
- [Lossless Compression](#lossless-compression)
  - [Adaptive Huffman](#adaptive-huffman)
  - [Standard Huffman](#standard-huffman)
  - [LZ77 Compression](#lz77-compression)
  - [LZ78 Compression](#lz78-compression)
- [Lossy Compression](#lossy-compression)
  - [2-D Feed Backward Predictive Coding](#2-d-feed-backward-predictive-coding)
  - [Vector Quantization (Gray)](#vector-quantization-gray)
  - [Vector Quantization (RGB)](#vector-quantization-rgb)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [License](#license)

---

## Lossless Compression

### Adaptive Huffman
- Path: `lossless/Adaptive Huffman/`
- Implements Adaptive Huffman coding for dynamic data compression.
- Includes encoder, decoder, and visualization tools.

### Standard Huffman
- Path: `lossless/Standard Huffman/`
- Implements the classic Huffman coding algorithm for static data compression.

### LZ77 Compression
- Path: `lossless/LZ77 Compression/`
- Implements the LZ77 sliding window compression algorithm.

### LZ78 Compression
- Path: `lossless/LZ78 Compression/`
- Implements the LZ78 dictionary-based compression algorithm.
- Includes sample text files for testing.

## Lossy Compression

### 2-D Feed Backward Predictive Coding
- Path: `lossy/2-D Feed Backward Predictive Coding/`
- Implements predictive coding for image compression.

### Vector Quantization (Gray)
- Path: `lossy/Vector Quantization Gray/`
- Implements vector quantization for grayscale images.

### Vector Quantization (RGB)
- Path: `lossy/Vector Quantization RGB/`
- Implements vector quantization for RGB images.
- Includes training and test image sets.

## Getting Started

1. **Requirements:**
   - Java (JDK 8 or higher)
2. **Compile & Run:**
   - Navigate to the desired algorithm's directory.
   - Compile using `javac *.java` inside the `src` folder.
   - Run the main class using `java Main` or the relevant main class for each algorithm.

## Project Structure

```
Information-Theory-Compression/
├── lossless/
│   ├── Adaptive Huffman/
│   ├── Standard Huffman/
│   ├── LZ77 Compression/
│   └── LZ78 Compression/
└── lossy/
    ├── 2-D Feed Backward Predictive Coding/
    ├── Vector Quantization Gray/
    └── Vector Quantization RGB/
```

Each subfolder contains its own `README.md` with more details and usage instructions.

## License

This project is for educational purposes.

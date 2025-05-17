# Adaptive Huffman Coding 

This project implements Adaptive Huffman Coding in Java, featuring both encoding and decoding, as well as a visualization of the adaptive Huffman tree.

## Features

- Adaptive Huffman encoding and decoding
- Real-time visualization of the Huffman tree (using Java Swing)
- Command-line and GUI usage

## How to Run

1. **Compile all Java files:**
   ```sh
   javac src/*.java
   ```

2. **Run with visualization (GUI):**
   ```sh
   java -cp src Main
   ```

3. **Run command-line only (no visualization):**
   ```sh
   java -cp src AdaptiveHuffman
   ```

4. **Input:**
   - When prompted, enter the string you want to encode.

5. **Output:**
   - The program will display the encoded binary string and the decoded text.
   - If using the GUI, a window will show the Huffman tree as it adapts.

## File Structure

- `src/Main.java` — Main entry point with visualization and user input
- `src/AdaptiveHuffman.java` — Command-line version for encoding/decoding
- `src/Encoder.java` / `src/Decoder.java` — Core logic for adaptive Huffman coding
- `src/HuffmanTree.java` / `src/Node.java` — Data structures for the tree
- `src/HuffmanTreeVisualizer.java` — Swing-based tree visualization

## Requirements

- Java 8 or higher

## Notes

- Visualization uses Java Swing; ensure your environment supports GUI windows.
- For command-line only, use `AdaptiveHuffman.java`.

---

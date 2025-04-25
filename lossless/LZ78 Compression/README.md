# LZ78 Compression & Decompression in Java
This project implements the LZ78 compression algorithm and its corresponding decompression in Java. It reads a text file, compresses it into binary tags using LZ78, and allows restoring the original file via decompression.

## Features
Compresses and decompresses text files using the LZ78 algorithm.

Writes compressed data in binary format.

All files are saved in a files/ directory.

Includes a custom binary encoding and decoding scheme.

Interactive menu-driven terminal application.

File Structure

├── LZ78compression.java <br>
├── files/ <br>
│   ├── your_input_file.txt <br>
│   ├── your_input_file_compressed <br>
│   └── your_input_file_compressed_decompressed.txt <br>

## 🛠 How It Works

### Compression
1. The input file is read character by character.
2. A dictionary is built to store substrings.
3. Each substring is replaced with a tag: `<position, nextChar>`.
4. Tags are converted into a binary format and written to a file.
5. The first 8 bits indicate the number of bits used for positions.

### Decompression
1. Reads binary tags from the file.
2. Uses the stored position and character to rebuild the dictionary.
3. Reconstructs the original text step-by-step.

▶How to Run

1. Compile the Java file:
   ```bash
   javac LZ78compression.java
   ```
2. Run the program:
   ```bash
   java LZ78compression
   ```
3. Choose from the menu:
   ```rust
   1 for Compression
   2 for Decompression
   3 for Terminating the Program
   ```
Provide file names (without extension) when prompted. Make sure the .txt file is inside the files/ directory.

Example
Given a file sample.txt with content:
```bash
abracadabra
```
After compression and decompression, the output file sample_compressed_decompressed.txt will contain:
```bash
abracadabra
```

### Concepts Used
- HashMap for dictionary implementation

- Bit manipulation for binary conversion

- File I/O for handling text and binary files

- Custom Tag class representing LZ78 tags

### Notes
- All input .txt files should be placed in the files/ folder.

- Compressed files are saved without extension, while decompressed files are saved with .txt.

### License
- This project is for academic and educational use.

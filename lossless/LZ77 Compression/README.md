# LZ77 Compression in Java

This project implements a simple LZ77 compression and decompression tool in Java. Two versions are provided:
- **Interactive version:** `LZ77compression.java` (menu-driven, prompts user for file names)
- **File I/O version:** `LZ77compressionFileIO.java` (menu-driven, takes full file paths, saves output in an `output_lz77` directory in the current working directory)

## How to Use

### Compile the Java code:
```bash
javac LZ77compression.java LZ77compressionFileIO.java
```

### Run the interactive version:
```bash
java LZ77compression
```
- Option 1: Compress a text file (prompts for file path)
- Option 2: Decompress a compressed file (prompts for file path)
- Option 3: Exit the program

### Run the file I/O version:
```bash
java LZ77compressionFileIO
```
- You will be prompted to enter the full path to the input file for compression or decompression.
- Output files will be saved in a directory named **output_lz77** in the current working directory.
- Compressed files are named `<original_filename>.lz77`.
- Decompressed files are named `<original_filename>_decompressed.txt`.

## Output Files
- All output files are saved in a directory named **output_lz77** in the current working directory.
- Compressed files: `output_lz77/<original_filename>.lz77`
- Decompressed files: `output_lz77/<original_filename>_decompressed.txt`

## Note
- The search buffer size for LZ77 is set to `final int SEARCH_BUFFER_SIZE = 5;` in the code for demonstration and small test files. **If you use this on real or long data, you should increase this value for better compression efficiency.**


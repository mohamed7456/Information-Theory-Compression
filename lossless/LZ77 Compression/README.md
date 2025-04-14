# 📦 LZ77 Compression in Java

This project implements a simple LZ77 compression and decompression tool in Java. It only generates LZ77 tag structure without creating real compressed files.
## 💻 How to Use

### Clone the repository:
```bash
git clone https://github.com/yourusername/LZ77compression.git
cd LZ77compression
```

### Compile the Java code:
```bash
javac LZ77compression.java
```

### Run the program:
```bash
java LZ77compression
```

### Follow the prompts:
- Option 1: Compress a text file.
- Option 2: Decompress a compressed file.
- Option 3: Exit the program.

✅ When prompted, provide the full path or relative path of the .txt file to be compressed or decompressed.
For example: `data/input.txt` or just `input.txt` if it’s in the same folder.

## 🗂 Output Files
- Compressed files will be saved as `<original_filename>_compressed.txt`
- Decompressed files will be saved as `<original_filename>_decompressed.txt`

## 📌 Notes
- All input/output files will be saved in the same directory as the Java file unless full paths are provided.
- No external dependencies required.
- Written in pure Java, compatible with JDK 8+.

## Future Work
- Make real compressed binary files.

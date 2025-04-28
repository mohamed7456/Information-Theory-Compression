# 📦 Standard Huffman Compression & Decompression in Java

This project implements the **Standard Huffman Compression Algorithm** in Java. It allows encoding text files into compressed binary files and decoding them back to their original form using Huffman coding. The program is flexible and allows users to specify file paths dynamically.

---

## 🚀 Features

- **Huffman Encoding**: Compresses text files into binary files using Huffman coding.
- **Huffman Decoding**: Restores the original text from the compressed binary file.
- **Entropy Calculation**: Calculates the entropy of the input text.
- **Code Table Generation**: Saves the Huffman code table for decoding.
- **Interactive Menu**: Provides a user-friendly terminal interface.
- **Dynamic File Paths**: Users can specify file paths for input and output files.

---

## 🛠 How It Works

### Encoding
1. Reads the input text file from the specified path.
2. Calculates the frequency of each character.
3. Builds a Huffman tree based on character frequencies.
4. Generates Huffman codes for each character.
5. Encodes the text into a binary stream.
6. Saves the encoded binary file and the code table.

### Decoding
1. Reads the encoded binary file and the code table from the specified paths.
2. Reconstructs the Huffman tree from the code table.
3. Decodes the binary stream back into the original text.
4. Saves the decoded text file.

---

## ▶️ How to Run

### Prerequisites
- Java Development Kit (JDK) 8 or higher installed.

### Steps
1. **Compile the Java file**:
   ```bash
   javac standardHuffmanCompression.java

2. Run the program:
```bash
java standardHuffmanCompression
```

3. Follow the menu options:
- 1 for Encoding
- 2 for Decoding
- 3 to Exit the Program

## Input and Output Instructions
Input
- Provide the full file path of the input .txt file when prompted.
Output
- Encoded File: Saved as <file_name>_encoded.bin in the same directory as the input file.
- Code Table: Saved as <file_name>_code_table.txt in the same directory as the input file.
- Decoded File: Saved as <file_name>_decoded.txt in the same directory as the input file.

## Example
Input File (C:\files\sample.txt):
```hello huffman```

Encoding
- Entropy: 3.238901256602631
- Compressed Stream: 1000100000011111101001011110110011011111010 (binary representation)
- Output Files:
- - C:\files\sample_encoded.bin
- - C:\files\sample_code_table.txt

Decoding
- Decoded File: C:\files\sample_decoded.txt
- Content: ```hello huffman```


## Notes
- Ensure the input file exists at the specified path.
- Encoded files are saved with a .bin extension.
- Decoded files are saved with a .txt extension.
- Code tables are saved as .txt files for easy reference.

## License
This project is for academic and educational purposes.
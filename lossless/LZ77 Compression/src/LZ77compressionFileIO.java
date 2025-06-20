import java.io.*;
import java.util.*;

public class LZ77compressionFileIO {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        OUTER:
        while (true) {
            System.out.println("\n1 for Compression, 2 for Decompression, 3 for Terminating the Program");
            int choice;
            try {
                choice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next();
                continue;
            }
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.println("Enter the full path to the file you want to compress:");
                    String fileName = sc.nextLine();
                    compress(fileName);
                }
                case 2 -> {
                    System.out.println("Enter the full path to the file you want to decompress (the .lz77 file):");
                    String fileName = sc.nextLine();
                    decompress(fileName);
                }
                case 3 -> {
                    System.out.println("Program Closed");
                    break OUTER;
                }
                default -> System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
        sc.close();
    }

    public static class Tag {
        int position;
        int length;
        char nextChar;

        public Tag(int position, int length, char nextChar) {
            this.position = position;
            this.length = length;
            this.nextChar = nextChar;
        }

        public int getPosition() { return position; }
        public int getLength() { return length; }
        public char getNextChar() { return nextChar; }
    }

    public static void compress(String inputPath) {
        String text = readFromFile(inputPath);
        if (text == null || text.isEmpty()) {
            System.out.println("Input file is empty or could not be read.");
            return;
        }

        List<Tag> tags = new ArrayList<>();
        final int SEARCH_BUFFER_SIZE = 5;
        int currentIndex = 0;

        while (currentIndex < text.length()) {
            int bestLength = 0;
            int bestPosition = 0;
            int searchStart = Math.max(0, currentIndex - SEARCH_BUFFER_SIZE);

            for (int j = searchStart; j < currentIndex; j++) {
                int currentLength = 0;
                while (currentIndex + currentLength < text.length() &&
                       text.charAt(j + currentLength) == text.charAt(currentIndex + currentLength)) {
                    currentLength++;
                }

                if (currentLength > bestLength) {
                    bestLength = currentLength;
                    bestPosition = currentIndex - j;
                }
            }

            char nextChar;
            int nextCharIndex = currentIndex + bestLength;
            if (nextCharIndex < text.length()) {
                nextChar = text.charAt(nextCharIndex);
            } else {
                nextChar = '\0'; // Sentinel
            }

            tags.add(new Tag(bestPosition, bestLength, nextChar));
            currentIndex += bestLength + 1;
        }

        int maxPos = 0, maxLen = 0;
        for (Tag tag : tags) {
            if (tag.getPosition() > maxPos) maxPos = tag.getPosition();
            if (tag.getLength() > maxLen) maxLen = tag.getLength();
        }

        int posBits = bitsNeeded(maxPos);
        int lenBits = bitsNeeded(maxLen);

        StringBuilder binaryTags = new StringBuilder();
        binaryTags.append(toBinary(posBits, 8));
        binaryTags.append(toBinary(lenBits, 8));

        for (Tag tag : tags) {
            binaryTags.append(binarizeTag(tag.getPosition(), posBits, tag.getLength(), lenBits, tag.getNextChar()));
        }

        File inputFile = new File(inputPath);
        String baseName = inputFile.getName();
        String outputDir = "output_lz77";
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        String outputPath = outputDir + File.separator + removeExtension(baseName) + ".lz77";
        writeBinaryToFile(binaryTags.toString(), outputPath);
        System.out.println("Compression successful!");
        System.out.println("Compressed file saved in: " + outputPath);
    }

    public static void decompress(String inputPath) {
        String binaryString = readBinaryFromFile(inputPath);
        if (binaryString == null || binaryString.length() < 16) {
            System.out.println("Invalid or empty compressed file.");
            return;
        }

        int posBits = binaryToDecimal(binaryString.substring(0, 8));
        int lenBits = binaryToDecimal(binaryString.substring(8, 16));
        List<Tag> tags = new ArrayList<>();
        int i = 16;
        int tagSize = posBits + lenBits + 8;

        while (i + tagSize <= binaryString.length()) {
            int position = binaryToDecimal(binaryString.substring(i, i + posBits));
            i += posBits;
            int length = binaryToDecimal(binaryString.substring(i, i + lenBits));
            i += lenBits;
            char nextChar = (char) binaryToDecimal(binaryString.substring(i, i + 8));
            i += 8;
            tags.add(new Tag(position, length, nextChar));
        }

        StringBuilder result = new StringBuilder();
        for (Tag tag : tags) {
            int position = tag.getPosition();
            int length = tag.getLength();
            if (position > 0) {
                int startIndex = result.length() - position;
                for (int k = 0; k < length; k++) {
                    result.append(result.charAt(startIndex + k));
                }
            }
            if (tag.getNextChar() != '\0') {
                result.append(tag.getNextChar());
            }
        }

        File inputFile = new File(inputPath);
        String baseName = inputFile.getName();
        String outputDir = "output_lz77";
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        String outputPath = outputDir + File.separator + removeLz77Extension(baseName) + "_decompressed.txt";
        createFile(outputPath, result.toString());
        System.out.println("Decompression successful!");
        System.out.println("Decompressed file saved in: " + outputPath);
    }

    public static String readFromFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Error: File not found at " + file.getAbsolutePath());
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
            return null;
        }
        return sb.toString();
    }

    private static void createFile(String filePath, String content) {
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(content);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while writing to file: " + e.getMessage());
        }
    }

    private static String removeExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot == -1) ? filename : filename.substring(0, dot);
    }

    private static String removeLz77Extension(String filename) {
        if (filename.toLowerCase().endsWith(".lz77")) {
            return filename.substring(0, filename.length() - 5);
        }
        return removeExtension(filename);
    }

    public static int bitsNeeded(int number) {
        if (number <= 0) return 1;
        return (int) (Math.floor(Math.log(number) / Math.log(2))) + 1;
    }

    public static String toBinary(int number, int bits) {
        String binaryString = Integer.toBinaryString(number);
        if (binaryString.length() > bits) {
            return binaryString.substring(binaryString.length() - bits);
        }
        return String.format("%" + bits + "s", binaryString).replace(' ', '0');
    }

    public static String binarizeTag(int pos, int posBits, int len, int lenBits, char nextChar) {
        String binaryPos = toBinary(pos, posBits);
        String binaryLen = toBinary(len, lenBits);
        String binaryChar = toBinary(nextChar, 8);
        return binaryPos + binaryLen + binaryChar;
    }

    public static void writeBinaryToFile(String binaryString, String filename) {
        try {
            File file = new File(filename);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                for (int i = 0; i < binaryString.length(); i += 8) {
                    String byteString;
                    if (i + 8 <= binaryString.length()) {
                        byteString = binaryString.substring(i, i + 8);
                    } else {
                        byteString = binaryString.substring(i);
                        while (byteString.length() < 8) {
                            byteString += "0";
                        }
                    }
                    int byteValue = Integer.parseInt(byteString, 2);
                    fos.write(byteValue);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("An error occurred while writing binary file: " + e.getMessage());
        }
    }

    public static String readBinaryFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.err.println("Error: Compressed file not found at " + file.getAbsolutePath());
            return null;
        }
        StringBuilder binaryString = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] allBytes = fis.readAllBytes();
            for (byte b : allBytes) {
                binaryString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading binary file: " + e.getMessage());
            return null;
        }
        return binaryString.toString();
    }

    public static int binaryToDecimal(String n) {
        try {
            return Integer.parseInt(n, 2);
        } catch (NumberFormatException e) {
            System.err.println("Error: Corrupt data. Cannot convert binary string to decimal: " + n);
            return 0;
        }
    }
}

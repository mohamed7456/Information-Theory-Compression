import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LZ78compression {

    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        OUTER:
        while (true) {
            System.out.println("1 for Compression, 2 for Decompression, 3 for Terminating the Program");
            int choice = sc.nextInt();
            sc.nextLine(); 
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter the file name to compress:");
                    String fileName = sc.nextLine();
                    compress(fileName);
                }
                case 2 -> {
                    System.out.println("Enter the file name to decompress:");
                    String fileName = sc.nextLine();
                    decompress(fileName);
                }
                case 3 ->{
                    System.out.println("Program Closed");
                    break OUTER;
                }
                default -> System.out.println("Invalid choice");
            }
        }
        sc.close(); 
    }


    // class Tag for LZ 78 tag structure
    public static class Tag {
        int position;
        char nextChar;

        public Tag(int position, char nextChar) {
            this.position = position;
            this.nextChar = nextChar;
        }
        public int getPosition() {
            return position;
        }
        public char getNextChar() {
            return nextChar;
        }
        public void printTag(){
            System.out.println("<" + position + ',' + nextChar + '>');
        }
    }


    // compression function
    public static void compress(String fileName) {
        String text = readFromFile(fileName);
        List<Tag> tags = new ArrayList<>();
        int pos = 1;
        Map<String, Integer> dict = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            StringBuilder temp = new StringBuilder();
            temp.append(text.charAt(i));
            int lastFoundPosition = 0;
            int k = 0;
            while (i + k < text.length() && dict.containsKey(temp.toString())) {
                lastFoundPosition = dict.get(temp.toString());
                k++;
                if (i + k < text.length()) {
                    temp.append(text.charAt(i + k));
                } else {
                    break;
                }
            }
            char nextChar;
            if (i + k < text.length()) {
                nextChar = text.charAt(i + k);
            } else {
                nextChar = '\0';
            }
            tags.add(new Tag(lastFoundPosition, nextChar));
            dict.put(temp.toString(), pos);
            pos++;
            i += k;
        }

        int maxPosition = 0;
        for (Tag tag : tags) {
            if (tag.getPosition() > maxPosition) {
                maxPosition = tag.getPosition();
            }
        }
        int noOfBits = bitsNeeded(maxPosition);
        StringBuilder compressedBinaryTags = new StringBuilder();
        compressedBinaryTags.append(toBinary(noOfBits, 8)); // saving bit length at the beginning to be able to decompress 
        for (Tag tag : tags) {
            compressedBinaryTags.append(BinarizeTag(tag.getPosition(), noOfBits, tag.getNextChar()));
            tag.printTag();
        }

        // write compresssed binary tags to a file
        String binaryString = compressedBinaryTags.toString();
        writeBinaryToFile(binaryString, "files/" + fileName + "_compressed");
    
        System.out.println("Compressed File Saved as: files/" + fileName + "_compressed");
    }


    // read text from file
    public static String readFromFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        try {
            File inputDir = new File("files");
            File file = new File(inputDir, fileName + ".txt");

            if (!file.exists()) {
                System.out.println("File not found: " + file.getAbsolutePath());
                return "";
            }
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNextLine()) {
                    sb.append(sc.nextLine()).append("\n");
                }
            }
        } 
        catch (FileNotFoundException e) {
            System.out.println("Cannot read file: " + fileName);
        }
        return sb.toString().trim();
    }


    // decompression function
    public static void decompress(String filename) {
        String binaryString = readBinaryFromFile("files/" + filename);
        int noOfBits = binaryToDecimal(binaryString.substring(0, 8)); // first 8 bits stores the maximum length of bits of position in the tag
        List<Tag> tags = new ArrayList<>();
        int i = 8;
        while (i + noOfBits + 8 <= binaryString.length()) {
            // noOfBits + 8 = tag size
            // first noOfBits are popsition length of tag
            int position = binaryToDecimal(binaryString.substring(i, i + noOfBits));
            i += noOfBits;
            // next 8 bits are the next char
            char nextChar = (char) binaryToDecimal(binaryString.substring(i, i + 8));
            i += 8;
            tags.add(new Tag(position, nextChar));
        }
        Map<Integer, String> dict = new HashMap<>();
        StringBuilder decompressedText = new StringBuilder();
        int j = 1;
        for (Tag tag : tags){
            StringBuilder temp = new StringBuilder();
            int pos = tag.getPosition();
            char nextC = tag.getNextChar();
            if (pos == 0) {
                temp.append(nextC);
            } 
            else {
                if (dict.containsKey(pos)) {
                    temp.append(dict.get(pos));
                }
                temp.append(nextC);
            }
            decompressedText.append(temp.toString());
            dict.put(j, temp.toString());
            j++;
        }
        // save decompressed text into file
        String text = decompressedText.toString();
        text = text.trim();
        createFile(filename + "_decompressed", text);

        System.out.println("Decompressed files saved as: files/" + filename + "_decompressed");
    }

    // create file and write string in
    private static void createFile(String fileName, String content) {
        try {
            File outputDir = new File("files");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File file = new File(outputDir, fileName + ".txt");
            file.createNewFile();
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write(content);
                System.out.println("File saved as " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Cannot write to file: " + fileName);
        }
    }


    public static int binaryToDecimal(String n) {
        int dec_value = 0;
        int base = 1;
        int len = n.length();
        for (int i = len - 1; i >= 0; i--) {
            if (n.charAt(i) == '1') {
                dec_value += base;
            }
            base *= 2;
        }
        return dec_value;
    }
    
    // convert a binary to ASCII
    public static String binaryToASCII(String str) {
        int N = str.length();
        if (N % 8 != 0) {
            return "Not Possible!";
        }
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < N; i += 8) {
            String byteStr = str.substring(i, i + 8);
            int decimalValue = binaryToDecimal(byteStr);
            res.append((char) decimalValue);
        } 
        return res.toString();
    }

    // method to return the number of bits needed to code an integer number
    public static int bitsNeeded(int number) {
        if (number == 0) {
            return 1; // Special case for 0
        }
        return (int)(Math.floor(Math.log(number) / Math.log(2))) + 1;
    }

    // take as input an integer number and #bits
    // return the binary representation of the number of length #bits
    public static String toBinary(int number, int bits) {
        String binaryString = Integer.toBinaryString(number);
        int length = binaryString.length();
        if (length < bits) {
            // Pad with leading zeros
            binaryString = String.format("%" + bits + "s", binaryString).replace(' ', '0');
        }
        return binaryString;
    }

    public static String BinarizeTag(int pos, int bits, char nextChar) {
        // The position will be encoded in certain number of bits
        String binaryPos = toBinary(pos, bits);
        // The nextChar will be encoded in exactly 8 bits
        int asciiCode = (int) nextChar;
        String binaryChar = toBinary(asciiCode, 8);
        // Return the string concatenation of binary representation of pos and nextChar
        return binaryPos + binaryChar;
    }

    // code to write the tags as bytes to the binary file
    public static void writeBinaryToFile(String binaryString, String filename) {
        byte[] byteArray = new byte[binaryString.length() / 8];
        for (int i = 0; i < byteArray.length; i++) {
            String byteString = binaryString.substring(8 * i, 8 * (i + 1));
            byteArray[i] = (byte) Integer.parseInt(byteString, 2);
        }
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // read the tags as bytes and form the binary string that contain the tags
    public static String readBinaryFromFile(String filename) {
        StringBuilder binaryString = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] byteArray = fis.readAllBytes();
            for (byte b : byteArray) {
                String byteString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
                binaryString.append(byteString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return binaryString.toString();
    }
}

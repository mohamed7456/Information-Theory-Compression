import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class standardHuffmanCompression {
    private static final String FILE_DIR = "files/";
    private static final String TXT_EXT = ".txt";
    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        OUTER:
        while (true) {
            System.out.println("Standard Huffman");
            System.out.println("1 for Encoding, 2 for Decoding, 3 for Exiting the Program");
            int choice = sc.nextInt();
            sc.nextLine(); 
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter the file name to Encode:");
                    String fileName = sc.nextLine();
                    huffmanEncoding(fileName);
                }
                case 2 -> {
                    System.out.println("Enter the file name to Decode:");
                    String fileName = sc.nextLine();
                    huffmanDecoding(fileName);
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


    // huffmun Encoding
    public static void huffmanEncoding(String fileName) {   
        String text = readFromFile(fileName);
        Map<Character, Integer> frequencyMap = new HashMap<>();
        int totalCharacters = 0;
        for (char c : text.toCharArray()) {
            if (frequencyMap.containsKey(c)) {
                frequencyMap.put(c, frequencyMap.get(c) + 1);
            } else {
                frequencyMap.put(c, 1);
            }
            totalCharacters++;
        }
        // calculate entropy
        double entropy  = calculateEntropy(frequencyMap, totalCharacters);
        System.out.println("Entropy: " + entropy);
        // build huffman tree
        HuffmanNode root = buildHuffmanTree(frequencyMap);
        // generate huffman codes
        Map<Character, String> huffmanCodes = new HashMap<>();
        generateHuffmanCodes(root, "", huffmanCodes);
        // encode the text
        StringBuilder encodedBits = new StringBuilder();
        for (char c : text.toCharArray()) {
            encodedBits.append(huffmanCodes.get(c));
        }
        System.out.println("Compressed Stream: " + encodedBits.toString());
        // save code tables and encoded file
        String baseName = FILE_DIR + fileName;
        saveEncodedData(encodedBits.toString(), baseName + "_encoded.bin");
        saveCodeTable(huffmanCodes, baseName + "_code_table.txt");
        System.out.println("Encoded file saved as: " + baseName + "_encoded.bin");
        System.out.println("Code Table saved as: " + baseName + "_code_table.txt");
    }


    // huffman decoding
    public static void huffmanDecoding(String fileName) {
        String baseFileName = fileName;
        if (fileName.endsWith("_encoded")) {
            baseFileName = fileName.substring(0, fileName.lastIndexOf("_encoded"));
        }
        String baseName = FILE_DIR + baseFileName;
        Map<Character, String> codeTable = readCodeTable(baseName + "_code_table.txt");
        String encodedBits = readEncodedData(baseName + "_encoded.bin");

        HuffmanNode root = buildTreeFromCodeTable(codeTable);
        String decodedText = decodeHuffman(root, encodedBits);

        createFile(fileName + "_decoded", decodedText);
        System.out.println("Decoded file saved as: " + fileName + "_decoded.txt");
    }


    // Huffman Node Class
    public static class HuffmanNode implements Comparable<HuffmanNode> {
        char character;
        int frequency;
        HuffmanNode left, right;
        // constructor for leaf nodes
        public HuffmanNode(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }
        // constructor for internal nodes
        public HuffmanNode(int frequency) {
            this('\0', frequency);
        }
        @Override
        public int compareTo(HuffmanNode node) {
            return Integer.compare(this.frequency, node.frequency);
        }
    }



    /*------------------------------------------------------ ENCODING FUNCTIONS ------------------------------------------------------ */
    // Entropy Calculation Method
    public static double calculateEntropy(Map<Character, Integer> frequencyMap, int totalCharacters) {
        double entropy = 0.0;
        for (var entry : frequencyMap.entrySet()) {
            double probability = (double) entry.getValue() / totalCharacters;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }
        return entropy;
    }


    // Huffman Tree Builder
    public static HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        // create leaf nodes for each character
        for (var entry : frequencyMap.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
            pq.add(node);
        }
        // create the tree
        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode(left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }
        return pq.poll();
    }

    // Huffman Code Generator
    public static void generateHuffmanCodes(HuffmanNode root, String code, Map<Character, String> huffmanCodes) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            huffmanCodes.put(root.character, code);
        }
        generateHuffmanCodes(root.left, code + "0", huffmanCodes);
        generateHuffmanCodes(root.right, code + "1", huffmanCodes);
    }



    /*------------------------------------------------------ DECODING FUNCTIONS ------------------------------------------------------ */
    // Huffman Decoder
    public static String decodeHuffman(HuffmanNode root, String encodedText) {
        StringBuilder decodedText = new StringBuilder();
        HuffmanNode current = root;
        for (char bit : encodedText.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }
            if (current.left == null && current.right == null) { // leaf node reached
                decodedText.append(current.character);
                current = root;
            }
        }
        return decodedText.toString();
    }

    private static Map<Character, String> readCodeTable(String fileName) {
        Map<Character, String> codeTable = new HashMap<>();
        File file = new File(fileName);
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(":");
                codeTable.put(parts[0].charAt(0), parts[1]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Code table not found: " + fileName);
        }
        return codeTable;
    }


    private static String readEncodedData(String fileName) {
        File file = new File(fileName);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            BitSet bitSet = (BitSet) ois.readObject();
            int length = ois.readInt();
            StringBuilder bits = new StringBuilder();
            for (int i = 0; i < length; i++) {
                bits.append(bitSet.get(i) ? '1' : '0');
            }
            return bits.toString();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading encoded data: " + e.getMessage());
        }
        return "";
    }

    
    private static HuffmanNode buildTreeFromCodeTable(Map<Character, String> codeTable) {
        HuffmanNode root = new HuffmanNode(0);
        for (var entry : codeTable.entrySet()) {
            HuffmanNode current = root;
            for (char bit : entry.getValue().toCharArray()) {
                if (bit == '0') {
                    if (current.left == null) current.left = new HuffmanNode(0);
                    current = current.left;
                } else {
                    if (current.right == null) current.right = new HuffmanNode(0);
                    current = current.right;
                }
            }
            current.character = entry.getKey();
        }
        return root;
    }



    /*------------------------------------------------------ FILES FUNCTIONS ------------------------------------------------------ */
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


    // create file and write string in
    private static void createFile(String fileName, String content) {
        File file = new File(FILE_DIR + fileName + TXT_EXT);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            System.out.println("File saved as: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    private static void saveEncodedData(String bits, String outputFileName) {
        try (FileOutputStream fos = new FileOutputStream(outputFileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
             
            BitSet bitSet = new BitSet(bits.length());
            for (int i = 0; i < bits.length(); i++) {
                if (bits.charAt(i) == '1') {
                    bitSet.set(i);
                }
            }
            oos.writeObject(bitSet);
            oos.writeInt(bits.length());
            
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveCodeTable(Map<Character, String> codeTable, String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            codeTable.forEach((c, code) -> pw.println(c + ":" + code));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



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
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            OUTER:
            while (true) {
                System.out.println("Standard Huffman");
                System.out.println("1 for Encoding, 2 for Decoding, 3 for Exiting the Program");
                int choice = sc.nextInt();
                sc.nextLine();
                switch (choice) {
                    case 1 -> {
                        System.out.println("Enter the full file path to Encode:");
                        String filePath = sc.nextLine();
                        huffmanEncoding(filePath);
                    }
                    case 2 -> {
                        System.out.println("Enter the full file path to Decode:");
                        String filePath = sc.nextLine();
                        huffmanDecoding(filePath);
                    }
                    case 3 -> {
                        System.out.println("Program Closed");
                        break OUTER;
                    }
                    default -> System.out.println("Invalid choice");
                }
            }
        }
    }

    public static void huffmanEncoding(String filePath) {
        String text = readFromFile(filePath);
        if (text.isEmpty()) return;

        Map<Character, Integer> frequencyMap = new HashMap<>();
        int totalCharacters = 0;
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
            totalCharacters++;
        }

        double entropy = calculateEntropy(frequencyMap, totalCharacters);
        System.out.println("Entropy: " + entropy);

        HuffmanNode root = buildHuffmanTree(frequencyMap);
        Map<Character, String> huffmanCodes = new HashMap<>();
        generateHuffmanCodes(root, "", huffmanCodes);

        StringBuilder encodedBits = new StringBuilder();
        for (char c : text.toCharArray()) {
            encodedBits.append(huffmanCodes.get(c));
        }
        System.out.println("Compressed Stream: " + encodedBits);

        String baseName = filePath.substring(0, filePath.lastIndexOf('.'));
        saveEncodedData(encodedBits.toString(), baseName + "_encoded.bin");
        saveCodeTable(huffmanCodes, baseName + "_code_table.txt");
        System.out.println("Encoded file saved as: " + baseName + "_encoded.bin");
        System.out.println("Code Table saved as: " + baseName + "_code_table.txt");
    }

    public static void huffmanDecoding(String filePath) {
        String baseName = filePath.substring(0, filePath.lastIndexOf('_'));
        Map<Character, String> codeTable = readCodeTable(baseName + "_code_table.txt");
        String encodedBits = readEncodedData(filePath);

        if (codeTable.isEmpty() || encodedBits.isEmpty()) return;

        HuffmanNode root = buildTreeFromCodeTable(codeTable);
        String decodedText = decodeHuffman(root, encodedBits);

        createFile(baseName + "_decoded.txt", decodedText);
        System.out.println("Decoded file saved as: " + baseName + "_decoded.txt");
    }

    public static class HuffmanNode implements Comparable<HuffmanNode> {
        char character;
        int frequency;
        HuffmanNode left, right;

        public HuffmanNode(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        public HuffmanNode(int frequency) {
            this('\0', frequency);
        }

        @Override
        public int compareTo(HuffmanNode node) {
            return Integer.compare(this.frequency, node.frequency);
        }
    }

    public static double calculateEntropy(Map<Character, Integer> frequencyMap, int totalCharacters) {
        double entropy = 0.0;
        for (var entry : frequencyMap.entrySet()) {
            double probability = (double) entry.getValue() / totalCharacters;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }
        return entropy;
    }

    public static HuffmanNode buildHuffmanTree(Map<Character, Integer> frequencyMap) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (var entry : frequencyMap.entrySet()) {
            HuffmanNode node = new HuffmanNode(entry.getKey(), entry.getValue());
            pq.add(node);
        }
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

    public static void generateHuffmanCodes(HuffmanNode root, String code, Map<Character, String> huffmanCodes) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            huffmanCodes.put(root.character, code);
        }
        generateHuffmanCodes(root.left, code + "0", huffmanCodes);
        generateHuffmanCodes(root.right, code + "1", huffmanCodes);
    }

    public static String decodeHuffman(HuffmanNode root, String encodedText) {
        StringBuilder decodedText = new StringBuilder();
        HuffmanNode current = root;
        for (char bit : encodedText.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }
            if (current.left == null && current.right == null) {
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

    public static String readFromFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File not found: " + file.getAbsolutePath());
            return "";
        }

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot read file: " + filePath);
        }
        return sb.toString().trim();
    }

    private static void createFile(String filePath, String content) {
        File file = new File(filePath);
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

        } catch (IOException e) {
            System.err.println("An error occurred while saving the encoded data: " + e.getMessage());
        }
    }

    private static void saveCodeTable(Map<Character, String> codeTable, String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            codeTable.forEach((c, code) -> pw.println(c + ":" + code));
        } catch (IOException e) {
            System.err.println("An error occurred while saving the code table: " + e.getMessage());
        }
    }
}



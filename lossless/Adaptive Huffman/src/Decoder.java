public class Decoder {
    private final HuffmanTree huffmanTree;
    private final StringBuilder decodedString;
    private int currentBitIndex;
    private String encodedString;

    public Decoder() {
        huffmanTree = new HuffmanTree();
        decodedString = new StringBuilder();
        currentBitIndex = 0;
    }

    public String decode(String encodedStr) {
        this.encodedString = encodedStr;
        currentBitIndex = 0;

        while (currentBitIndex < encodedString.length()) {
            Node currentNode = huffmanTree.getRoot();
            // traverse the tree until a leaf node is found
            while (currentNode != null && !currentNode.isLeaf()) {
                if (currentBitIndex >= encodedString.length()) {
                    throw new IllegalArgumentException("Invalid encoded string: Unexpected end of input.");
                }
                char bit = encodedString.charAt(currentBitIndex);
                currentBitIndex++;
                if (bit == '0') {
                    currentNode = currentNode.getLeft();
                } else {
                    currentNode = currentNode.getRight();
                }
            }

            if (currentNode == null) {
                throw new IllegalArgumentException("Invalid encoded string: Path leads to null node.");
            }

            // check if the leaf is NYT
            if (currentNode == huffmanTree.getNYT()) {
                // read the next 8 bits for ASCII
                if (currentBitIndex + 8 > encodedString.length()) {
                    throw new IllegalArgumentException("Invalid encoded string: Incomplete ASCII bits.");
                }
                String asciiBits = encodedString.substring(currentBitIndex, currentBitIndex + 8);
                currentBitIndex += 8;
                char ch = (char) Integer.parseInt(asciiBits, 2);
                decodedString.append(ch);
                huffmanTree.insertSymbol(ch);
            } else {
                // existing character
                char ch = currentNode.getSymbol();
                decodedString.append(ch);
                huffmanTree.insertSymbol(ch);
            }
        }

        return decodedString.toString();
    }
}
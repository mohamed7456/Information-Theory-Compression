import java.util.HashSet;

public class AdaptiveHuffmanMain {
    public static void printTree(Node node, String indent, boolean isLeft, HashSet<Node> visited) {
        if (node == null) return;
        if (visited.contains(node)) {
            System.out.println(indent + (isLeft ? "L-- " : "R-- ") + "[Cycle detected: " + node.toString() + "]");
            return;
        }
        visited.add(node);
        System.out.println(indent + (isLeft ? "L-- " : "R-- ") + node.toString());
        printTree(node.getLeft(), indent + "    ", true, visited);
        printTree(node.getRight(), indent + "    ", false, visited);
    }

    public static void main(String[] args) {
        HuffmanTree tree = new HuffmanTree();
        
        // test string for adaptive Huffman coding.
        String input = "abcccaaaa";
        System.out.println("Input string: " + input);
        
        // insert each character into the tree and print its structure after each insertion.
        for (char ch : input.toCharArray()) {
            tree.insertSymbol(ch);
            System.out.println("Tree after inserting '" + ch + "':");
            printTree(tree.getRoot(), "", true, new HashSet<>());
            System.out.println("----------------------------");
        }
        
        // final visualization
        System.out.println("Final Huffman Tree:");
        printTree(tree.getRoot(), "", true, new HashSet<>());
    }
}

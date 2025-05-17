public class HuffmanTreeTest {
    public static void main(String[] args) {
        HuffmanTree tree = new HuffmanTree();
        String input = "abcccaaaa";
        
        System.out.println("Initial Tree:");
        tree.printTree();
        
        for (char c : input.toCharArray()) {
            System.out.println("\nInserting: " + c);
            tree.insertSymbol(c);
            tree.printTree();
            System.out.println("Current codes:");
            System.out.println("a: " + tree.getCode('a'));
            System.out.println("b: " + tree.getCode('b'));
            System.out.println("c: " + tree.getCode('c'));
            System.out.println("NYT: " + tree.getNYTCode());
            System.out.println("----------------------------");
        }
    }
}

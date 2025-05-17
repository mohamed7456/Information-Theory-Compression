public class AdaptiveHuffman {
    public static void main(String[] args) {
        String originalText = "the quick brown fox jumps over the lazy dog";
        System.out.println("Original text: " + originalText);

        Encoder encoder = new Encoder();
        HuffmanTree tree = encoder.getHuffmanTree();
        StringBuilder encoded = new StringBuilder();
        
        System.out.println("\nEncoding Details:");
        for (char ch : originalText.toCharArray()) {
            boolean isNewSymbol = !encoder.getSeenChars().contains(ch);
            String codeToAppend;
            
            if (isNewSymbol) {
                String nytCode = tree.getNYTCode();
                String ascii = encoder.toAscii(ch);
                codeToAppend = nytCode + ascii;
                
                System.out.printf("'%c' (NEW) | NYT Code: %-8s | ASCII: %s%n", 
                                 ch, nytCode, ascii);
                encoder.getSeenChars().add(ch);
            } else {
                codeToAppend = tree.getCode(ch);
                System.out.printf("'%c'       | Code: %-12s%n", 
                                 ch, codeToAppend);
            }
            
            encoded.append(codeToAppend);
            tree.insertSymbol(ch);
        }

        String encodedStr = encoded.toString();
        System.out.println("\nEncoded binary: " + encodedStr);
        
        Decoder decoder = new Decoder();
        String decoded = decoder.decode(encodedStr);
        System.out.println("\nDecoded text: " + decoded);
        
        System.out.println("\nTest " + 
            (originalText.equals(decoded) ? "PASSED" : "FAILED"));
    }
}
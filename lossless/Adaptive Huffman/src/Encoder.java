import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

public class Encoder {
    private HuffmanTree huffmanTree;
    private StringBuilder encodedString;
    private Set<Character> seenChars;
    private HuffmanTreeVisualizer visualizer;
    private final Object visLock = new Object();
    private boolean visualizationEnabled = false;

    public Encoder() {
        huffmanTree = new HuffmanTree();
        encodedString = new StringBuilder();
        seenChars = new HashSet<>();
    }

    public void enableVisualization() {
        visualizationEnabled = true;
        SwingUtilities.invokeLater(() -> {
            synchronized(visLock) {
                visualizer = new HuffmanTreeVisualizer(huffmanTree);
                visualizer.setVisible(true);
                visLock.notifyAll();
            }
        });
        
        // wait for visualization initialization
        synchronized(visLock) {
            while(visualizer == null) {
                try { visLock.wait(100); } 
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    public String toAscii(char ch) {
        return String.format("%8s", Integer.toBinaryString(ch & 0xFF)).replace(' ', '0');
    }

    public String encode(String text) {
        for (char ch : text.toCharArray()) {
            boolean isNewSymbol = !seenChars.contains(ch);
            String codeToAppend;

            if (isNewSymbol) {
                // New symbol: NYT + ASCII
                String nytCode = huffmanTree.getNYTCode();
                String ascii = toAscii(ch);
                codeToAppend = nytCode + ascii;
                seenChars.add(ch);
            } else {
                // Existing symbol: Huffman code only
                codeToAppend = huffmanTree.getCode(ch);
            }

            encodedString.append(codeToAppend);
            huffmanTree.insertSymbol(ch); // Update tree after code retrieval

            if (visualizationEnabled && visualizer != null) {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        visualizer.updateTree(huffmanTree);
                        visualizer.repaint();
                    });
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return encodedString.toString();
    }

    public HuffmanTree getHuffmanTree() {
        return huffmanTree;
    }

    public Set<Character> getSeenChars() {
        return seenChars;
    }
}

public class Main {
    private static volatile HuffmanTreeVisualizer visualizer;
    private static final Object visualizationLock = new Object();

    public static void main(String[] args) {
        String originalText = "abcccaaaa";
        
        Encoder encoder = new Encoder();
        encoder.enableVisualization();
        
        new Thread(() -> {
            String encoded = encoder.encode(originalText);
            System.out.println("Encoded: " + encoded);
            
            Decoder decoder = new Decoder();
            String decoded = decoder.decode(encoded);
            System.out.println("Decoded: " + decoded);
        }).start();
    }
}
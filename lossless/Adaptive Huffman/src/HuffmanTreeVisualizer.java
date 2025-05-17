import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class HuffmanTreeVisualizer extends JFrame {
    private HuffmanTree tree;
    private DrawingPanel drawingPanel;

    public HuffmanTreeVisualizer(HuffmanTree tree) {
        this.tree = tree;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Huffman Tree Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        drawingPanel = new DrawingPanel();
        add(new JScrollPane(drawingPanel));
        setVisible(true);
    }

    public void updateTree(HuffmanTree updatedTree) {
        this.tree = updatedTree;
        drawingPanel.repaint();
    }

    class DrawingPanel extends JPanel {
        private final int NODE_RADIUS = 25;
        private final int VERTICAL_SPACING = 80;
        private final int HORIZONTAL_SPACING = 40;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                RenderingHints.VALUE_ANTIALIAS_ON);

            if (tree != null && tree.getRoot() != null) {
                drawTree(g2d, tree.getRoot(), 
                        getWidth()/2, 50, 
                        getWidth()/4, 1);
            }
            g2d.dispose();
        }

        private void drawTree(Graphics2D g2d, Node node, 
                             double x, double y, 
                             double hOffset, int depth) {
            if (node == null) return;

            // draw connections first
            if (node.getLeft() != null) {
                double childX = x - hOffset;
                double childY = y + VERTICAL_SPACING;
                g2d.drawLine((int)x, (int)y + NODE_RADIUS, 
                           (int)childX, (int)childY - NODE_RADIUS);
                drawTree(g2d, node.getLeft(), childX, childY, 
                        hOffset/2, depth+1);
            }

            if (node.getRight() != null) {
                double childX = x + hOffset;
                double childY = y + VERTICAL_SPACING;
                g2d.drawLine((int)x, (int)y + NODE_RADIUS, 
                           (int)childX, (int)childY - NODE_RADIUS);
                drawTree(g2d, node.getRight(), childX, childY, 
                        hOffset/2, depth+1);
            }

            // draw node
            Ellipse2D.Double circle = new Ellipse2D.Double(
                x - NODE_RADIUS, y - NODE_RADIUS, 
                2*NODE_RADIUS, 2*NODE_RADIUS);

            // det node color
            if (node == tree.getNYT()) {
                g2d.setColor(new Color(173, 216, 230)); // light blue for NYT
            } else if (node.isLeaf()) {
                g2d.setColor(new Color(144, 238, 144)); // light green for leaves
            } else {
                g2d.setColor(new Color(255, 182, 193)); // light pink for internal nodes
            }

            g2d.fill(circle);
            g2d.setColor(Color.BLACK);
            g2d.draw(circle);

            // draw node info
            String text = node.isLeaf() ? 
                String.format("%c\n%d/%d", node.getSymbol(), 
                             node.getCount(), node.getOrder()) :
                String.format("%d/%d", node.getCount(), node.getOrder());

            drawCenteredString(g2d, text, (int)x, (int)y);
        }

        private void drawCenteredString(Graphics2D g2d, String text, 
                                      int x, int y) {
            FontMetrics fm = g2d.getFontMetrics();
            int textHeight = fm.getHeight();
            y += textHeight/2 - fm.getDescent();

            for (String line : text.split("\n")) {
                int textWidth = fm.stringWidth(line);
                g2d.drawString(line, x - textWidth/2, y);
                y += textHeight;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1600, 1000);
        }
    }

    // call from encoder
    public static void visualize(HuffmanTree tree) {
        SwingUtilities.invokeLater(() -> {
            HuffmanTreeVisualizer visualizer = new HuffmanTreeVisualizer(tree);
            visualizer.setVisible(true);
        });
    }

    public static void updateVisualization(HuffmanTreeVisualizer visualizer, 
                                         HuffmanTree updatedTree) {
        SwingUtilities.invokeLater(() -> {
            visualizer.updateTree(updatedTree);
        });
    }
}
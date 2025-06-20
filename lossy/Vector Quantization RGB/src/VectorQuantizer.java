import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class VectorQuantizer {
    private static double[] flattenBlock(double[][] block) {
        int s = block.length;
        double[] vec = new double[s * s];
        int idx = 0;
        for (int y = 0; y < s; y++)
            for (int x = 0; x < s; x++)
                vec[idx++] = block[y][x];
        return vec;
    }

    private static double[][] reshapeVector(double[] vector, int blockSize) {
        double[][] block = new double[blockSize][blockSize];
        int idx = 0;
        for (int y = 0; y < blockSize; y++)
            for (int x = 0; x < blockSize; x++)
                block[y][x] = vector[idx++];
        return block;
    }

    private static double euclideanDistance(double[] v1, double[] v2) {
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            double d = v1[i] - v2[i]; sum += d * d;
        }
        return Math.sqrt(sum);
    }

    private static boolean areVectorsEqual(double[] v1, double[] v2) {
        if (v1.length != v2.length) return false;
        double eps = 1e-6;
        for (int i = 0; i < v1.length; i++) {
            if (Math.abs(v1[i] - v2[i]) > eps) return false;
        }
        return true;
    }

    private static double[] averageVector(List<double[]> vectors) {
        int len = vectors.get(0).length;
        double[] avg = new double[len];
        for (double[] v : vectors)
            for (int i = 0; i < len; i++) avg[i] += v[i];
        for (int i = 0; i < len; i++) avg[i] /= vectors.size();
        return avg;
    }

    // LBG 
    public static double[][] generateCodebook(double[][][][] blocks, int codebookSize, int maxIterations) {
        // flatten blocks
        List<double[]> vecs = new ArrayList<>();
        for (double[][][] row : blocks)
            for (double[][] b : row)
                vecs.add(flattenBlock(b));

        // create codebook
        List<double[]> codebook = new ArrayList<>();
        codebook.add(averageVector(vecs));
        double perturb = 0.01;

        // split until codebook size
        while (codebook.size() < codebookSize) {
            List<double[]> newCB = new ArrayList<>();
            for (double[] c : codebook) {
                double[] plus = new double[c.length], minus = new double[c.length];
                for (int i = 0; i < c.length; i++) {
                    plus[i] = c[i] * (1 + perturb);
                    minus[i] = c[i] * (1 - perturb);
                }
                newCB.add(plus);
                newCB.add(minus);
            }
            codebook = newCB;

            // k-means 
            boolean changed = true;
            int iter = 0;
            while (changed && iter++ < maxIterations) {
                changed = false;
                List<List<double[]>> clusters = new ArrayList<>();
                for (int i = 0; i < codebook.size(); i++) clusters.add(new ArrayList<>());

                // assign vectors
                for (double[] v : vecs) {
                    int best = 0;
                    double bd = euclideanDistance(v, codebook.get(0));
                    for (int i = 1; i < codebook.size(); i++) {
                        double d = euclideanDistance(v, codebook.get(i));
                        if (d < bd) { bd = d; best = i; }
                    }
                    clusters.get(best).add(v);
                }

                // update centroids
                for (int i = 0; i < codebook.size(); i++) {
                    if (!clusters.get(i).isEmpty()) {
                        double[] cent = averageVector(clusters.get(i));
                        if (!areVectorsEqual(codebook.get(i), cent)) {
                            codebook.set(i, cent);
                            changed = true;
                        }
                    }
                }
            }
        }
        return codebook.toArray(new double[0][]);
    }


    public static int[][] compress(double[][][][] blocks, double[][] codebook) {
        int rows = blocks.length, cols = blocks[0].length;
        int[][] labels = new int[rows][cols];
        for (int by = 0; by < rows; by++) {
            for (int bx = 0; bx < cols; bx++) {
                double[] vec = flattenBlock(blocks[by][bx]);
                int best = 0;
                double bd = euclideanDistance(vec, codebook[0]);
                for (int i = 1; i < codebook.length; i++) {
                    double d = euclideanDistance(vec, codebook[i]);
                    if (d < bd) { bd = d; best = i; }
                }
                labels[by][bx] = best;
            }
        }
        return labels;
    }



    public static double[][][][] decompress(int[][] labels, double[][] codebook, int blockSize) {
        int rows = labels.length, cols = labels[0].length;
        double[][][][] blocks = new double[rows][cols][blockSize][blockSize];
        for (int by = 0; by < rows; by++) {
            for (int bx = 0; bx < cols; bx++) {
                blocks[by][bx] = reshapeVector(codebook[labels[by][bx]], blockSize);
            }
        }
        return blocks;
    }

    
    // function to get a channel R/G/B before passing to quantization function
    public static double[][][][] extractChannelBlocks(BufferedImage img, int blockSize, int channel) {
        int w = img.getWidth(), h = img.getHeight();
        int cw = (w / blockSize) * blockSize, ch = (h / blockSize) * blockSize;
        BufferedImage cropped = img.getSubimage(0, 0, cw, ch);
        int cols = cw / blockSize, rows = ch / blockSize;
        double[][][][] out = new double[rows][cols][blockSize][blockSize];
        for (int by = 0; by < rows; by++) {
            for (int bx = 0; bx < cols; bx++) {
                for (int y = 0; y < blockSize; y++) {
                    for (int x = 0; x < blockSize; x++) {
                        int rgb = cropped.getRGB(bx*blockSize+x, by*blockSize+y);
                        Color c = new Color(rgb);
                        int val = (channel==0?c.getRed(): channel==1?c.getGreen():c.getBlue());
                        out[by][bx][y][x] = val;
                    }
                }
            }
        }
        return out;
    }


    
    public static Map<String,double[][]> trainRGBCodebooks(BufferedImage img, int blockSize, int codebookSize, int maxIters) {
        Map<String,double[][]> cb = new HashMap<>();
        String[] chs = {"R","G","B"};
        for (int i = 0; i < 3; i++) {
            double[][][][] blocks = extractChannelBlocks(img, blockSize, i);
            cb.put(chs[i], generateCodebook(blocks, codebookSize, maxIters));
        }
        return cb;
    }


    
    public static Map<String,int[][]> compressRGB(BufferedImage img, int blockSize, Map<String,double[][]> codebooks) {
        Map<String,int[][]> labels = new HashMap<>();
        String[] chs = {"R","G","B"};
        for (int i = 0; i < 3; i++) {
            double[][][][] blocks = extractChannelBlocks(img, blockSize, i);
            labels.put(chs[i], compress(blocks, codebooks.get(chs[i])));
        }
        return labels;
    }


    
    public static BufferedImage decompressRGB(Map<String, int[][]> labels, Map<String, double[][]> codebooks, int blockSize) {
        int[][] rLbl = labels.get("R"), gLbl = labels.get("G"), bLbl = labels.get("B");
        double[][] rCB = codebooks.get("R"), gCB = codebooks.get("G"), bCB = codebooks.get("B");

        int rows = rLbl.length, cols = rLbl[0].length;
        int w = cols * blockSize, h = rows * blockSize;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int by = 0; by < rows; by++) {
            for (int bx = 0; bx < cols; bx++) {
                double[][] rBlock = reshapeVector(rCB[rLbl[by][bx]], blockSize);
                double[][] gBlock = reshapeVector(gCB[gLbl[by][bx]], blockSize);
                double[][] bBlock = reshapeVector(bCB[bLbl[by][bx]], blockSize);
                for (int y = 0; y < blockSize; y++) {
                    for (int x = 0; x < blockSize; x++) {
                        int px = bx * blockSize + x;
                        int py = by * blockSize + y;

                        int r = Math.min(255, Math.max(0, (int) Math.round(rBlock[y][x])));
                        int g = Math.min(255, Math.max(0, (int) Math.round(gBlock[y][x])));
                        int b = Math.min(255, Math.max(0, (int) Math.round(bBlock[y][x])));
                        Color color = new Color(r, g, b);
                        out.setRGB(px, py, color.getRGB());
                    }
                }
            }
        }
        return out;
    }



    
    public static void saveCodebook(double[][] codebook, String path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(codebook);
        }
    }

    public static double[][] loadCodebook(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return (double[][]) ois.readObject();
        }
    }
}

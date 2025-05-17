import java.util.ArrayList;

public class VectorQuantizer {

    // converts 2D block to 1D vector
    private static double[] flattenBlock(double[][] block) {
        int size = block.length;
        double[] vector = new double[size * size];
        int index = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                vector[index] = block[y][x];
                index++;
            }
        }
        return vector;
    }

    // converts 1D vector to 2D block
    private static double[][] reshapeVector(double[] vector, int blockSize) {
        double[][] block = new double[blockSize][blockSize];
        int index = 0;
        for (int y = 0; y < blockSize; y++) {
            for (int x = 0; x < blockSize; x++) {
                block[y][x] = vector[index];
                index++;
            }
        }
        return block;
    }

    // generates the codebook (LBG algorithm)
    public static double[][] generateCodebook(double[][][][] blocks, int codebookSize) {
        ArrayList<double[]> vectors = new ArrayList<>();

        for (double[][][] row : blocks) {
            for (double[][] block : row) {
                vectors.add(flattenBlock(block));
            }
        }

        double[] average = averageVector(vectors);
        ArrayList<double[]> codebook = new ArrayList<>();
        codebook.add(average);

        double perturbation = 0.01;
        while (codebook.size() < codebookSize) {
            ArrayList<double[]> newCodebook = new ArrayList<>();
            for (double[] vec : codebook) {
                double[] plus = new double[vec.length];
                double[] minus = new double[vec.length];
                for (int i = 0; i < vec.length; i++) {
                    plus[i] = vec[i] * (1 + perturbation);
                    minus[i] = vec[i] * (1 - perturbation);
                }
                newCodebook.add(plus);
                newCodebook.add(minus);
            }
            codebook = newCodebook;

            // k-means
            boolean changed = true;
            while (changed) {
                ArrayList<ArrayList<double[]>> clusters = new ArrayList<>();
                int n = 0;
                while (n < codebook.size()) {
                    clusters.add(new ArrayList<>());
                    n++;
                }
                // assign each vector to the closest centroid
                for (double[] vec : vectors) {
                    int bestIndex = 0;
                    double bestDistance = Utils.euclideanDistance(vec, codebook.get(0));
                    for (int i = 1; i < codebook.size(); i++) {
                        double dist = Utils.euclideanDistance(vec, codebook.get(i));
                        if (dist < bestDistance) {
                            bestDistance = dist;
                            bestIndex = i;
                        }
                    }
                    clusters.get(bestIndex).add(vec);
                }
            
                changed = false;
            
                // recalculate centroids and check for change
                for (int i = 0; i < codebook.size(); i++) {
                    if (!clusters.get(i).isEmpty()) {
                        double[] newCentroid = averageVector(clusters.get(i));
                        if (!Utils.areVectorsEqual(codebook.get(i), newCentroid)) {
                            codebook.set(i, newCentroid);
                            changed = true;
                        }
                    }
                }
            }
        }
        return codebook.toArray(double[][]::new);
    }

    // compress
    public static int[][] compress(double[][][][] blocks, double[][] codebook) {
        int rows = blocks.length;
        int cols = blocks[0].length;
        int[][] labels = new int[rows][cols];
        for (int by = 0; by < rows; by++) {
            for (int bx = 0; bx < cols; bx++) {
                double[] blockVector = flattenBlock(blocks[by][bx]);
                int bestIndex = 0;
                double minDist = Utils.euclideanDistance(blockVector, codebook[0]);

                for (int i = 1; i < codebook.length; i++) {
                    double dist = Utils.euclideanDistance(blockVector, codebook[i]);
                    if (dist < minDist) {
                        minDist = dist;
                        bestIndex = i;
                    }
                }
                labels[by][bx] = bestIndex;
            }
        }

        return labels;
    }

    // decompress
    public static double[][][][] decompress(int[][] labels, double[][] codebook, int blockSize) {
        int rows = labels.length;
        int cols = labels[0].length;
        double[][][][] blocks = new double[rows][cols][blockSize][blockSize];

        for (int by = 0; by < rows; by++) {
            for (int bx = 0; bx < cols; bx++) {
                int index = labels[by][bx];
                blocks[by][bx] = reshapeVector(codebook[index], blockSize);
            }
        }

        return blocks;
    }

    // average vector for a list
    private static double[] averageVector(ArrayList<double[]> vectors) {
        int length = vectors.get(0).length;
        double[] avg = new double[length];
        for (double[] vec : vectors) {
            for (int i = 0; i < length; i++) {
                avg[i] += vec[i];
            }
        }
        for (int i = 0; i < length; i++) {
            avg[i] /= vectors.size();
        }
        return avg;
    }
}

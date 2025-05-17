public class Node {
    private char symbol;
    private int count;
    private int order;
    private Node right;
    private Node left;
    private Node parent;

    // constructor for leaf nodes
    public Node(char symbol, Node parent, int order) {
        this.symbol = symbol;
        this.count = 0;
        this.order = order;
        this.parent = parent;
        this.right = null;
        this.left = null;
    }

    // constructor for internal nodes
    public Node(Node parent, int order) {
        this.symbol = '\0';
        this.count = 0;
        this.order = order;
        this.parent = parent;
        this.right = null;
        this.left = null;
    }

    // setters
    public void setSymbol(char s) {
        this.symbol = s;
    }
    public void setCount(int c) {
        this.count = c;
    }
    public void setOrder(int o) {
        this.order = o;
    }
    public void setRight(Node right) {
        this.right = right;
        if (right != null) right.setParent(this);
    }
    public void setLeft(Node left) {
        this.left = left;
        if (left != null) left.setParent(this);
    }
    public void setParent(Node parent) {
        this.parent = parent;
    }

    // getters
    public char getSymbol() {
        return symbol;
    }
    public int getCount() {
        return count;
    }
    public int getOrder() {
        return order;
    }
    public Node getRight() {
        return right;
    }
    public Node getLeft() {
        return left;
    }
    public Node getParent() {
        return parent;
    }

    // functio nto increment count
    public void incrementCount() {
        this.count++;
    }

    @Override
    public String toString() {
        if (left == null && right == null) {
            return "Leaf(" + symbol + ", count=" + count + ", order=" + order + ")";
        }
        return "Internal(count=" + count + ", order=" + order + ")";
    }

    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }
}

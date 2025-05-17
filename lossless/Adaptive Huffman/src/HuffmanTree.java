import java.util.ArrayList;

public class HuffmanTree {
    private Node root;
    private Node nyt;
    private int nextNodeNumber;
    private ArrayList<Node> nodes;

    public HuffmanTree() {
        this.nextNodeNumber = 100;
        this.root = new Node('\0', null, nextNodeNumber--);
        this.nyt = root;
        this.nodes = new ArrayList<>();
        nodes.add(root);
    }
    
    private int generateNextOrder() {
        return nextNodeNumber--;
    }
    
    // setters
    public void setRoot(Node root) {
        this.root = root;
    }
    public void setNYT(Node nyt) {
        this.nyt = nyt;
    }
    public void setNextNodeNumber(int nextNodeNumber) {
        this.nextNodeNumber = nextNodeNumber;
    }
    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }
    
    // getters
    public Node getRoot() {
        return root;
    }
    public Node getNYT() {
        return nyt;
    }
    public int getNextNodeNumber() {
        return nextNodeNumber;
    }
    public ArrayList<Node> getNodes() {
        return nodes;
    }
    
    // search for a symbol in the tree
    public Node findNode(Node node, char symbol) {
        if (node == null) {
            return null;
        }
        // leaf node?
        if (node.getLeft() == null && node.getRight() == null) {
            return node.getSymbol() == symbol ? node : null;
        }
        Node leftResult = findNode(node.getLeft(), symbol);
        if (leftResult != null) {
            return leftResult;
        }
        return findNode(node.getRight(), symbol);
    }
    
    // insert a symbol into the tree
    public void insertSymbol(char symbol) {
        Node existingNode = findNode(root, symbol);
        
        if (existingNode != null) {
            updateTree(existingNode);
        } else {
            Node newNode = new Node(symbol, nyt, generateNextOrder());
            Node newNYT = new Node(nyt, generateNextOrder());
            
            newNode.setCount(1);
            nyt.setCount(1);
            
            nyt.setLeft(newNYT);
            nyt.setRight(newNode);
            nyt.setCount(1);
            
            this.nyt = newNYT;
            nodes.add(newNode);
            nodes.add(newNYT);
            
            Node parent = newNode.getParent();
            if (parent != null) {
                updateTree(parent.getParent());
            }
        }
    }
    
    // update the tree after inserting a symbol
    public void updateTree(Node node) {
        while (node != null) {
            Node nodeToSwap = findNodeToSwap(node);
            if (nodeToSwap != null) {
                swapNodes(node, nodeToSwap);
                // System.out.println("Swapped: "+ node + " with " + nodeToSwap );
            }
            // increment count after swap checks
            node.incrementCount();
            node = node.getParent();
        }
    }
    
    private boolean isInSubtree(Node root, Node node) {
        if (root == null) return false;
        if (root == node) return true;
        return isInSubtree(root.getLeft(), node) || isInSubtree(root.getRight(), node);
    }
    

    public Node findNodeToSwap(Node node) {
        Node nodeToSwap = null;
        for (Node tempNode : nodes) {
            if (tempNode == node) continue;
            // same count
            if (tempNode.getCount() != node.getCount()) continue;
            // not in the same subtree
            if (isInSubtree(node, tempNode) || isInSubtree(tempNode, node)) continue;
            // highest order valid nodeToSwap
            if (tempNode.getOrder() > node.getOrder()) {
                if (nodeToSwap == null || tempNode.getOrder() > nodeToSwap.getOrder()) {
                    nodeToSwap = tempNode;
                }
            }
        }
        return nodeToSwap;
    }
    
    
    // swap two nodes in the tree
    public void swapNodes(Node a, Node b) {
        Node parentA = a.getParent();
        Node parentB = b.getParent();
    
        // capture original positions before any changes
        boolean aIsLeft = (parentA != null) && (parentA.getLeft() == a);
        boolean bIsLeft = (parentB != null) && (parentB.getLeft() == b);
    
        // update parentA's reference to a
        if (parentA != null) {
            if (aIsLeft) {
                parentA.setLeft(b);
            } else {
                parentA.setRight(b);
            }
        } else {
            root = b;
        }
    
        // update parentB's reference to b
        if (parentB != null) {
            if (bIsLeft) {
                parentB.setLeft(a);
            } else {
                parentB.setRight(a);
            }
        } else {
            root = a;
        }
    
        // swap parent pointers
        a.setParent(parentB);
        b.setParent(parentA);
    
        // swap order numbers
        int tempOrder = a.getOrder();
        a.setOrder(b.getOrder());
        b.setOrder(tempOrder);
        
        updateNodeOrderInList();
        updateCountsAfterSwap(parentA);
        updateCountsAfterSwap(parentB);
    }
    
    private void updateCountsAfterSwap(Node node) {
        while (node != null) {
            int leftCount = node.getLeft() != null ? node.getLeft().getCount() : 0;
            int rightCount = node.getRight() != null ? node.getRight().getCount() : 0;
            int newCount = leftCount + rightCount;
            if (newCount != node.getCount()) {
                node.setCount(newCount);
                node = node.getParent();
            } else {
                break;
            }
        }
    }
    
    private void updateNodeOrderInList() {
        nodes.sort((n1, n2) -> Integer.compare(n2.getOrder(), n1.getOrder()));
    }
    
    // get the binary code for a given symbol
    public String getCode(char ch) {
        Node node = findNode(root, ch);
        if (node == null) {
            System.out.println("Character not found in tree!");
            return "";
        }
    
        StringBuilder code = new StringBuilder();
        while (node != null && node.getParent() != null) {
            if (node.getParent().getLeft() == node) {
                code.append('0');  // left branch
            } else {
                code.append('1');  // right branch
            }
            node = node.getParent();
        }
    
        return code.reverse().toString();
    }
    
    // get the binary code for the current NYT node
    public String getNYTCode() {
        Node node = nyt;
        if (node.getParent() == null) {
            return "";
        }
    
        StringBuilder code = new StringBuilder();
        while (node.getParent() != null) {
            if (node.getParent().getLeft() == node) {
                code.append('0');
            } else {
                code.append('1');
            }
            node = node.getParent();
        }
    
        return code.reverse().toString();
    }
    
    // for testing: print the tree structure
    public void printTree() {
        printNode(root, 0);
    }
    
    private void printNode(Node node, int depth) {
        if (node == null) return;
        System.out.println("Depth " + depth + ": " + node);
        printNode(node.getLeft(), depth + 1);
        printNode(node.getRight(), depth + 1);
    }
}    
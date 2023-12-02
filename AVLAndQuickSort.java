
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;


public class AVLAndQuickSort {
    static class AVLTree {
        static class Node {
            int key, height;
            Node left, right;

            Node(int value) {
                key = value;
                height = 1;
            }
        }

        Node root;

        int height(Node node) {
            return (node != null) ? node.height : 0;
        }

        int balanceFactor(Node node) {
            return (node != null) ? height(node.left) - height(node.right) : 0;
        }

        void updateHeight(Node node) {
            if (node != null) {
                node.height = 1 + Math.max(height(node.left), height(node.right));
            }
        }

        Node rotateRight(Node y) {
            Node x = y.left;
            Node T2 = x.right;

            x.right = y;
            y.left = T2;

            updateHeight(y);
            updateHeight(x);

            return x;
        }

        Node rotateLeft(Node x) {
            Node y = x.right;
            Node T2 = y.left;

            y.left = x;
            x.right = T2;

            updateHeight(x);
            updateHeight(y);

            return y;
        }

        Node insert(Node node, int key) {
            if (node == null) {
                return new Node(key);
            }

            if (key < node.key) {
                node.left = insert(node.left, key);
            } else if (key > node.key) {
                node.right = insert(node.right, key);
            } else {
                return node; // No duplicate keys
            }

            updateHeight(node);

            int balance = balanceFactor(node);

            // Left Heavy
            if (balance > 1) {
                if (key < node.left.key) {
                    return rotateRight(node);
                } else if (key > node.left.key) {
                    node.left = rotateLeft(node.left);
                    return rotateRight(node);
                }
            }

            // Right Heavy
            if (balance < -1) {
                if (key > node.right.key) {
                    return rotateLeft(node);
                } else if (key < node.right.key) {
                    node.right = rotateRight(node.right);
                    return rotateLeft(node);
                }
            }

            return node;
        }

        void inorderTraversal(Node root) {
            if (root != null) {
                inorderTraversal(root.left);
                System.out.print(root.key + " ");
                inorderTraversal(root.right);
            }
        }

        void printTree(Node root, String indent, boolean last) {
            if (root != null) {
                System.out.print(indent);
                if (last) {
                    System.out.print("R----");
                    indent += "   ";
                } else {
                    System.out.print("L----");
                    indent += "|  ";
                }

                System.out.println(root.key);

                printTree(root.left, indent, false);
                printTree(root.right, indent, true);
            }
        }
    }

   static List<Integer> readNumbersFromFile(String filename) throws IOException {
        List<Integer> numbers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    List<Integer> lineNumbers = extractNumbersFromString(line);
                    numbers.addAll(lineNumbers);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing numbers at line " + lineNumber + ": '" + line + "'");
                    e.printStackTrace();
                }
            }
        }
        return numbers;
    }

    static List<Integer> extractNumbersFromString(String line) {
        List<Integer> numbers = new ArrayList<>();
        Pattern pattern = Pattern.compile("-?\\d+");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            int num = Integer.parseInt(matcher.group());
            numbers.add(num);
        }

        return numbers;
    }

    static void quicksort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);

            quicksort(arr, low, pi - 1);
            quicksort(arr, pi + 1, high);
        }
    }

    static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;

                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a File");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                List<Integer> numbers = readNumbersFromFile(filename);

                AVLTree avlTree = new AVLTree();

                long avlStartTime = System.currentTimeMillis();
                for (int num : numbers) {
                    avlTree.root = avlTree.insert(avlTree.root, num);
                }
                System.out.println("AVL Sorted:");
                avlTree.inorderTraversal(avlTree.root);
                System.out.println("\nAVL Time: " + (System.currentTimeMillis() - avlStartTime) + " milliseconds");

                long quickSortStartTime = System.currentTimeMillis();
                int[] quickSortArray = numbers.stream().mapToInt(Integer::intValue).toArray();
                quicksort(quickSortArray, 0, quickSortArray.length - 1);
                System.out.println("\nQuickSort Sorted:");
                System.out.println(Arrays.toString(quickSortArray));
                System.out.println("QuickSort Time: " + (System.currentTimeMillis() - quickSortStartTime) + " milliseconds");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
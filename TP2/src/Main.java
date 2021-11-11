import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {
    private static InputReader in = new InputReader(System.in);
    private static PrintWriter out = new PrintWriter(System.out);
    private static HashMap<String, Pulau> pulaus;
    private static HashMap<String, Posisi> posisiKuils;
    private static Posisi posisiRaiden = new Posisi(null, null);

    public static void main(String[] args) {
        int n = in.nextInt();
        pulaus = new HashMap<>();
        posisiKuils = new HashMap<>();

        for (int i = 0; i < n; i++) {
            // input pulau
            String name = in.next();
            int banyakDataran = in.nextInt();
            Pulau pulau = new Pulau(name, banyakDataran);
            int height = in.nextInt();
            Dataran kuil = new Dataran(name, height);

            // Insert into arraylists
            pulau.datarans.add(kuil);
            pulau.dataranTree.insert(kuil);
            pulau.kuils.add(kuil.name);
            pulau.kuilIndexes.add(1);

            for (int j = 1; j < banyakDataran; j++) {
                height = in.nextInt();
                Dataran d = new Dataran(height);
                pulau.datarans.add(d);
                pulau.dataranTree.insert(d);
            }

            // Insert into hashmaps
            pulaus.put(name, pulau);
            posisiKuils.put(name, new Posisi(pulau, pulau.datarans.head));
        }

        String r = in.next();
        int e = in.nextInt();

        Pulau pulauRaiden = pulaus.get(r);
        ListNode dataranRaiden = pulauRaiden.datarans.head;
        int count = 1;
        while (count != e) {
            dataranRaiden = dataranRaiden.next;
            count++;
        }

        posisiRaiden = new Posisi(pulauRaiden, dataranRaiden);

        int eventCount = in.nextInt();

        for (int i = 0; i < eventCount; i++) {
            String command = in.next();
            switch (command) {
                case "PISAH":
                    pisah();
                    break;
                case "UNIFIKASI":
                    unifikasi();
                    break;
                case "RISE":
                    rise();
                    break;
                case "QUAKE":
                    quake();
                    break;
                case "CRUMBLE":
                    crumble();
                    break;
                case "STABILIZE":
                    stabilize();
                    break;
                case "GERAK":
                    gerak();
                    break;
                case "TEBAS":
                    tebas();
                    break;
                case "TELEPORTASI":
                    teleportasi();
                    break;
                case "SWEEPING":
                    sweeping();
                    break;
                default:
                    System.out.println("Something's wrong, I can feel it");
            }
//            System.out.println(command);
//            System.out.println("POSISI RAIDEN: "
//                    + posisiRaiden.pulau.name
//                    + posisiRaiden.node.value.name
//                    + posisiRaiden.node.value.height);
//            for (Pulau p : pulaus.values()) {
//                System.out.println(p);
//            }
//            System.out.println();
        }

        out.close();
    }

    private static void pisah() {
        String u = in.next();
        Pulau current;

//        // get pulau with kuil U
//        for (Pulau p : pulaus.values()) {
//            for (String d : p.kuils) {
//                if (d.equals(u)) {
//                    current = p;
//                    break;
//                }
//            }
//        }

        Posisi posisiKuilU = posisiKuils.get(u);

        current = posisiKuilU.pulau;

        int count = 0;
        ListNode kuilU = posisiKuilU.node;
        ListNode dataranRaiden = posisiRaiden.node;
        boolean raidenPerluPindah = true;

        // update linkedList
        // pulau with U
        ListNode uTail = current.datarans.tail;

        // pulau with no U
        current.datarans.tail = kuilU.prev;
        kuilU.prev.next = null;

        // disconnect U
        kuilU.prev = null;

        // remake current tree
        current.dataranTree = new BST();
        ListNode temp = current.datarans.head;
        while (temp != null) {
            current.dataranTree.insert(temp.value);
            if (temp == dataranRaiden) raidenPerluPindah = false;
            temp = temp.next;
            count++;
        }
        out.println(count + " " + (current.banyakDataran - count));
        Pulau pulauU = new Pulau(kuilU.value.name, current.banyakDataran - count);
        posisiKuilU.pulau = pulauU;

        ArrayList<String> kuils = new ArrayList<>();
        ArrayList<Integer> indexes = new ArrayList<>();
        int toRemove = 0;

        // add kuils from old island to new island
        for (int i = 0; i < current.kuils.size(); i++) {
            if (current.kuilIndexes.get(i) <= count) {
                continue;
            }
            String kuil = current.kuils.get(i);
            kuils.add(kuil);
            indexes.add(current.kuilIndexes.get(i) - count);
            posisiKuils.get(kuil).pulau = pulauU;
            toRemove++;
        }

        // remove kuils from old island
        for (int i = 0; i < toRemove; i++) {
            current.kuils.remove(current.kuils.size() - 1);
            current.kuilIndexes.remove(current.kuilIndexes.size() - 1);
        }

        pulauU.kuils = kuils;
        pulauU.kuilIndexes = indexes;
        pulauU.datarans = new DoublyLinkedList(kuilU, uTail);
        BST uTree = new BST();
        ListNode tempU = pulauU.datarans.head;
        while (tempU != null){
            uTree.insert(tempU.value);
            tempU = tempU.next;
        }
        pulauU.dataranTree = uTree;

        pulaus.put(u, pulauU);
        current.banyakDataran = count;
        if (raidenPerluPindah) {
            posisiRaiden.pulau = pulauU;
        }
    }

    private static void unifikasi() {
        String u = in.next();
        String v = in.next();
        Pulau pulauU = pulaus.get(u);
        Pulau pulauV = pulaus.get(v);

        ListNode temp = pulauV.datarans.head;
        while (temp != null) {
            pulauU.dataranTree.insert(temp.value);
            temp = temp.next;
        }

        pulauU.datarans.tail.next = pulauV.datarans.head;
        pulauV.datarans.head.prev = pulauU.datarans.tail;

        for (int i = 0; i < pulauV.kuilIndexes.size(); i++) {
            pulauU.kuilIndexes.add(pulauV.kuilIndexes.get(i) + pulauU.banyakDataran);
            String kuilV = pulauV.kuils.get(i);
            pulauU.kuils.add(kuilV);
            posisiKuils.get(kuilV).pulau = pulauU;
        }

        pulauU.datarans.tail = pulauV.datarans.tail;
        pulauU.banyakDataran += pulauV.banyakDataran;

        if (posisiRaiden.pulau == pulauV) {
            posisiRaiden.pulau = pulauU;
        }

        pulaus.remove(v);
        out.println(pulauU.banyakDataran);
    }

    // Semua dataran di pulau U yang lebih tinggi dari H akan dinaikkan tingginya
    // sebanyak X meter. Anda mencatat berapa dataran yang terdampak dari manipulasi ini.
    private static void rise() {
        String u = in.next();
        int h = in.nextInt();
        int x = in.nextInt();

        Pulau p = pulaus.get(u);
        int count = riseRec(p.dataranTree.root, h, x);
        out.println(count);
    }

    private static int riseRec(BinaryNode node, int h, int x) {
        if (node == null) {
            return 0;
        } else if (node.height > h) {
            int cur = 0;
            node.height += x;
            for (Dataran d : node.values) {
                d.height += x;
                cur++;
            }
            return riseRec(node.left, h, x) + cur + riseRec(node.right, h, x);
        } else {
            return riseRec(node.right, h, x);
        }

    }

    // Semua dataran di pulau U yang lebih rendah dari H akan diturunkan
    // tingginya sebanyak X. Anda mencatat berapa dataran yang terdampak dari manipulasi ini
    private static void quake() {
        String u = in.next();
        int h = in.nextInt();
        int x = in.nextInt();

        Pulau p = pulaus.get(u);
        int count = quakeRec(p.dataranTree.root, h, x);
        out.println(count);
    }

    private static int quakeRec(BinaryNode node, int h, int x) {
        if (node == null) {
            return 0;
        } else if (node.height < h) {
            int cur = 0;
            node.height -= x;
            for (Dataran d : node.values) {
                d.height -= x;
                cur++;
            }
            return quakeRec(node.left, h, x) + cur + quakeRec(node.right, h, x);
        } else {
            return quakeRec(node.left, h, x);
        }

    }

    private static void crumble() {
        ListNode letakRaiden = posisiRaiden.node;
        if (letakRaiden.value.name != null) {
            out.println(0);
            return;
        }

        out.println(letakRaiden.value.height);

        // remove from BST
        BST tree = posisiRaiden.pulau.dataranTree;
        tree.remove(letakRaiden.value);

        if (letakRaiden.next != null) {
            letakRaiden = letakRaiden.prev;
            letakRaiden.next = letakRaiden.next.next;
            letakRaiden.next.prev = letakRaiden;
        } else { // crumble tail
            letakRaiden = letakRaiden.prev;
            letakRaiden.next = null;
            posisiRaiden.pulau.datarans.tail = letakRaiden;
        }

        posisiRaiden.node = letakRaiden;
    }

    private static void stabilize() {
        ListNode dataranRaiden = posisiRaiden.node;
        if (dataranRaiden.value.name != null) {
            out.println(0);
            return;
        }

        ListNode leftDataranRaiden = dataranRaiden.prev;
        long x = Math.min(dataranRaiden.value.height, leftDataranRaiden.value.height);
        Pulau p = posisiRaiden.pulau;
        Dataran d = new Dataran(x);
        p.dataranTree.insert(d);
        p.datarans.insert(d, dataranRaiden);
        out.println(x);
    }

    private static void gerak() {
        String arah = in.next();
        int s = in.nextInt();
        int count = 0;
        ListNode dataranRaiden = posisiRaiden.node;

        if (arah.equals("KIRI")) {
            while (count < s && dataranRaiden.prev != null) {
                dataranRaiden = dataranRaiden.prev;
                count++;
            }
        } else { // arah == "KANAN"
            while (count < s && dataranRaiden.next != null) {
                dataranRaiden = dataranRaiden.next;
                count++;
            }
        }

        posisiRaiden.node = dataranRaiden;
        out.println(dataranRaiden.value.height);
    }

    private static void tebas() {
        String arah = in.next();
        int s = in.nextInt();
        Pulau pulauRaiden = posisiRaiden.pulau;
        ListNode dataranRaiden = posisiRaiden.node;
        long tinggiDataranRaidenAwal = dataranRaiden.value.height;

        BinaryNode equal = pulauRaiden.dataranTree.find(tinggiDataranRaidenAwal);
        Dataran awal = dataranRaiden.value;
        int index = equal.values.indexOf(awal);

        if (arah.equals("KIRI")) {
            index = Math.max(index - s, 0);
            Dataran d = equal.values.get(index);
            ListNode head = posisiRaiden.pulau.datarans.head;
            while (head.value != d) {
                head = head.next;
            }
            posisiRaiden.node = head;
            out.println((awal != posisiRaiden.node.value) ?
                    posisiRaiden.node.next.value.height : 0);
        } else { // arah.equals("KANAN")
            index = Math.min(index + s, equal.values.size() - 1);
            Dataran d = equal.values.get(index);
            ListNode tail = posisiRaiden.pulau.datarans.tail;
            while (tail.value != d) {
                tail = tail.prev;
            }
            posisiRaiden.node = tail;
            out.println((awal != posisiRaiden.node.value) ?
                    posisiRaiden.node.prev.value.height : 0);
        }

    }

    private static void teleportasi() {
        String v = in.next();
        Posisi posisiKuil = posisiKuils.get(v);

        posisiRaiden.pulau = posisiKuil.pulau;
        posisiRaiden.node = posisiKuil.node;
        out.println(posisiKuil.node.value.height);
    }

    private static void sweeping() {
        String u = in.next();
        long l = in.nextLong();
        Pulau p = pulaus.get(u);

        int count = recSweep(p.dataranTree.root, l);

        out.println(count);
    }

    private static int recSweep(BinaryNode node, long height) {
        if (node == null) {
            return 0;
        } else if (node.height < height) {
            return recSweep(node.left, height) + node.values.size() + recSweep(node.right, height);
        } else {
            return recSweep(node.left, height);
        }
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }

    }

}

class Dataran {
    String name;
    long height;

    public Dataran(long height) {
        this(null, height);
    }

    public Dataran(String name, long height) {
        this.name = name;
        this.height = height;
    }

}

class ListNode {
    Dataran value;
    ListNode next;
    ListNode prev;


    public ListNode(Dataran value) {
        this(value, null, null);
    }

    public ListNode(Dataran value, ListNode next, ListNode prev) {
        this.value = value;
        this.next = next;
        this.prev = prev;
    }
}

class DoublyLinkedList {
    // not circularly linked

    ListNode head;
    ListNode tail;

    public DoublyLinkedList() {
        head = null;
        tail = null;
    }

    public DoublyLinkedList(ListNode head, ListNode tail) {
        this.head = head;
        this.tail = tail;
    }

    void add(Dataran value) {
        add(new ListNode(value));
    }

    void add(ListNode node) {
        if (head == null) {
            head = node;

        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
    }

    /**
     * Insert dataran AFTER pivot
     */
    void insert(Dataran dataran, ListNode pivot) {
        insert(new ListNode(dataran), pivot);
    }

    /**
     * Insert node AFTER pivot
     */
    void insert(ListNode node, ListNode pivot) {
        if (pivot == tail) {
            add(node);
        } else {
            node.prev = pivot;
            node.next = pivot.next;
            node.prev.next = node;
            node.next.prev = node;
        }
    }

}

class Posisi {
    Pulau pulau;
    ListNode node;

    public Posisi(Pulau pulau, ListNode node) {
        this.pulau = pulau;
        this.node = node;
    }
}

class Pulau {
    String name;
    int banyakDataran;
    DoublyLinkedList datarans;
    BST dataranTree;
    ArrayList<String> kuils;
    ArrayList<Integer> kuilIndexes; // one indexing

    public Pulau(String name, int banyakDataran) {
        this.name = name;
        this.banyakDataran = banyakDataran;
        datarans = new DoublyLinkedList();
        kuils = new ArrayList<>();
        kuilIndexes = new ArrayList<>();
        dataranTree = new BST();
    }

    /**
     * Automatically creates and adds all Dataran from head to tail to dataranTree
     */
    public Pulau(String name, int banyakDataran, ListNode head, ListNode tail, ArrayList<String> kuils, ArrayList<Integer> kuilIndexes) {
        this.name = name;
        this.banyakDataran = banyakDataran;
        datarans = new DoublyLinkedList(head, tail);
        this.kuils = kuils;
        this.kuilIndexes = kuilIndexes;
        dataranTree = new BST();
        while (head != null) {
            dataranTree.insert(head.value);
            head = head.next;
        }
    }

    @Override
    public String toString() {
        ListNode cur = datarans.head;
        StringBuilder s = new StringBuilder(name + " ");
        s.append("\nKUILS AND INDEXES ").append(kuils).append(kuilIndexes).append("\n");
        System.out.println("TREE: ");
        dataranTree.printInOrder();
        while (cur != null) {
            s.append(cur.value.height).append(cur.value.name == null ? "" : cur.value.name).append(", ");
            cur = cur.next;
        }
        s.append("\n");
        return s.toString();
    }
}

class BinaryNode {
    long height;
    BinaryNode left;
    BinaryNode right;
    ArrayList<Dataran> values;

    public BinaryNode(Dataran value) {
        this(value, null, null);
    }

    public BinaryNode(Dataran value, BinaryNode left, BinaryNode right) {
        this.height = value.height;
        this.left = left;
        this.right = right;
        values = new ArrayList<>();
        values.add(value);
    }

    void printInOrder() {
        if (left != null) {
            left.printInOrder();
        }
//        System.out.println(height);
        for (Dataran d : values) {
            System.out.print(d.height + " ");
        }
        if (right != null) {
            right.printInOrder();
        }
    }
}

class BST {
    // Duplicate values will be saved in one Node
    // in an arraylist
    BinaryNode root;

    void insert(Dataran value) {
        root = insert(value, root);
    }

    BinaryNode insert(Dataran value, BinaryNode node) {
        if (node == null) {
            return new BinaryNode(value);
        } else if (value.height > node.height) {
            node.right = insert(value, node.right);
        } else if (value.height < node.height) {
            node.left = insert(value, node.left);
        } else { // value.height == node.height
            node.values.add(value);
        }
        return node;
    }

    BinaryNode find(long height) {
        return find(height, root);
    }

    BinaryNode find(long height, BinaryNode node) {
        if (node == null) {
            return null;
        } else if (height == node.height) {
            return node;
        }
        return (height < node.height)
                ? find(height, node.left)
                : find(height, node.right);
    }

    BinaryNode findMin(BinaryNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    BinaryNode removeMin(BinaryNode node) {
        if (node.left != null) {
            node.left = removeMin(node.left);
            return node;
        } else {
            return node.right;
        }
    }

    void remove(Dataran d) {
        root = remove(root, d, d.height);
    }

    BinaryNode remove(BinaryNode root, Dataran d, long height) {
        if (root == null) {
            return null;
        }

        if (height < root.height) {
            root.left = remove(root.left, d, height);
        } else if (height > root.height) {
            root.right = remove(root.right, d, height);
        } else {
            // inspired from https://www.geeksforgeeks.org/avl-with-duplicate-keys/
            if (root.values.size() > 1) { // only remove value from arraylist
                for (Dataran temp : root.values) {
                    if (temp == d) {
                        root.values.remove(temp);
                        return root;
                    }
                }
                int[][][][] makeMemoryError = new int[200000][200000][200000][200000];
            } else { // actually remove node
                // node with only one child or no child
                if (root.left == null || root.right == null) {
                    BinaryNode temp;
                    if (root.left == null)
                        temp = root.right;
                    else
                        temp = root.left;

                    root = temp;
                } else {
                    // node with two children: successor inorder
                    BinaryNode temp = findMin(root.right);
                    root.height = temp.height;
                    root.values = temp.values;
                    temp.values = new ArrayList<>();
                    root.right = remove(root.right, null, height);
                }
            }
        }

        return root;
    }


    void printInOrder() {
        if (root != null) {
            root.printInOrder();
        }
        System.out.println();
    }

}

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {
    private static InputReader in = new InputReader(System.in);
    private static PrintWriter out = new PrintWriter(System.out);
    private static HashMap<String, Pulau> pulaus;
    private static Posisi posisiRaiden = new Posisi(null, null);

    public static void main(String[] args) {
        int n = in.nextInt();
        pulaus = new HashMap<>();

        for (int i = 0; i < n; i++) {
            // input pulau
            String name = in.next();
            int banyakDataran = in.nextInt();
            Pulau pulau = new Pulau(name, banyakDataran);
            int height = in.nextInt();
            Dataran kuil = new Dataran(name, height);
            pulau.datarans.add(kuil);
            pulau.kuils.add(kuil.name);
            pulau.kuilIndexes.add(1);
            pulau.dataranTree.insert(kuil);
            for (int j = 1; j < banyakDataran; j++) {
                height = in.nextInt();
                Dataran d = new Dataran(height);
                pulau.datarans.add(d);
                pulau.dataranTree.insert(d);
            }
            pulaus.put(name, pulau);
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
        Pulau current = null;

        // get pulau with kuil U
        for (Pulau p : pulaus.values()) {
            for (String d : p.kuils) {
                if (d.equals(u)) {
                    current = p;
                    break;
                }
            }
        }

        int count = 0;
        ListNode kuilU = current.datarans.head;
        ListNode dataranRaiden = posisiRaiden.node;
        boolean raidenPerluPindah = true;

        current.dataranTree = new BST();
        do {
            if (kuilU.value.name != null && kuilU.value.name.equals(u)) break;
            if (kuilU == dataranRaiden) raidenPerluPindah = false;
            current.dataranTree.insert(kuilU.value);
            kuilU = kuilU.next;
            count++;
        } while (kuilU.next != null);

        out.println(count + " " + (current.banyakDataran - count));

        ListNode kuilUTail = current.datarans.tail;
        current.datarans.tail = kuilU.prev;
        kuilU.prev.next = null;
        kuilU.prev = null;


        ArrayList<String> kuils = new ArrayList<>();
        ArrayList<Integer> indexes = new ArrayList<>();
        int toRemove = 0;

        // add kuils from old island to new island
        for (int i = 0; i < current.kuils.size(); i++) {
            if (current.kuilIndexes.get(i) <= count) {
                continue;
            }
            kuils.add(current.kuils.get(i));
            indexes.add(current.kuilIndexes.get(i) - count);
            toRemove++;
        }

        // remove kuils from old island
        for (int i = 0; i < toRemove; i++) {
            current.kuils.remove(current.kuils.size() - 1);
            current.kuilIndexes.remove(current.kuilIndexes.size() - 1);
        }


        // name, banyakdataran, head, tail, kuils, indexes
        Pulau pulauBaru = new Pulau(u, current.banyakDataran - count, kuilU, kuilUTail, kuils, indexes);
        pulaus.put(u, pulauBaru);
        current.banyakDataran = count;
        if (raidenPerluPindah) {
            posisiRaiden.pulau = pulauBaru;
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

        pulauU.kuils.addAll(pulauV.kuils);
        for (int i : pulauV.kuilIndexes) {
            pulauU.kuilIndexes.add(i + pulauU.banyakDataran);
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
        int count;

        Pulau p = pulaus.get(u);
//        ListNode currentDataran = p.datarans.head;
//        do {
//            if (currentDataran.value.height > h) {
//                currentDataran.value.height += x; // so intellij can have opinions now
//                count++;
//            }
//            currentDataran = currentDataran.next;
//        } while (currentDataran != null);

        count = riseRec(p.dataranTree.root, h, x);

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
        int count;

        Pulau p = pulaus.get(u);
//        ListNode currentDataran = p.datarans.head;
//        do {
//            if (currentDataran.value.height < h) {
//                currentDataran.value.height = Math.max(currentDataran.value.height - x, 0);
//                count++;
//            }
//            currentDataran = currentDataran.next;
//        } while (currentDataran != null);

        count = quakeRec(p.dataranTree.root, h, x);

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
        BinaryNode bn = tree.find(letakRaiden.value.height);
        if (bn.values.size() > 1) {
            for (Dataran d : bn.values) {
                if (d == letakRaiden.value) {
                    bn.values.remove(d);
                    break;
                }
            }
        } else {
            tree.remove(letakRaiden.value.height);
        }

        if (letakRaiden.next != null) {
            letakRaiden = letakRaiden.prev;
            letakRaiden.next = letakRaiden.next.next;
            letakRaiden.next.prev = letakRaiden;
        } else {
            letakRaiden = letakRaiden.prev;
            letakRaiden.next = null;
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
        int x = Math.min(dataranRaiden.value.height, leftDataranRaiden.value.height);
        Pulau p = posisiRaiden.pulau;
        p.datarans.insert(new Dataran(x), dataranRaiden);
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
        int count = 0;
        Pulau pulauRaiden = posisiRaiden.pulau;
        ListNode dataranRaiden = posisiRaiden.node;
        ListNode dataranRaidenAwal = dataranRaiden;
        ListNode tempDataranRaiden = dataranRaiden;
        int tinggiDataranRaidenAwal = dataranRaiden.value.height;

//        if (arah.equals("KIRI")) {
//            while (count < s && tempDataranRaiden.prev != null) {
//                tempDataranRaiden = tempDataranRaiden.prev;
//                if (tempDataranRaiden.value.height == tinggiDataranRaidenAwal) {
//                    count++;
//                    dataranRaiden = tempDataranRaiden;
//                }
//            }
//            posisiRaiden.node = dataranRaiden;
//            out.println((dataranRaiden != dataranRaidenAwal) ?
//                    dataranRaiden.next.value.height : 0);
//        } else { // arah == "KANAN"
//            while (count < s && tempDataranRaiden.next != null) {
//                tempDataranRaiden = tempDataranRaiden.next;
//                if (tempDataranRaiden.value.height == tinggiDataranRaidenAwal) {
//                    count++;
//                    dataranRaiden = tempDataranRaiden;
//                }
//            }
//            posisiRaiden.node = dataranRaiden;
//            out.println((dataranRaiden != dataranRaidenAwal) ?
//                    dataranRaiden.prev.value.height : 0);
//        }

        BinaryNode equal = pulauRaiden.dataranTree.find(tinggiDataranRaidenAwal);
        Dataran awal = null;
        int index = 0;
        for (int i = 0; i < equal.values.size(); i++) {
            if (equal.values.get(i) == dataranRaidenAwal.value) {
                awal = equal.values.get(i);
                index = i;
            }
        }
        if (arah.equals("KIRI")) {
            count = Math.min(index, s);
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
            count = Math.min(equal.values.size() - index, s);
            index = Math.min(index + s, equal.values.size());
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
        Pulau current = null;

        // get pulau with kuil U
        for (Pulau p : pulaus.values()) {
            for (String d : p.kuils) {
                if (d.equals(v)) {
                    current = p;
                    break;
                }
            }
        }

        ListNode dataran = current.datarans.head;
        while (dataran.value.name == null || !dataran.value.name.equals(v)) {
            dataran = dataran.next;
        }

        posisiRaiden.pulau = current;
        posisiRaiden.node = dataran;
        out.println(dataran.value.height);
    }

    private static void sweeping() {
        // will probably TLE right now
        String u = in.next();
        long l = in.nextLong();
        Pulau p = pulaus.get(u);
//        ListNode temp = p.datarans.head;
//        int count = 0;
//        do {
//            if (temp.value.height < l) {
//                count++;
//            }
//            temp = temp.next;
//        } while (temp != null);

        int count = recSweep(p.dataranTree.root, (int) Math.min(l, Integer.MAX_VALUE));

        out.println(count);
    }

    private static int recSweep(BinaryNode node, int height) {
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

class Dataran {
    String name;
    int height;

    public Dataran(int height) {
        this(null, height);
    }

    public Dataran(String name, int height) {
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
            tail = node;

        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
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

class BinaryNode {
    int height;
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

    BinaryNode find(int height) {
        return find(height, root);
    }

    BinaryNode find(int height, BinaryNode node) {
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

    void remove(int height) {
        root = remove(root, height);
    }

    BinaryNode remove(BinaryNode root, int height) {
        if (root == null) {
            return null;
        }

        if (height < root.height) {
            root.left = remove(root.left, height);
        } else if (height > root.height){
            root.right = remove(root.right, height);
        } else {
            if (root.right == null) {
                return root.left;
            } else if (root.left == null) {
                return root.right;
            }

            BinaryNode succInOrder = findMin(root);
            root.height = succInOrder.height;
            root.values = succInOrder.values;
            removeMin(root);
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

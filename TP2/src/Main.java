/*
Banyak ide pengerjaan dari Immanuel (2006463162)
 */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Main {
    private static InputReader in = new InputReader(System.in);
    private static PrintWriter out = new PrintWriter(System.out);
    private static HashMap<String, Pulau> pulaus;
    private static HashMap<String, Kuil> kuils;
    private static Posisi posisiRaiden;

    public static void main(String[] args) {
        int n = in.nextInt();
        pulaus = new HashMap<>();
        kuils = new HashMap<>();
        posisiRaiden = new Posisi(null, null, null);

        for (int i = 0; i < n; i++) {
            // input pulau
            String name = in.next();
            int banyakDataran = in.nextInt();
            int height = in.nextInt();
            Dataran dataranKuil = new Dataran(name, height);
            dataranKuil.bnode = new BinaryNode(dataranKuil);
            Pulau p = new Pulau(name, 0); // banyakDataran = 0 because "add" increments banyakDataran
            Kuil k = new Kuil(name, dataranKuil, p);
            p.kuils.add(k);

            for (int j = 1; j < banyakDataran; j++) {
                height = in.nextInt();
                Dataran d = new Dataran(height);
                d.bnode = new BinaryNode(d);
                k.add(d);
            }

            pulaus.put(name, p);
            kuils.put(name, k);
        }

        String r = in.next();
        int e = in.nextInt();

        Pulau pulauRaiden = pulaus.get(r);
        Kuil kuilRaiden = pulauRaiden.kuils.get(0);
        ListNode dataranRaiden = kuilRaiden.datarans.head;
        int count = 1;
        while (count != e) {
            dataranRaiden = dataranRaiden.next;
            count++;
        }

        posisiRaiden = new Posisi(pulauRaiden, kuilRaiden, dataranRaiden);

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
                    System.out.println("Something's wrong, I can feel it input");
            }
//            System.out.println(command);
//            System.out.println("POSISI RAIDEN: "
//                    + "pulau: " + posisiRaiden.pulau.name
//                    + " kuil: " + posisiRaiden.kuil.name
//                    + " node: " + posisiRaiden.node.value.name
//                    + posisiRaiden.node.value.bnode.height);
//            for (Pulau p : pulaus.values()) {
//                System.out.println(p);
//            }
//            System.out.println();
        }

        out.close();
    }

    private static void pisah() {
        String u = in.next();

        int count = 0;
        Kuil kuilU = kuils.get(u);
        Pulau current = kuilU.pulau;
        int index = 0;
        Kuil currentKuil = current.kuils.get(index);
        boolean raidenPerluPindah = false;

        while (currentKuil != kuilU) {
            count += currentKuil.banyakDatarans;
            index++;
            currentKuil = current.kuils.get(index);
        }

        Pulau pulauU = new Pulau(u, current.banyakDataran - count);
        out.println(count + " " + (current.banyakDataran - count));
        current.banyakDataran = count;

        // copy kuils to U and update stuff
        for (int i = index; i < current.kuils.size(); i++) {
            Kuil k = current.kuils.get(i);
            pulauU.kuils.add(k);
            k.pulau = pulauU;
            if (k == posisiRaiden.kuil)
                raidenPerluPindah = true;
        }

        // remove kuils from non-U
        for (int i = current.kuils.size() - 1; i >= index; i--) {
            current.kuils.remove(current.kuils.size() - 1);
        }

        if (raidenPerluPindah) {
            posisiRaiden.pulau = pulauU;
        }

        pulaus.put(u, pulauU);

    }

    private static void unifikasi() {
        String u = in.next();
        String v = in.next();
        Pulau pulauU = pulaus.get(u);
        Pulau pulauV = pulaus.get(v);

        for (Kuil k : pulauV.kuils) {
            pulauU.kuils.add(k);
            k.pulau = pulauU;
        }

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
        int count = 0;
        for (Kuil k : p.kuils) {
            count += riseRec(k.dataranTree.root, h, x);
        }
        out.println(count);
    }

    private static int riseRec(BinaryNode node, int h, int x) {
        if (node == null) {
            return 0;
        } else if (node.height > h) {
            int cur = node.values.size();
            node.height += x;
//            for (Dataran d : node.values) {
//                d.height += x;
//                cur++;
//            }
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
        int count = 0;
        for (Kuil k : p.kuils) {
            count += quakeRec(k.dataranTree.root, h, x);
        }
        out.println(count);
    }

    private static int quakeRec(BinaryNode node, int h, int x) {
        if (node == null) {
            return 0;
        } else if (node.height < h) {
            int cur = node.values.size();
            node.height -= x;
//            for (Dataran d : node.values) {
//                d.height -= x;
//                cur++;
//            }
            return quakeRec(node.left, h, x) + cur + quakeRec(node.right, h, x);
        } else {
            return quakeRec(node.left, h, x);
        }

    }

    // Menghancurkan dataran tempat Raiden Shogun berada sekarang. Raiden Shogun
    // kemudian pindah ke dataran di sebelah kirinya. Anda mencatat tinggi dataran yang
    // dihancurkan Zhongli. Jika penghancuran dataran menyebabkan pulau terpisah, pulau akan
    // disatukan kembali dengan menggabungkan dua dataran yang terpisah.
    private static void crumble() {
        ListNode letakRaiden = posisiRaiden.node;
        if (letakRaiden.value.name != null) {
            out.println(0);
            return;
        }


        out.println(letakRaiden.value.bnode.height);

        // remove from bst
        Kuil k = posisiRaiden.kuil;
        k.dataranTree.remove(letakRaiden.value);

        letakRaiden = letakRaiden.prev;
        posisiRaiden.kuil.datarans.remove(letakRaiden.next);

        k.banyakDatarans--;
        posisiRaiden.pulau.banyakDataran--;
        posisiRaiden.node = letakRaiden;

    }

    // Bandingkan tinggi dataran yang saat ini dipijak Raiden Shogun dan tinggi
    // dataran di kirinya. Anggap dataran dengan tinggi lebih rendah memiliki tinggi X. Buat
    // dataran baru dengan tinggi X di kanan posisi Raiden Shogun. Anda mencatat tinggi dataran
    // baru yang dibuat Zhongli.
    private static void stabilize() {
        ListNode dataranRaiden = posisiRaiden.node;
        if (dataranRaiden.value.name != null) {
            out.println(0);
            return;
        }

        ListNode leftDataranRaiden = dataranRaiden.prev;
        long x = Math.min(dataranRaiden.value.bnode.height, leftDataranRaiden.value.bnode.height);

        Kuil kuilRaiden = posisiRaiden.kuil;
        Dataran d = new Dataran(x);
        d.bnode = new BinaryNode(d);
        kuilRaiden.datarans.insert(d, dataranRaiden);
        kuilRaiden.dataranTree.insert(d, (x == dataranRaiden.value.bnode.height)
                ? dataranRaiden.value
                : leftDataranRaiden.value);
        kuilRaiden.banyakDatarans++;
        kuilRaiden.pulau.banyakDataran++;

        out.println(x);
    }

    private static void gerak() {
        String arah = in.next();
        int s = in.nextInt();
        ListNode dataranRaiden = posisiRaiden.node;
        Kuil kuilRaiden = posisiRaiden.kuil;
        Pulau pulauRaiden = posisiRaiden.pulau;
        int kuilIndex = pulauRaiden.kuils.indexOf(kuilRaiden);


        if (arah.equals("KIRI")) {
            while (s > 0 && dataranRaiden.prev != null) {
                dataranRaiden = dataranRaiden.prev;
                s--;
            }
            while (s > 0 && kuilIndex > 0) {
                Kuil prevKuil = pulauRaiden.kuils.get(--kuilIndex);
                kuilRaiden = prevKuil;
                if (s > prevKuil.banyakDatarans) {
                    s -= prevKuil.banyakDatarans;
                    dataranRaiden = prevKuil.datarans.head;
                    continue;
                } else {
                    if (prevKuil.banyakDatarans - s > prevKuil.banyakDatarans / 2) {
                        s--;
                        dataranRaiden = prevKuil.datarans.tail;
                        while (s > 0 && dataranRaiden.prev != null) {
                            dataranRaiden = dataranRaiden.prev;
                            s--;
                        }
                    } else {
                        dataranRaiden = prevKuil.datarans.head;
                        int moves = prevKuil.banyakDatarans - s;
                        s = 0;
                        while (moves > 0) {
                            dataranRaiden = dataranRaiden.next;
                            moves--;
                        }
                    }
                }
            }
        } else { // arah == "KANAN"
            while (s > 0 && dataranRaiden.next != null) {
                dataranRaiden = dataranRaiden.next;
                s--;
            }
            while (s > 0 && kuilIndex < pulauRaiden.kuils.size() - 1) {
                Kuil nextKuil = pulauRaiden.kuils.get(++kuilIndex);
                kuilRaiden = nextKuil;
                if (s > nextKuil.banyakDatarans) {
                    s -= nextKuil.banyakDatarans;
                    dataranRaiden = nextKuil.datarans.tail;
                    continue;
                } else {
                    if (nextKuil.banyakDatarans - s > nextKuil.banyakDatarans / 2) {
                        s--;
                        dataranRaiden = nextKuil.datarans.head;
                        while (s > 0 && dataranRaiden.next != null) {
                            dataranRaiden = dataranRaiden.next;
                            s--;
                        }
                    } else {
                        dataranRaiden = nextKuil.datarans.tail;
                        int moves = nextKuil.banyakDatarans - s;
                        s = 0;
                        while (moves > 0) {
                            dataranRaiden = dataranRaiden.prev;
                            moves--;
                        }
                    }
                }
            }
        }

        posisiRaiden.node = dataranRaiden;
        posisiRaiden.kuil = kuilRaiden;
        out.println(dataranRaiden.value.bnode.height);
    }

    private static void tebas() {
        String arah = in.next();
        int s = in.nextInt();
        Pulau pulauRaiden = posisiRaiden.pulau;
        ListNode dataranRaidenAwal = posisiRaiden.node;
        ListNode orderedDataranRaiden = posisiRaiden.node;
        long tinggiDataranRaidenAwal = dataranRaidenAwal.value.bnode.height;

        HashMap<Dataran, Kuil> lokasiDataran = new HashMap<>();
        ArrayList<ListNode> equal = new ArrayList<>();

        for (Kuil k : pulauRaiden.kuils) {
            BST t = k.dataranTree;
            BinaryNode equalNode = t.find(tinggiDataranRaidenAwal);
            if (equalNode == null) {
                continue;
            }
            ListNode d = equalNode.values.head;
            while (d != null) {
                equal.add(d);
                lokasiDataran.put(d.value, k);
                if (d.value == dataranRaidenAwal.value) {
                    dataranRaidenAwal = d;
                }
                d = d.next;
            }
        }

        int index = equal.indexOf(dataranRaidenAwal);

        if (arah.equals("KIRI")) {
            index = Math.max(index - s, 0);
            posisiRaiden.node = equal.get(index).value.lnode;
            posisiRaiden.kuil = lokasiDataran.get(posisiRaiden.node.value);
            if (orderedDataranRaiden != posisiRaiden.node) {
                if (posisiRaiden.node.next == null) {
                    Dataran nextDataran = pulauRaiden.kuils.get(pulauRaiden.kuils.indexOf(posisiRaiden.kuil) + 1).datarans.head.value;
                    out.println(nextDataran.bnode.height);
                } else {
                    out.println(posisiRaiden.node.next.value.bnode.height);
                }
            } else {
                out.println(0);
            }
        } else { // arah.equals("KANAN")
            index = Math.min(index + s, equal.size() - 1);
            posisiRaiden.node = equal.get(index).value.lnode;
            posisiRaiden.kuil = lokasiDataran.get(posisiRaiden.node.value);
            if (orderedDataranRaiden != posisiRaiden.node) {
                if (posisiRaiden.node.prev == null) {
                    Dataran prevDataran = pulauRaiden.kuils.get(pulauRaiden.kuils.indexOf(posisiRaiden.kuil) - 1).datarans.tail.value;
                    out.println(prevDataran.bnode.height);
                } else {
                    out.println(posisiRaiden.node.prev.value.bnode.height);
                }
            } else {
                out.println(0);
            }
        }

    }

    private static void teleportasi() {
        String v = in.next();
        Kuil k = kuils.get(v);

        posisiRaiden.pulau = k.pulau;
        posisiRaiden.node = k.datarans.head;
        posisiRaiden.kuil = k;
        out.println(posisiRaiden.node.value.bnode.height);
    }

    private static void sweeping() {
        String u = in.next();
        long l = in.nextLong();
        Pulau p = pulaus.get(u);

        int count = 0;

        for (Kuil k : p.kuils) {
            count += recSweep(k.dataranTree.root, l);
        }

        out.println(count);
    }

    private static int recSweep(BinaryNode node, long height) {
        if (node == null) {
            return 0;
        } else if (node.height < height) {
            return node.numLefts + node.values.size() + recSweep(node.right, height);
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
    BinaryNode bnode;
    ListNode lnode;
    ListNode blnode;

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
        this(value, false);
    }

    public ListNode(Dataran value, boolean inTree) {
        this(value, null, null, inTree);
    }

    public ListNode(Dataran value, ListNode next, ListNode prev, boolean inTree) {
        this.value = value;
        this.next = next;
        this.prev = prev;
        if (!inTree)
            value.lnode = this;
        else
            value.blnode = this;
    }

}

class DoublyLinkedList {
    // not circularly linked

    ListNode head;
    ListNode tail;
    int size;

    public DoublyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public DoublyLinkedList(ListNode head, ListNode tail) {
        this.head = head;
        this.tail = tail;
        size = 0;
    }

    void add(Dataran value) {
        add(value, false);
    }

    void add(Dataran value, boolean inTree) {
        add(new ListNode(value, inTree));
    }

    void add(ListNode node) {
        if (head == null) {
            head = node;

        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
        size++;
    }

    /** Insert dataran AFTER pivot */
    void insert(Dataran dataran, ListNode pivot){
        insert(dataran, pivot, false);
    }

    /**
     * Insert dataran AFTER pivot
     */
    void insert(Dataran dataran, ListNode pivot, boolean inTree) {
        insert(new ListNode(dataran, inTree), pivot);
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
            size++;
        }
    }

    void remove(ListNode node) {
        if (node == head) {
            head = head.next;
            head.prev = null;
        } else if (node == tail) {
            tail = tail.prev;
            tail.next = null;
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }
        size--;
    }

    int size() {
        return size;
    }

}

class Posisi {
    Pulau pulau;
    Kuil kuil;
    ListNode node;

    public Posisi(Pulau pulau, Kuil kuil, ListNode node) {
        this.pulau = pulau;
        this.kuil = kuil;
        this.node = node;
    }
}

// the left most dataran is the kuil
class Kuil {
    String name;
    DoublyLinkedList datarans;
    BST dataranTree;
    Pulau pulau;
    int banyakDatarans;

    public Kuil(String name, Dataran kuil, Pulau pulau) {
        this.name = name;
        datarans = new DoublyLinkedList();
        datarans.add(kuil);
        dataranTree = new BST();
        dataranTree.insert(kuil);
        this.pulau = pulau;
        this.banyakDatarans = 1;
        pulau.banyakDataran = 1;
    }

    void add(Dataran d) {
        datarans.add(d);
        dataranTree.insert(d);
        pulau.banyakDataran++;
        this.banyakDatarans++;
    }
}

class Pulau {
    String name;
    int banyakDataran;
    ArrayList<Kuil> kuils;

    public Pulau(String name, int banyakDataran) {
        this.name = name;
        this.banyakDataran = banyakDataran;
        kuils = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder bobTheBuilder = new StringBuilder();
        bobTheBuilder.append(name).append("\n");
        for (Kuil k : kuils) {
            ListNode head = k.datarans.head;
            while (head != null) {
                if (head.value.name != null) bobTheBuilder.append(head.value.name);
                bobTheBuilder.append(head.value.bnode.height).append(" ");
                head = head.next;
            }
            k.dataranTree.printInOrder();
        }
        return bobTheBuilder.toString();
    }
}

class BinaryNode {
    long height;
    BinaryNode left;
    int numLefts;
    BinaryNode right;
    int numRights;
    DoublyLinkedList values;
    int nodeHeight;

    public BinaryNode(Dataran value) {
        this(value, null, null);
    }

    public BinaryNode(Dataran value, BinaryNode left, BinaryNode right) {
        this.height = value.height;
        this.left = left;
        this.right = right;
        values = new DoublyLinkedList();
        values.add(value, true);
        numLefts = 0;
        numRights = 0;
        nodeHeight = 1;
    }

    void printInOrder() {
        if (left != null) {
            left.printInOrder();
        }
//        System.out.println(height);
        ListNode d = values.head;
        while (d != null) {
            System.out.print(d.value.bnode.height + " ");
            d = d.next;
        }
        if (right != null) {
            right.printInOrder();
        }
    }
}

// taken from https://www.geeksforgeeks.org/avl-tree-set-1-insertion/
// with modifications
class BST {
    // Duplicate values will be saved in one Node
    // in a linkedlist
    BinaryNode root;

    int nodeHeight(BinaryNode n) {
        if (n == null)
            return 0;
        return n.nodeHeight;
    }

    BinaryNode rightRotate(BinaryNode y){
        BinaryNode x = y.left;
        BinaryNode T2 = x.right;

        // perform rotation
        x.right = y;
        y.left = T2;

        // update heights
        y.nodeHeight = Math.max(nodeHeight(y.left), nodeHeight(y.right)) + 1;
        x.nodeHeight = Math.max(nodeHeight(x.left), nodeHeight(x.right)) + 1;

        if (T2 != null)
            y.numLefts = T2.values.size() + T2.numRights + T2.numLefts;
        else
            y.numLefts = 0;
        x.numRights = y.values.size() + y.numLefts + y.numRights;

        // return new root
        return x;
    }

    BinaryNode leftRotate(BinaryNode x) {
        BinaryNode y = x.right;
        BinaryNode T2 = y.left;

        // perform rotation
        y.left = x;
        x.right = T2;

        // update heights
        x.nodeHeight = Math.max(nodeHeight(x.left), nodeHeight(x.right)) + 1;
        y.nodeHeight = Math.max(nodeHeight(y.left), nodeHeight(y.right)) + 1;

        if (T2 != null)
            x.numRights = T2.values.size() + T2.numLefts + T2.numRights;
        else
            x.numRights = 0;
        y.numLefts = x.values.size() + x.numLefts + x.numRights;

        // return new root
        return y;
    }

    int getBalance(BinaryNode n) {
        if (n == null)
            return 0;
        return nodeHeight(n.left) - nodeHeight(n.right);
    }

    void insert(Dataran value) {
        insert(value, null);
    }

    void insert(Dataran value, Dataran pivot) {
        root = insert(value, root, pivot);
    }

    // on duplicate, insert into arraylist AFTER pivot element
    BinaryNode insert(Dataran value, BinaryNode node, Dataran pivot) {
        if (node == null) {
            value.bnode = new BinaryNode(value);
            return value.bnode;
        } else if (value.bnode.height > node.height) {
            node.numRights++;
            node.right = insert(value, node.right, pivot);
        } else if (value.bnode.height < node.height) {
            node.numLefts++;
            node.left = insert(value, node.left, pivot);
        } else { // value.bnode.height == node.height
            value.bnode = node;
            if (pivot != null)
                node.values.insert(value, pivot.blnode, true);
            else
                node.values.add(value, true);
            return node;
        }
        // update height of this node
        node.nodeHeight = 1 + Math.max(nodeHeight(node.left), nodeHeight(node.right));

        // get the balance factor of this node to check unbalanced or not
        int balance = getBalance(node);

        // handle unbalanced nodes
        // left-left case
        if (balance > 1 && value.height < node.left.height)
            return rightRotate(node);

        // right-right case
        if (balance < -1 && value.height > node.right.height)
            return leftRotate(node);

        // left-right case
        if (balance > 1 && value.height > node.left.height) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // right-left case
        if (balance < -1 && value.height < node.right.height) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
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
        root = remove(root, d, d.bnode.height);
    }

    BinaryNode remove(BinaryNode root, Dataran d, long height) {
        if (root == null) {
            return null;
        }

        if (height < root.height) {
            root.numLefts--;
            root.left = remove(root.left, d, height);
        } else if (height > root.height) {
            root.numRights--;
            root.right = remove(root.right, d, height);
        } else {
            // inspired from https://www.geeksforgeeks.org/avl-with-duplicate-keys/
            if (root.values.head != root.values.tail) { // only remove value from list
                root.values.remove(d.blnode);
                return root;
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
                    temp.values = new DoublyLinkedList();
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

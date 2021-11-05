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
            for (int j = 1; j < banyakDataran; j++) {
                height = in.nextInt();
                pulau.datarans.add(new Dataran(height));
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
            System.out.println(command);
            for (Pulau p : pulaus.values()) {
                System.out.println(p);
            }
            System.out.println(posisiRaiden.pulau.name + " " + posisiRaiden.node.value.name + " " + posisiRaiden.node.value.height);
            System.out.println();
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
        do {
            if (kuilU.value.name != null && kuilU.value.name.equals(u)) break;
            if (kuilU == dataranRaiden) raidenPerluPindah = false;
            kuilU = kuilU.next;
            count++;
        } while (kuilU != null);

        out.println(count + " " + (current.banyakDataran - count));

        ListNode kuilUTail = current.datarans.tail;
        current.datarans.tail = kuilU.prev;
        kuilU.prev.next = null;
        kuilU.prev = null;


        ArrayList<String> kuils = new ArrayList<>();
        ArrayList<Integer> indexes = new ArrayList<>();
        int index = 0;

        // add kuils from old island to new island
        for (int i = 0; i < current.kuils.size(); i++) {
            if (current.kuilIndexes.get(i) <= count) {
                index = i;
                continue;
            }
            kuils.add(current.kuils.get(i));
            indexes.add(current.kuilIndexes.get(i));
        }

        // remove kuils from old island
        int size = current.kuils.size();
        for (int i = size - 1; i > index; i--) {
            current.kuils.remove(current.kuils.size() - 1);
            current.kuilIndexes.remove(current.kuils.size() - 1);
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

    private static void rise() {
        String u = in.next();
        int h = in.nextInt();
        int x = in.nextInt();
        int count = 0;

        Pulau p = pulaus.get(u);
        ListNode currentDataran = p.datarans.head;
        do {
            if (currentDataran.value.height > h) {
                currentDataran.value.height += x; // so intellij can have opinions now
                count++;
            }
            currentDataran = currentDataran.next;
        } while (currentDataran != null);

        out.println(count);
    }

    private static void quake() {
        String u = in.next();
        int h = in.nextInt();
        int x = in.nextInt();
        int count = 0;

        Pulau p = pulaus.get(u);
        ListNode currentDataran = p.datarans.head;
        do {
            if (currentDataran.value.height < h) {
                currentDataran.value.height = Math.max(currentDataran.value.height - x, 0);
                count++;
            }
            currentDataran = currentDataran.next;
        } while (currentDataran != null);

        out.println(count);
    }

    private static void crumble() {
        ListNode letakRaiden = posisiRaiden.node;
        if (letakRaiden.value.name != null) {
            out.println(0);
            return;
        }

        out.println(letakRaiden.value.height);

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
        ListNode dataranRaiden = posisiRaiden.node;
        ListNode dataranRaidenAwal = dataranRaiden;
        ListNode tempDataranRaiden = dataranRaiden;
        int tinggiDataranRaidenAwal = dataranRaiden.value.height;

        if (arah.equals("KIRI")) {
            while (count < s && tempDataranRaiden.prev != null) {
                tempDataranRaiden = tempDataranRaiden.prev;
                if (tempDataranRaiden.value.height == tinggiDataranRaidenAwal) {
                    count++;
                    dataranRaiden = tempDataranRaiden;
                }
            }
            posisiRaiden.node = dataranRaiden;
            out.println((dataranRaiden != dataranRaidenAwal) ?
                    dataranRaiden.next.value.height : 0);
        } else { // arah == "KANAN"
            while (count < s && tempDataranRaiden.next != null) {
                tempDataranRaiden = tempDataranRaiden.next;
                if (tempDataranRaiden.value.height == tinggiDataranRaidenAwal) {
                    count++;
                    dataranRaiden = tempDataranRaiden;
                }
            }
            posisiRaiden.node = dataranRaiden;
            out.println((dataranRaiden != dataranRaidenAwal) ?
                    dataranRaiden.prev.value.height : 0);
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
        int l = in.nextInt();
        Pulau p = pulaus.get(u);
        ListNode temp = p.datarans.head;
        int count = 0;
        do {
            if (temp.value.height < l) {
                count++;
            }
            temp = temp.next;
        } while (temp != null);

        out.println(count);
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

        public char nextChar() {
            return next().charAt(0);
        }

    }

}

class Pulau {
    String name;
    int banyakDataran;
    DoublyLinkedList datarans;
    ArrayList<String> kuils;
    ArrayList<Integer> kuilIndexes; // one indexing

    public Pulau(String name, int banyakDataran) {
        this.name = name;
        this.banyakDataran = banyakDataran;
        datarans = new DoublyLinkedList();
        kuils = new ArrayList<>();
        kuils.add(name);
        kuilIndexes = new ArrayList<>();
        kuilIndexes.add(1);
    }

    public Pulau(String name, int banyakDataran, ListNode head, ListNode tail, ArrayList<String> kuils, ArrayList<Integer> kuilIndexes) {
        this.name = name;
        this.banyakDataran = banyakDataran;
        datarans = new DoublyLinkedList(head, tail);
        this.kuils = new ArrayList<>();
        this.kuils.addAll(kuils);
        this.kuilIndexes = new ArrayList<>();
        this.kuilIndexes.addAll(kuilIndexes);
    }

    @Override
    public String toString() {
        ListNode cur = datarans.head;
        StringBuilder s = new StringBuilder(name + " ");
        while (cur != null) {
            s.append(cur.value.height).append(cur.value.name == null ? "" : cur.value.name).append(", ");
            cur = cur.next;
        }
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
    int value;
    BinaryNode left;
    BinaryNode right;

    public BinaryNode(int value) {
        new BinaryNode(value, null, null);
    }

    public BinaryNode(int value, BinaryNode left, BinaryNode right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }
}

class BST {
    BinaryNode root;

    void insert(int value) throws Exception {
        root = insert(value, root);
    }

    BinaryNode insert(int value, BinaryNode node) throws Exception {
        if (node == null) {
            return new BinaryNode(value);
        } else if (value < node.value) {
            node.left = insert(value, node.left);
        } else if (value > node.value) {
            node.right = insert(value, node.right);
        } else {
            throw new Exception("Duplicate Item in BST");
        }
        return node;
    }

    BinaryNode find(int value, BinaryNode node) {
        if (node == null) {
            return null;
        } else if (value == node.value) {
            return node;
        }
        return (value < node.value)
                ? find(value, node.left)
                : find(value, node.right);
    }

}

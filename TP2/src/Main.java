import jdk.swing.interop.SwingInterOpUtils;

import java.io.*;
import java.util.*;

public class Main {
    private static InputReader in = new InputReader(System.in);
    private static PrintWriter out = new PrintWriter(System.out);
    private static HashMap<String, Pulau> pulaus;
    private static Map.Entry<Pulau, ListNode> posisiRaiden;

    public static void main(String[] args) {
        int n = in.nextInt();
        pulaus = new HashMap<>();

        for (int i = 0; i < n; i++) {
            // input pulau
            String name = in.next();
            int banyakDataran = in.nextInt();
            Pulau pulau = new Pulau(name, banyakDataran);
            int height = in.nextInt();
            pulau.datarans.add(new Dataran(name, height)); // kuil
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
        while (count != e){
            dataranRaiden = dataranRaiden.next;
            count++;
        }

        posisiRaiden = Map.entry(pulauRaiden, dataranRaiden);

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
                case "LANGKAH":
                    langkah();
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
            for (Pulau p: pulaus.values()){
                System.out.println(p);
            }
            System.out.println();
        }

        out.close();
    }

    private static void pisah(){
        String u = in.next();
        Pulau current = null;

        // get pulau with kuil U
        for (Pulau p : pulaus.values()){
            for (String d : p.kuils){
                if (d.equals(u)){
                    current = p;
                    break;
                }
            }
        }

        int count = 1;
        ListNode kuilU = current.datarans.head;
        while (!kuilU.value.name.equals(u)) {
            kuilU = kuilU.next;
            count++;
        }
        kuilU.prev.next = null;
        kuilU.prev = kuilU;

        pulaus.put(u, new Pulau(u, current.banyakDataran - count));
        current.banyakDataran = count;

        out.println(current.banyakDataran + " " + count);
    }

    private static void unifikasi(){
        String u = in.next();
        String v = in.next();
        Pulau pulauU = pulaus.get(u);
        Pulau pulauV = pulaus.get(v);

        pulauU.datarans.tail.next = pulauV.datarans.head;
        pulauV.datarans.head.prev = pulauU.datarans.tail;

        pulauU.kuils.addAll(pulauV.kuils);

        pulauU.banyakDataran += pulauV.banyakDataran;

        pulaus.remove(v);
        out.println(pulauU.banyakDataran);
    }

    private static void rise(){

    }

    private static void quake() {
        String u = in.next();
        int h = in.nextInt();
        int x = in.nextInt();
        int count = 0;

        Pulau p = pulaus.get(u);
        ListNode currentDataran = p.datarans.head;
        while (currentDataran.next != null) {
            if (currentDataran.value.height < h){
                currentDataran.value.height = Math.max(currentDataran.value.height-x, 0);
                count++;
            }
            currentDataran = currentDataran.next;
        }

        out.println(count);
    }

    private static void crumble() {
        ListNode letakRaiden = posisiRaiden.getValue();
        if (letakRaiden.value.name != null) {
            out.println(0);
            return;
        }

        out.println(letakRaiden.value.height);

        if (letakRaiden.next != null){
            letakRaiden = letakRaiden.prev;
            letakRaiden.next = letakRaiden.next.next;
            letakRaiden.next.prev = letakRaiden;
        } else {
            letakRaiden = letakRaiden.prev;
            letakRaiden.next = null;
        }

        posisiRaiden = Map.entry(posisiRaiden.getKey(), letakRaiden);
    }

    private static void stabilize() {

    }

    private static void langkah() {

    }

    private static void tebas() {
        String arah = in.next();
        int s = in.nextInt();
        int count = 0;
        ListNode dataranRaiden = posisiRaiden.getValue();
        ListNode tempDataranRaiden = dataranRaiden;
        int tinggiDataranRaidenAwal = dataranRaiden.value.height;

        if (arah.equals("KIRI")){
            while (count < s && tempDataranRaiden.prev != null) {
                tempDataranRaiden = tempDataranRaiden.prev;
                if (tempDataranRaiden.value.height == tinggiDataranRaidenAwal){
                    count++;
                    dataranRaiden = tempDataranRaiden;
                }
            }
            out.println(dataranRaiden.next.value.height);
        } else {
            while (count < s && tempDataranRaiden.next != null) {
                tempDataranRaiden = tempDataranRaiden.next;
                if (tempDataranRaiden.value.height == tinggiDataranRaidenAwal) {
                    count++;
                    dataranRaiden = tempDataranRaiden;
                }
            }
            out.println(dataranRaiden.prev.value.height);
        }
    }

    private static void teleportasi() {
        String v = in.next();
        Pulau current = null;

        // get pulau with kuil U
        for (Pulau p : pulaus.values()){
            for (String d : p.kuils){
                if (d.equals(v)){
                    current = p;
                    break;
                }
            }
        }

        ListNode dataran = current.datarans.head;
        while (dataran.value.name == null || !dataran.value.name.equals(v)){
            dataran = dataran.next;
        }
        posisiRaiden = Map.entry(current, dataran);
        out.println(dataran.value.height);
    }

    private static void sweeping() {

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

    public Pulau(String name, int banyakDataran) {
        this.name = name;
        this.banyakDataran = banyakDataran;
        datarans = new DoublyLinkedList();
        kuils = new ArrayList<>();
        kuils.add(name);
    }

    @Override
    public String toString(){
        ListNode cur = datarans.head;
        StringBuilder s = new StringBuilder(name + " ");
        while (cur != null) {
            s.append(cur.value.height).append(" ");
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
    // the prev of head will be itself

    ListNode head;
    ListNode tail;

    public DoublyLinkedList() {
        head = null;
        tail = null;
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

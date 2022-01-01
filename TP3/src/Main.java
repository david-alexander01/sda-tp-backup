// beberapa ide pengerjaan dari Cynthia Philander
// dan Graciella Regina

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {
    private static InputReader in;
    private static PrintWriter out;
    private static Graph graph;
    private static UnionFindDisjoinSet ufdSet;
    private static ArrayList<Node>[] pangkats;
    private static int count;

    public static void main(String[] args) {
        in = new InputReader(System.in);
        out = new PrintWriter(System.out);

        int N = in.nextInt();
        int M = in.nextInt();
        int Q = in.nextInt();

        graph = new Graph(N);
        ufdSet = new UnionFindDisjoinSet(N);
        pangkats = new ArrayList[N + 10]; // harusnya N+1 aja cukup
        count = 0;
        for (int i = 1; i <= N + 9; i++) {
            pangkats[i] = new ArrayList<>();
        }
        graph.nodes.add(null); // make one based indexing
        for (int i = 1; i <= N; i++) {
            int pangkat = in.nextInt();
            Node temp = new Node(i, pangkat);
            graph.nodes.add(temp);
            pangkats[pangkat].add(temp);
            temp.idxPangkats = pangkats[pangkat].size() - 1;
        }

        for (int i = 0; i < M; i++) {
            tambah();
        }

        for (int i = 0; i < Q; i++) {
            int command = in.nextInt();
            switch (command) {
                case 1:
                    tambah();
                    break;
                case 2:
                    resign();
                    break;
                case 3:
                    carry();
                    break;
                case 4:
                    boss();
                    break;
                case 5:
                    sebar();
                    break;
                case 6:
                    simulasi();
                    break;
                case 7:
                    networking();
                    break;
                default:
                    System.out.println("wrong input");
            }
        }

        out.flush();
    }

    private static void tambah() {
        int U = in.nextInt();
        int V = in.nextInt();
        tambah(U, V);
    }

    private static void tambah(int U, int V) {
        Node u = graph.nodes.get(U);
        Node v = graph.nodes.get(V);
        int weight = Math.abs(u.pangkat - v.pangkat);

        Edge uv = new Edge(V, v.pangkat, weight);
        Edge vu = new Edge(U, u.pangkat, weight);

        uv.other = vu;
        vu.other = uv;

        u.adjacents.insert(uv);
        v.adjacents.insert(vu);

        if (!u.rentan && !v.rentan) {
            if (u.pangkat == v.pangkat) {
                u.rentan = true;
                v.rentan = true;
                count += 2;
            } else if (u.pangkat > v.pangkat) {
                v.rentan = true;
                count++;
            } else { // v.pangkat > u.pangkat
                u.rentan = true;
                count++;
            }
        }

        if (!u.rentan) {
            if (v.pangkat >= u.pangkat) {
                u.rentan = true;
                count++;
            }
        }

        if (!v.rentan) {
            if (u.pangkat >= v.pangkat) {
                v.rentan = true;
                count++;
            }
        }

        if (!ufdSet.isSameSet(U, V)) {
            if (u.networkHeap == null && v.networkHeap == null) {
                MaxHeap temp = new MaxHeap();
                temp.insert(u);
                temp.insert(v);
                u.networkHeap = temp;
                v.networkHeap = temp;
            } else if (u.networkHeap == null) {
                v.networkHeap.insert(u);
                u.networkHeap = v.networkHeap;
            } else if (v.networkHeap == null) {
                u.networkHeap.insert(v);
                v.networkHeap = u.networkHeap;
            } else {
                if (u.networkHeap.size > v.networkHeap.size) {
                    MaxHeap vHeap = v.networkHeap;
                    while (vHeap.heap.size() > 1) {
                        Node temp = vHeap.heap.remove(vHeap.heap.size() - 1);
                        u.networkHeap.insert(temp);
                        temp.networkHeap = u.networkHeap;
                    }
                } else {
                    MaxHeap uHeap = u.networkHeap;
                    while (uHeap.heap.size() > 1) {
                        Node temp = uHeap.heap.remove(uHeap.heap.size() - 1);
                        v.networkHeap.insert(temp);
                        temp.networkHeap = v.networkHeap;
                    }
                }
            }
        }

        ufdSet.unionSet(U, V);
    }

    private static void resign() {
        int U = in.nextInt();
        resign(graph, U);
    }

    private static void pangkatSwap(ArrayList<Node> arr, int fpos, int spos) {
        Node tmp;
        tmp = arr.get(fpos);
        arr.set(fpos, arr.get(spos));
        arr.set(spos, tmp);

        arr.get(fpos).idxPangkats = fpos;
        arr.get(spos).idxPangkats = spos;
    }

    private static void resign(Graph g, int U) {
        Node u = g.nodes.get(U);
        ArrayList<Node> uList = pangkats[u.pangkat];
        pangkatSwap(uList, u.idxPangkats, uList.size() - 1);
        uList.remove(uList.size() - 1);
        if (u.rentan) {
            count--;
        }

        // neighbor is v
        for (int i = 1; i < u.adjacents.heap.size(); i++) {
            Edge uv = u.adjacents.heap.get(i);
            Edge vu = uv.other;
            Node v = g.nodes.get(uv.to);
            v.adjacents.remove(vu.idxHeap);

            if (v.rentan) {
                int maxFriendPangkat = v.adjacents.heap.size() > 1
                        ? v.adjacents.peek().toPangkat : 0;
                if (v.pangkat > maxFriendPangkat) {
                    v.rentan = false;
                    count--;
                }
            }
        }

        g.nodes.set(U, null);
        g.banyakKaryawan--;
    }

    private static void carry() {
        int U = in.nextInt();
        Node u = graph.nodes.get(U);
        int max = u.adjacents.isEmpty() ?
                0 : u.adjacents.peek().toPangkat;
        out.println(max);
    }

    private static void boss() {
        int U = in.nextInt();
        Node u = graph.nodes.get(U);

        if (u.adjacents.isEmpty()) {
            out.println(0);
            return;
        }

        Node toPrint = u.networkHeap.peek();

        if (toPrint == u) {
            toPrint = u.networkHeap.getMaxChild(1);
        }

        out.println(toPrint.pangkat);

    }

    private static void sebar() {
        int U = in.nextInt();
        int V = in.nextInt();

        Node u = graph.nodes.get(U);
        Node v = graph.nodes.get(V);

        if (u == v) {
            out.println(0);
            return;
        }

        boolean[] flag = new boolean[graph.nodes.size()];
        boolean[] pangkatsFlag = new boolean[graph.nodes.size()];
        int[] dist = new int[graph.nodes.size()];
        ArrayDeque<Node> q = new ArrayDeque<>();
        flag[u.label] = true;
        q.offer(u);

        while (!q.isEmpty()) {
            Node cur = q.poll();
            if (cur == v)
                break;
            for (int i = 1; i < cur.adjacents.heap.size(); i++) {
                Edge e = cur.adjacents.heap.get(i);
                Node w = graph.nodes.get(e.to);
                if (!flag[w.label]) {
                    dist[w.label] = dist[cur.label] + 1;
                    flag[w.label] = true;
                    q.offer(w);
                }
            }

            if (!pangkatsFlag[cur.pangkat]) {
                pangkatsFlag[cur.pangkat] = true;
                for (Node w : pangkats[cur.pangkat]) {
                    if (!flag[w.label]) {
                        dist[w.label] = dist[cur.label] + 1;
                        flag[w.label] = true;
                        q.offer(w);
                    }
                }
            }
        }


        out.println(dist[v.label] - 1);
    }

    private static void simulasi() {
        out.println(graph.banyakKaryawan - count);
    }

    private static void networking() {
//        System.out.println("UFDSET: " + Arrays.toString(ufdSet.parent));
//        ArrayList<Integer> roots = new ArrayList<>();
//        for (int i = 1; i < ufdSet.parent.length; i++) {
//            ;
//            roots.add(ufdSet.findSet(i));
//        }
//        roots = (ArrayList<Integer>) roots.stream().distinct().collect(Collectors.toList());
//        System.out.println("ROOTS: " + roots);
//
//        ArrayList<Node>[] groups = new ArrayList[graph.nodes.size()];
//        for (int i = 0; i < roots.size(); i++) {
//            groups[roots.get(i)] = new ArrayList<>();
//        }
//
//        for (Node n : graph.nodes) {
//            if (n == null)
//                continue;
//            groups[ufdSet.parent[n.label]].add(n);
//        }
//
//        System.out.println("GROUPS");
//        for (int i = 0; i < groups.length; i++) {
//            if (groups[i] == null)
//                continue;
//            for (Node n : groups[i]) {
//                System.out.print(n.label + " ");
//            }
//            System.out.println();
//        }
        out.println(0);
    }

    private static void removeDuplicates(ArrayList<Integer> arr) {
    }

    private static int minDifference(int[] a, int[] b) {
        mergeSort(a);
        mergeSort(b);

        int i = 0;
        int j = 0;

        int result = Integer.MAX_VALUE;

        while (i < a.length && j < b.length) {
            result = Math.min(Math.abs(a[i] - b[j]), result);
            if (a[i] < b[j])
                i++;
            else
                j++;
        }

        return result;
    }

    private static void merge(int[] arr, int left, int mid, int right) {
        int leftSize = mid - left + 1;
        int rightSize = right - mid;

        int[] L = new int[leftSize];
        int[] R = new int[rightSize];

        for (int i = 0; i < leftSize; i++)
            L[i] = arr[left + i];
        for (int i = 0; i < rightSize; i++) {
            R[i] = arr[mid + 1 + i];
        }

        int i = 0;
        int j = 0;
        int k = left;

        while (i < leftSize && j < rightSize) {
            if (L[i] <= R[j]) {
                arr[k++] = L[i++];
            } else {
                arr[k++] = R[j++];
            }
        }

        while (i < leftSize) {
            arr[k++] = L[i++];
        }
        while (j < rightSize) {
            arr[k++] = R[j++];
        }
    }

    private static void mergeSort(int[] arr) {
        mergeSort(arr, 0, arr.length - 1);
    }

    private static void mergeSort(int[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;

            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);

            merge(arr, left, mid, right);
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

    }
}

class Graph {
    ArrayList<Node> nodes;
    int banyakKaryawan;

    public Graph(int banyakKaryawan) {
        this.banyakKaryawan = banyakKaryawan;
        nodes = new ArrayList<>();
    }
}

class Edge implements Comparable<Edge> {
    int to;
    int toPangkat;
    Edge other;
    int idxHeap;
    int weight;

    public Edge(int to, int toPangkat, int weight) {
        this.to = to;
        this.weight = weight;
        this.toPangkat = toPangkat;
    }

    @Override
    public int compareTo(Edge o) {
        return this.toPangkat - o.toPangkat;
    }
}

class Node implements Comparable<Node> {
    int label;
    int pangkat;
    EdgeMaxHeap adjacents;
    MaxHeap networkHeap;
    int idxPangkats;
    boolean rentan;

    public Node(int label, int pangkat) {
        this.label = label;
        this.pangkat = pangkat;
        adjacents = new EdgeMaxHeap();
        rentan = false;
    }

    @Override
    public int compareTo(Node o) {
        return this.pangkat - o.pangkat;
    }
}

// taken from SDA-15.Graph-20191205.pdf
class UnionFindDisjoinSet {
    int[] parent;

    public UnionFindDisjoinSet(int size) {
        parent = new int[size + 1];
        for (int i = 0; i <= size; i++) {
            parent[i] = i;
        }
        parent[0] = -1;
    }

    public int findSet(int i) {
        if (parent[i] == i) {
            return i;
        } else {
            return parent[i] = findSet(parent[i]); // path compression
        }
    }

    public void unionSet(int i, int j) {
        parent[findSet(i)] = findSet(j);
    }

    public boolean isSameSet(int i, int j) {
        return findSet(i) == findSet(j);
    }
}

// taken from https://www.geeksforgeeks.org/max-heap-in-java/ with modifications
class MaxHeap {
    private final int FRONT = 1;
    ArrayList<Node> heap;
    int size;

    public MaxHeap() {
        this.size = 0;
        heap = new ArrayList<>();
        heap.add(new Node(-1, Integer.MAX_VALUE));
    }

    private int parent(int pos) {
        return pos / 2;
    }

    private int leftChild(int pos) {
        return (2 * pos);
    }

    private int rightChild(int pos) {
        return (2 * pos) + 1;
    }

    private boolean isLeaf(int pos) {
        return rightChild(pos) > size && leftChild(pos) > size;
    }

    private void swap(int fpos, int spos) {
        Node tmp;
        tmp = heap.get(fpos);
        heap.set(fpos, heap.get(spos));
        heap.set(spos, tmp);
    }

    void maxHeapify(int pos) {
        if (pos > size || isLeaf(pos))
            return;
        if (heap.get(pos).compareTo(heap.get(leftChild(pos))) < 0
                || rightChild(pos) <= size
                && heap.get(pos).compareTo(heap.get(rightChild(pos))) < 0) {
            if (rightChild(pos) > size
                    || heap.get(leftChild(pos)).compareTo(heap.get(rightChild(pos))) > 0) {
                swap(pos, leftChild(pos));
                maxHeapify(leftChild(pos));
            } else {
                swap(pos, rightChild(pos));
                maxHeapify(rightChild(pos));
            }
        }
    }

    Node getMaxChild(int pos) {
        Node left = null;
        Node right = null;
        if (leftChild(pos) <= size) {
            left = heap.get(leftChild(pos));
        }
        if (rightChild(pos) <= size) {
            right = heap.get(rightChild(pos));
        }

        if (right == null
                || left.pangkat > right.pangkat) {
            return left;
        }
        return right;
    }

    void insert(Node element) {
        heap.add(element);
        size++;
        perlocateUp(size);
    }

    Node remove() {
        Node popped = heap.get(FRONT);
        heap.set(FRONT, heap.get(size));
        heap.remove(size);
        size--;
        maxHeapify(FRONT);

        return popped;
    }

    Node peek() {
        return heap.get(FRONT);
    }

    void perlocateUp(int pos) {
        while (heap.get(pos).compareTo(heap.get(parent(pos))) > 0) {
            swap(pos, parent(pos));
            pos = parent(pos);
        }
    }

    boolean isEmpty() {
        return size == 0;
    }

}

// taken from https://www.geeksforgeeks.org/max-heap-in-java/ with modifications
class EdgeMaxHeap {
    private final int FRONT = 1;
    ArrayList<Edge> heap;
    int size;

    public EdgeMaxHeap() {
        this.size = 0;
        heap = new ArrayList<>();
        heap.add(new Edge(-1, Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    private int parent(int pos) {
        return pos / 2;
    }

    private int leftChild(int pos) {
        return (2 * pos);
    }

    private int rightChild(int pos) {
        return (2 * pos) + 1;
    }

    private boolean isLeaf(int pos) {
        return rightChild(pos) > size && leftChild(pos) > size;
    }

    private void swap(int fpos, int spos) {
        Edge tmp;
        tmp = heap.get(fpos);
        heap.set(fpos, heap.get(spos));
        heap.set(spos, tmp);

        heap.get(fpos).idxHeap = fpos;
        heap.get(spos).idxHeap = spos;
    }


    void maxHeapify(int pos) {
        if (pos > size || isLeaf(pos))
            return;
        if (heap.get(pos).compareTo(heap.get(leftChild(pos))) < 0
                || rightChild(pos) <= size
                && heap.get(pos).compareTo(heap.get(rightChild(pos))) < 0) {
            if (rightChild(pos) > size
                    || heap.get(leftChild(pos)).compareTo(heap.get(rightChild(pos))) > 0) {
                swap(pos, leftChild(pos));
                maxHeapify(leftChild(pos));
            } else {
                swap(pos, rightChild(pos));
                maxHeapify(rightChild(pos));
            }
        }
    }

    void insert(Edge element) {
        heap.add(element);
        size++;
        element.idxHeap = size;
        perlocateUp(size);
    }

    Edge remove() {
        return remove(FRONT);
    }

    Edge remove(int id) {
        Edge popped = heap.get(id);
        swap(id, size);
        heap.remove(size);
        size--;
        maxHeapify(id);

        return popped;
    }

    Edge peek() {
        return heap.get(FRONT);
    }

    void perlocateUp(int pos) {
        while (heap.get(pos).compareTo(heap.get(parent(pos))) > 0) {
            swap(pos, parent(pos));
            pos = parent(pos);
        }
    }

    boolean isEmpty() {
        return size == 0;
    }

}
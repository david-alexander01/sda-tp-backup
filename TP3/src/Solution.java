import java.io.*;
import java.util.*;
import static java.lang.Math.min;
import static java.lang.Math.max;


class Orang implements Comparable<Orang> {

    int ranking, id;
    public Orang(int _ranking, int _id) {
        ranking = _ranking;
        id = _id;
    }

    public int compareTo(Orang other) {
        return ranking - other.ranking;
    }

}

class BossStatistics {
    int bestRanking, bestPengguna, secondBestRanking;
    BossStatistics() {
        bestRanking = 0; bestPengguna = 0; secondBestRanking = 0;
    }

    void update(int id, int ranking) {
        if(ranking >= bestRanking) {
            secondBestRanking = bestRanking;
            bestRanking = ranking;
            bestPengguna = id;
        } else if(ranking > secondBestRanking) {
            secondBestRanking = ranking;
        }
    }
}

class EdgeNetworking implements Comparable<EdgeNetworking> {

    int U, V, W;
    public EdgeNetworking(int _U, int _V, int _W) {
        U = _U; V = _V; W = _W;
    }

    public int compareTo(EdgeNetworking other) {
        return W - other.W;
    }
}

public class Solution {

    static ArrayList<PriorityQueue<Orang>> temans = new ArrayList<>();

    static int ranking[] = new int[100001];
    static boolean isResigned[] = new boolean[100001];
    static int isSafe[] = new int[1000001]; // isSafe -> aman dari SIMULASI
    static int numSafe = 0; // jumlah yang aman

    static boolean isOrangSafe(int U) {
        int bigBoss = handleCarry(U);
        return (ranking[U] > bigBoss);
    }

    // handle the changes to SIMULASI array
    static int handleChange(int U) {

        if(isResigned[U]) return 0;

        int prefSafe = isSafe[U];

        int afterSafe = (isOrangSafe(U)) ? 1 : 0;
        //System.out.println("HUHA " +U + " "+ prefSafe + " " + afterSafe);
        isSafe[U] = afterSafe;
        return afterSafe - prefSafe;

    }

    static void handleTambah(int U, int V) {
        temans.get(U).add(new Orang(ranking[V], V));
        temans.get(V).add(new Orang(ranking[U], U));
        adj.get(U).add(V);
        adj.get(V).add(U);

        numSafe += handleChange(U);
        numSafe += handleChange(V);

    }

    static void handleResign(int U) {
        //System.out.println("handle RESIGN " + U);
        numSafe -= isSafe[U];
        isResigned[U] = true;

        // update neighbours
        for(int neighbour : adj.get(U)) {
            //PriorityQueue<Orang> pq = temans.get(neighbour);
            //while(pq.size() > 0 && pq.peek().id == U) pq.poll();

            //while(temanstemans.get(neighbour).peek().id == U) 
            numSafe += handleChange(neighbour);
        }
    }

    // O(N lg N) total time over all query
    static int handleCarry(int U) {
        PriorityQueue<Orang> pq = temans.get(U);
        while(pq.size() > 0 && isResigned[pq.peek().id]) {
            pq.poll();
        }

        if(pq.size() > 0) {
            return pq.peek().ranking;
        } else {
            return 0;
        }
    }


    // O(N log N) PER QUERY. not optimized biar ga ngebug
    static int handleSimulasi(int N) {
        return numSafe;
    }

    // UFDS code
    static int parent[] = new int[100001];
    static int find(int x) {
        if(x == parent[x]) return x;
        Stack<Integer> s = new Stack<>();
        while(x != parent[x]) {
            s.add(x);
            x = parent[x];
        }
        while(s.size() > 0) {
            int tp = s.pop();
            parent[tp] = x;
        }
        return x;
    }

    static void unite(int x, int y) {
        int u = x; int v = y;
        x = find(x); y = find(y);
        if(x == y) return;
        parent[x] = y;
        find(u); // dummy biar path compressed
        find(v); // dummy biar path compressed
    }

    static BossStatistics[] bs= new BossStatistics[100001];

    // O(lg N) per query
    static int handleBoss(int U) {
        int leader = find(U);
        BossStatistics componentStat = bs[leader];

        if(U != componentStat.bestPengguna) return componentStat.bestRanking;
        else return componentStat.secondBestRanking;
    }

    // O(N lg N)
    static int handleNetworking(int N) {

        // sort ranking values and make edge of sebelahan
        ArrayList<Orang> rankingValues = new ArrayList<>();
        for(int temp=1; temp<=N; temp++) {
            rankingValues.add(new Orang(ranking[temp], temp));
        }

        Collections.sort(rankingValues);
        
        ArrayList<EdgeNetworking> diffEdge = new ArrayList<>();
        for(int temp=0;temp<rankingValues.size() - 1;temp++) {
            int U = rankingValues.get(temp).id, V = rankingValues.get(temp+1).id;
            int W = ranking[V] - ranking[U]; // sebenernya bisa pake rankingValues.get().ranking. Biar dikit aja nulisnya
            diffEdge.add(new EdgeNetworking(U, V, W));
        }

        Collections.sort(diffEdge);

        // kruskal
        int cost = 0;
        for(EdgeNetworking e : diffEdge) {
            if(find(e.U) == find(e.V)) continue;
            cost += e.W;
            unite(e.U, e.V);
        }
        return cost;


    }

    // menyimpan list siapa saja yang punya ranking i
    static ArrayList<ArrayList<Integer>> orangPerRanking = new ArrayList<>();
    static ArrayList<ArrayList<Integer>> adj = new ArrayList<>();

    // O(N + M) per query
    static int handleSebar(int U, int V, int N) {

        int dist[] = new int[N+1];
        for(int temp=1;temp<=N;temp++) dist[temp] = -1;

        boolean hasTelepati[] = new boolean[N+1];
        
        Queue<Integer> Q = new ArrayDeque<>();
        dist[U] = 0;
        Q.add(U);

        while(Q.size() > 0) {
            int node = Q.poll();

            // resign, dont propagate
            if(isResigned[node]) continue;

            //if(node == V) return dist[V]; // pruning bosque

            // do telepati kalau rankingnya blom telepati
            if(!hasTelepati[ranking[node]]) {
                //System.out.println("HUHAHUHA" + orangPerRanking.get(ranking[node]).size());
                for(int orang : orangPerRanking.get(ranking[node])) {
                    if(dist[orang] == -1) {
                        dist[orang] = dist[node] + 1;
                        Q.add(orang);
                    }
                }
                hasTelepati[ranking[node]] = true; 
            }

            // sebar ke teman
            for(int teman : adj.get(node)) {
                if(dist[teman] == -1) {
                    dist[teman] = dist[node] + 1;
                    Q.add(teman);
                }
            }
        }
        
        return dist[V];
    }

    private static InputReader in = new InputReader(System.in);
    private static PrintWriter out = new PrintWriter(System.out);
    public static void main(String[] args) {

        int N = in.nextInt();
        int M = in.nextInt();
        int Q = in.nextInt();

        // init all the globals
        for(int temp=0;temp<=N;temp++) {
            parent[temp] = temp;
            orangPerRanking.add(new ArrayList<>());
            adj.add(new ArrayList<>());
            temans.add(new PriorityQueue<>(Collections.reverseOrder()));
            bs[temp] = new BossStatistics();
        }

        for(int temp=1;temp<=N;temp++) {
            ranking[temp] = in.nextInt();
            orangPerRanking.get(ranking[temp]).add(temp);
        }

        for(int temp=0;temp<M;temp++) {
            int U = in.nextInt();
            int V = in.nextInt();
            unite(U, V);
            temans.get(U).add(new Orang(ranking[V], V));
            temans.get(V).add(new Orang(ranking[U], U));
            adj.get(U).add(V);
            adj.get(V).add(U);
        }

        for(int temp=1;temp<=N;temp++) {
            if (isOrangSafe(temp)) {
                isSafe[temp] = 1;
                numSafe++;
            } else {
                isSafe[temp] = 0;
            }
        }

        //System.out.println("HUHA networking");
        // calculate boss staticstics
        for(int temp=1;temp<=N;temp++) {
            int leader = find(temp);
            bs[leader].update(temp, ranking[temp]);
        }

        for(int temp=1;temp<=Q;temp++) {
            int code = in.nextInt();
            if(code == 1) {
                int U = in.nextInt();
                int V = in.nextInt();
                handleTambah(U, V);
            } else if(code == 2) {
                int U = in.nextInt();
                handleResign(U);
            } else if(code == 3) {
                int U = in.nextInt();
                out.println(handleCarry(U));
            } else if(code == 4) {
                int U = in.nextInt();
                out.println(handleBoss(U));
            } else if(code == 5) {
                int U = in.nextInt();
                int V = in.nextInt();
                int res = handleSebar(U, V, N);
                if(res > 0) res--;
                out.println(res);
            } else if(code == 6) {
                out.println(handleSimulasi(N));
            } else if(code == 7) {
                out.println(handleNetworking(N));
            }
        }

        out.flush();


    }

    
    
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
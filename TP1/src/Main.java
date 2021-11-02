import java.io.*;
import java.util.*;

public class Main {
    private static InputReader in = new InputReader(System.in);
    private static PrintWriter out = new PrintWriter(System.out);
    private static Map<String, Student> students;
    private static PriorityQueue<Student> rankPQueue;
    private static int[][] dp;

    public static void main(String[] args) {
        int batches = in.nextInt();

        for (int z = 0; z < batches; z++) {
            int studentCount = in.nextInt();
            students = new HashMap<>();
            for (int i = 0; i < studentCount; i++) {
                String tempCode = in.next();
                students.put(tempCode, new Student(tempCode, in.nextChar(), i));
            }
            int trainingDays = in.nextInt();

            for (int i = 0; i < trainingDays; i++) {
                int eventCount = in.nextInt();
                int first = -1;
                int last = students.size();
                for (int j = 0; j < eventCount; j++) {
                    String code = in.next();
                    int v = in.nextInt(); // 0 -> code to rank 1, 1 -> code to last
                    process(code, v, first, last);
                    if (v == 0) {
                        first--;
                    } else {
                        last++;
                    }
                }

                rankPQueue = new PriorityQueue<>();
                rankPQueue.addAll(students.values());
                int currentRank = 0;
                while (!rankPQueue.isEmpty()) {
                    Student temp = rankPQueue.poll();
                    if (currentRank < temp.oldRank) {
                        temp.hasImproved = true;
                    }
                    temp.oldRank = currentRank;
                    temp.rank = currentRank;
                    currentRank++;
                    out.print(temp + " ");
                }
                out.println();

                // end day
            }

            String command = in.next();
            rankPQueue = new PriorityQueue<>();
            rankPQueue.addAll(students.values());
            switch (command) {
                case "PANUTAN":
                    panutan();
                    break;
                case "KOMPETITIF":
                    kompetitif();
                    break;
                case "EVALUASI":
                    evaluasi();
                    break;
                case "DUO":
                    duo();
                    break;
                case "DEPLOY":
                    deploy();
                    break;
                default:
                    System.out.println("ERROR");
            }

            // end batch
        }


        out.flush();
    }

    private static void deploy() {
        int k = in.nextInt();
        dp = new int[k + 1][students.size()];
        rankPQueue = new PriorityQueue<>();
        rankPQueue.addAll(students.values());
        Student[] ranks = new Student[students.size()];
        for (int i = 0; i < ranks.length; i++) {
            ranks[i] = rankPQueue.poll();
        }
        makeGroups(k, students.size(), 0, ranks);
        out.println(Arrays.stream(dp[1]).sum());
        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j < dp[i].length; j++) {
                System.out.print(dp[i][j] + " ");
            }
            System.out.println();
        }
    }


    private static void makeGroups(int groupCount, int end, int start, Student[] ranks) {
//        System.out.println(start + " " + end + " " + groupCount);
        if (groupCount == 1) {
            if (ranks[start].spec == ranks[ranks.length - 1].spec) {
                dp[groupCount][start] = (dp[groupCount][start] + 1 + Arrays.stream(dp[groupCount - 1]).sum())
                        % 1000000007;
            }
            return;
        }
        for (int i = start + 1; i < ranks.length - (2 * (groupCount - 1)); i++) {
            if (ranks[start].spec != ranks[i].spec) {
                continue;
            }
            dp[groupCount][start] = (dp[groupCount][start] + 1) % 1000000007;
            makeGroups(groupCount - 1, end, i + 1, ranks);
        }
    }

    private static void duo() {
        ArrayDeque<Student> kangBakso = new ArrayDeque<>();
        ArrayDeque<Student> kangSiomay = new ArrayDeque<>();

        while (!rankPQueue.isEmpty()) {
            Student temp = rankPQueue.poll();
            if (temp.spec == 'B') {
                if (kangSiomay.isEmpty()) {
                    kangBakso.offer(temp);
                } else {
                    out.println(temp + " " + kangSiomay.poll());
                }
            } else {
                if (kangBakso.isEmpty()) {
                    kangSiomay.offer(temp);
                } else {
                    out.println(kangBakso.poll() + " " + temp);
                }
            }
        }
        if (!kangBakso.isEmpty() || !kangSiomay.isEmpty()) {
            out.print("TIDAK DAPAT: ");
            while (!kangBakso.isEmpty()) {
                out.print(kangBakso.poll() + " ");
            }
            while (!kangSiomay.isEmpty()) {
                out.print(kangSiomay.poll() + " ");
            }
            out.println();
        }
    }

    private static void evaluasi() {
        boolean printed = false;
        while (!rankPQueue.isEmpty()) {
            Student temp = rankPQueue.poll();
            if (!temp.hasImproved) {
                out.print(temp + " ");
                printed = true;
            }
        }

        if (!printed) {
            out.println("TIDAK ADA");
        } else {
            out.println();
        }
    }

    private static void kompetitif() {
        int max = 0;
        Student s = null;
        while (!rankPQueue.isEmpty()) {
            Student temp = rankPQueue.poll();
            if (temp.picks > max) {
                max = temp.picks;
                s = temp;
            }
        }
        out.println(s + " " + s.picks);
    }

    private static void panutan() {
        int q = in.nextInt();
        int bakso = 0;
        int siomay = 0;

        int count = 0;
        while (!rankPQueue.isEmpty()) {
            Student temp = rankPQueue.poll();
            count++;
            if (temp.spec == 'B') {
                bakso++;
            } else {
                siomay++;
            }
            if (count == q) {
                break;
            }
        }
        out.println(bakso + " " + siomay);
    }

    // v = 0 -> code to rank 1
    // v = 1 -> code to last
    private static void process(String code, int v, int first, int last) {
        Student s = students.get(code);
        if (v == 0) {
            s.rank = first;
        } else {
            s.rank = last;
        }
        s.picks++;

    }

    private static class Student implements Comparable<Student> {
        String code;
        char spec; // bakso or siomay
        int picks;
        int rank; // can be negative or more than students.size()
        int oldRank; // 0 indexing
        boolean hasImproved;

        public Student(String code, char spec, int rank) {
            this.code = code;
            this.spec = spec;
            this.rank = rank;
            this.oldRank = rank;
            this.picks = 0;
            hasImproved = false;
        }

        @Override
        public String toString() {
            return this.code;
        }

        @Override
        public int compareTo(Student o) {
            return rank - o.rank;
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

        public char nextChar() {
            return next().charAt(0);
        }

    }
}

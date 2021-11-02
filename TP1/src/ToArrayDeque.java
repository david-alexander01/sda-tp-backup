import java.io.*;
import java.util.*;

public class ToArrayDeque {
    private static InputReader in = new InputReader(System.in);
    private static PrintWriter out = new PrintWriter(System.out);
    private static Deque<Student> ranks;
    private static int groupPossibilities;

    public static void main(String[] args) {
        int batches = in.nextInt();

        for (int z = 0; z < batches; z++) {
            int studentCount = in.nextInt();
            ranks = new ArrayDeque<>();
            for (int i = 0; i < studentCount; i++) {
                ranks.add(new Student(in.next(), in.nextChar(), i));
            }
            int trainingDays = in.nextInt();

            for (int i = 0; i < trainingDays; i++) {
                int eventCount = in.nextInt();
                for (int j = 0; j < eventCount; j++) {
                    String code = in.next();
                    int v = in.nextInt(); // 0 -> code to rank 1, 1 -> code to last
                    process(code, v);
                }

                // check if any students has improved (for EVALUASI)
                for (int j = 0; j < ranks.size(); j++) {
                    Student s = ranks.poll();
                    if (j < s.rank) {
                        s.hasImproved = true;
                    }
                    ranks.add(s);
                    s.rank = j;
                }

                // print updated ranks
                out.println(Arrays.toString(ranks.toArray()).replace("[", "")
                        .replace("]", "")
                        .replace(",", ""));
                // end day
            }

            String command = in.next();
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
        groupPossibilities = 0;
        int k = in.nextInt();
//        makeGroups(0, ranks.size(), k);
        out.println(groupPossibilities);
    }


//    private static void makeGroups(int start, int end, int groupCount) {
//        if (groupCount == 1) {
//            if (ranks.get(start).spec == ranks.get(end - 1).spec) {
//                groupPossibilities++;
//            }
//            return;
//        }
//        for (int i = start + 1; i < end - (2 * (groupCount - 1)); i++) {
//            if (ranks.get(start).spec != ranks.get(i).spec) {
//                continue;
//            }
//            makeGroups(i + 1, end, groupCount - 1);
//        }
//    }

    private static void duo() {
        Deque<Student> kangBakso = new ArrayDeque<>();
        Deque<Student> kangSiomay = new ArrayDeque<>();

        for (int i = 0; i < ranks.size(); i++) {
            Student temp = ranks.poll();
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
        for (Student temp : ranks) {
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
        for (Student temp : ranks) {
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
        for (int i = 0; i < q; i++) {
            if (ranks.poll().spec == 'B') {
                bakso++;
            } else {
                siomay++;
            }
        }
        out.println(bakso + " " + siomay);
    }

    // v = 0 -> code to rank 1
    // v = 1 -> code to last
    private static void process(String code, int v) {
        Stack<Student> holder = new Stack<>();
        Student s = null;

        while (true) {
            s = ranks.poll();
            if (s.code.equals(code)){
                break;
            }
            holder.push(s);
        }

        s.picks++;

        // remake queue
        while (!holder.isEmpty()){
            ranks.offerFirst(holder.pop());
        }

        if (v == 0) {
            ranks.offerFirst(s);
        } else {
            ranks.offer(s);
        }

    }


    private static class Student {
        String code;
        char spec; // bakso or siomay
        int picks;
        int rank; // 0 indexing
        boolean hasImproved;

        public Student(String code, char spec, int rank) {
            this.code = code;
            this.spec = spec;
            this.rank = rank;
            this.picks = 0;
            hasImproved = false;
        }

        @Override
        public String toString() {
            return this.code;
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

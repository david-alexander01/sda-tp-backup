import java.util.ArrayList;

public class Testing {
    public static void main(String[] args) {
        long start = System.nanoTime();

        ArrayList<Integer> a = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            a.add(i);
        }
        while (!a.isEmpty()) {
            a.remove(0);
        }

        long end = System.nanoTime();
        System.out.println("TIME: " + (end-start)/Math.pow(10, 9));
    }

    private static void modify(ArrayList<Integer> a){
        a.add(100);
    }
}

import java.util.*;

public class Testing {
    public static void main(String[] args) throws InterruptedException {
        PriorityQueue<Integer> p = new PriorityQueue<>();
        p.add(5);
        p.add(-1);
        p.add(1);
        p.add(4);
        // what the fuck
        for (int i: p){
            System.out.println(i);
        }
        while (!p.isEmpty()){
            System.out.println(p.poll());
        }
        System.out.println(deploy());
    }

    private static int deploy() {
        return (int) (Math.random() * 1000000007);
    }
}

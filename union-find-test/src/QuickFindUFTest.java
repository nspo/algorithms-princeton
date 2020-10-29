public class QuickFindUFTest {
    public static void main(String[] args) {
        QuickFindUF qf = new QuickFindUF(10);
        qf.union(0, 5);
        qf.union(5, 9);
        qf.union(3, 7);
        qf.union(3, 5);
        qf.union(9, 0);
        System.out.println(qf.connected(0, 3));
    }
}

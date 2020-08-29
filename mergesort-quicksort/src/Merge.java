public class Merge {
    // Mergesort

    // whether a is less than b
    private static <T extends Comparable<T>> boolean less(T a, T b) {
        return a.compareTo(b) < 0;
    }

    private static <T extends Comparable<T>> boolean isSorted(T[] a, int lo, int hi) {
        assert (lo <= hi);
        for (int i = lo; i < hi; ++i) {
            if (less(a[i + 1], a[i])) {
                return false;
            }
        }
        return true;
    }

    private static <T extends Comparable<T>> void merge(T[] a, T[] aux, int lo, int mid, int hi) {
        assert isSorted(a, lo, mid);
        assert isSorted(a, mid + 1, hi);

        for (int k = lo; k <= hi; ++k) {
            aux[k] = a[k];
        }

        int i = lo;
        int j = mid + 1;
        for (int k = lo; k <= hi; ++k) {
            if (i > mid) a[k] = aux[j++];
            else if (j > hi) a[k] = aux[i++];
            else if (less(aux[j], aux[i])) a[k] = aux[j++];
            else a[k] = aux[i++];
        }

        assert isSorted(a, lo, hi);
    }

    private static <T> void print_arr(T[] a, int lo, int hi) {
        System.out.printf("Printing array from index %d..%d: ", lo, hi);
        for (int i = lo; i <= hi; ++i) {
            System.out.print(a[i]);
            System.out.print(" ");
        }
        System.out.print("\n");
    }

    private static <T extends Comparable<T>> void sort(T[] a, T[] aux, int lo, int hi) {
        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid);
        sort(a, aux, mid + 1, hi);
        merge(a, aux, lo, mid, hi);
    }


    public static <T extends Comparable<T>> void sort(T[] a) {
        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Comparable[a.length];
        sort(a, aux, 0, a.length - 1);
    }

    public static void main(String[] args) {
        Integer[] a = {0, 1, 7, 8, 4, 3, 2, 1, 8, 5, 6, 0};
        print_arr(a, 0, a.length - 1);
        sort(a);
        print_arr(a, 0, a.length - 1);
    }
}

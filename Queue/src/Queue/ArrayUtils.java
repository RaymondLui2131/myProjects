package cse214hw1;

public class ArrayUtils {
    public static void rotate(int[] a, int r) {
        int[] hold = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            hold[(i + r) % a.length] = a[i];
        }
        for (int j = 0; j < a.length; j++) {
            a[j] = hold[j];
        }
    }

    public static void rotate(char[] a, int r) {
        char[] hold = new char[a.length];
        for (int i = 0; i < a.length; i++) {
            hold[(i + r) % a.length] = a[i];
        }
        for (int j = 0; j < a.length; j++) {
            a[j] = hold[j];
        }
    }

    public static int[] merge(int[] a, int[] b) {
        int[] c = new int[a.length + b.length];
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i];
            count++;
        }
        for (int j = 0; j < b.length; j++) {
            c[j + count] = b[j];
        }
        return c;
    }
}


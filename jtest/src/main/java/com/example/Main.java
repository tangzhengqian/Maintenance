package com.example;

public class Main {
    public static void main(String[] args) {
        int p = 1;
        int q=p++;
//        test(p++);
        System.out.println(p);
        System.out.println(q);
//        test(++p);
//        System.out.println(p);
    }

    private static void test(int p) {
        System.out.println("--" + p);
    }
}

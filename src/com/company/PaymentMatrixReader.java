package com.company;

import java.util.Scanner;

public class PaymentMatrixReader {
    public static float[][] read() {
        int m, n; //size mxn
        Scanner in = new Scanner(System.in);
        System.out.println("Введите m");
        m = in.nextInt();
        System.out.println("Введите n");
        n = in.nextInt();
        System.out.println("Введите платёжную матрицу");
        float[][] result = new float[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = in.nextFloat();
            }
        }
        in.close();
        return result;
    }
}

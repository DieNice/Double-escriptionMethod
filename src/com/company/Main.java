package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;

public class Main {


    public static void sortmatrix(float M[][]) {
        int numcol = M[0].length;
        int numrow = M.length;
        float min[] = new float[numrow];
        int coloms[] = new int[numrow];
        //founding string min
        for (int i = 0; i < numrow; i++) {
            min[i] = M[i][0];
            for (int j = 1; j < numcol; j++)
                if (M[i][j] < min[i]) {
                    min[i] = M[i][j];
                    coloms[i] = j;
                }
        }
        for (int i = 0; i < numcol; i++) {

        }
        //bubble sort
        for (int i = numrow - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (min[j] < min[j + 1]) {
                    float tmp = min[j];
                    int tmpc = coloms[j];
                    min[j] = min[j + 1];
                    coloms[j] = coloms[j + 1];
                    min[j + 1] = tmp;
                    coloms[j + 1] = tmpc;
                }
            }
        }
        float result[][] = new float[numrow][numcol];
        for (int i = 0; i < numrow; i++) {
            for (int j = 0; j < numrow; j++) {
                result[j][i] = M[j][coloms[i]];
            }
        }
    }

    public static void printmatrix(float[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    //проверка двух точек на пересечение m-1 гиперплоскостей
    public static boolean is_intersection_mm1(CornerPoint p1, CornerPoint p2) {
        boolean flag = false;
        int m = p1.boundary_hyperplanes.length;
        int n = p1.marginal_hyperplanes.length;
        int count = 0;
        for (int i = 0; i < m; i++) if (p1.boundary_hyperplanes[i] && p2.boundary_hyperplanes[i]) count++;
        for (int i = 0; i < n; i++) if (p1.marginal_hyperplanes[i] && p2.marginal_hyperplanes[i]) count++;
        if (count >= m - 1) flag = true;
        return flag;
    }

    public static void main(String[] args) {
        //Платёжная матрица
        float[][] payment_matrix = PaymentMatrixReader.read();
        int m = payment_matrix.length; //size mxn
        int n = payment_matrix[0].length; //size mxn
        //Все помеченные угловые точки Pj плоскости Cj
        ArrayList<CornerPoint> P_i = new ArrayList<CornerPoint>(m);
        ArrayList<Boolean> P_i_bool = new ArrayList<Boolean>(m);
        for (int i = 0; i < m; i++) {
            P_i.add(new CornerPoint(m, payment_matrix, i, 0));
            P_i_bool.add(true);
        }

        //множество индексов критических точек
        TreeSet<Integer> Kj = new TreeSet<Integer>();
        //Начало цикла
        for (int j = 0; j < n - 1; j++) {
            //Столбец Cj
            float Cj[] = new float[m];
            for (int l = 0; l < m; l++) Cj[l] = payment_matrix[l][j];
            //Столбец Сj+1 с -1 элементом
            float Cj_p1[] = new float[m + 1];
            for (int l = 0; l < m; l++) Cj_p1[l] = payment_matrix[l][j + 1];
            Cj_p1[m] = -1;
            for (int l = 0; l < P_i_bool.size(); l++) if (P_i_bool.get(l)) Kj.add(l);
            //расстояния di
            int num_cp = P_i.size();//Число критических точек
            //Вычисляем значения di для Kj
            Float d_i[] = new Float[num_cp];
            for (int l = 0; l < num_cp; l++) d_i[l] = Float.valueOf(0);
            for (int l = 0; l < num_cp; l++) {
                if (Kj.contains(l)) {
                    for (int s = 0; s < m; s++) d_i[l] = d_i[l] + P_i.get(l).barycentric_coordinates[s] * Cj_p1[s];
                    d_i[l] = d_i[l] + P_i.get(l).affine_coordinate * Cj_p1[m];
                }
            }
            //Выделить d_j_a и d_j_b
            ArrayList<Integer> d_j_a = new ArrayList<>();
            ArrayList<Integer> d_j_b = new ArrayList<>();
            for (int l = 0; l < num_cp; l++) {
                if (P_i_bool.get(l)) {
                    if (d_i[l] <= 0) {
                        d_j_a.add(l);
                    } else {
                        d_j_b.add(l);
                        Kj.add(l);//добавили новый индекс к Kj из 6
                    }
                }
            }
            //Проверить имеют ли точки p_j_a и p_j_b (m-1) одинаковых плоскостей
            for (int l = 0; l < d_j_b.size(); l++) {
                for (int s = 0; s < d_j_a.size(); s++) {
                    int d1 = d_j_b.get(l);
                    int d2 = d_j_a.get(s);
                    if (is_intersection_mm1(P_i.get(d1), P_i.get(d2))) {
                        CornerPoint newpoint = new CornerPoint(m, j, d_i[d1], d_i[d2], P_i.get(d1), P_i.get(d2));
                        P_i.add(newpoint);
                        P_i_bool.add(true);
                        Kj.add(P_i.size());//добавили новый индекс к анализу из 8
                    }
                }
            }
            //исключили i_a из множества индексов Kj
            for (int l = 0; l < d_j_a.size(); l++) {
                Kj.remove(d_j_a.get(l));
                P_i_bool.set(d_j_a.get(l), false);
            }

            //добавили новые угловые точки с условием ai,j+1 < min(ai1,ai2,...aij).
            for (int i = 0; i < m; i++) {
                float min_p_str = payment_matrix[i][0];
                for (int s = 0; s < j; s++) {
                    if (payment_matrix[i][s] < min_p_str) min_p_str = payment_matrix[i][s];
                }
                if (payment_matrix[i][j + 1] < min_p_str) {
                    P_i.add(new CornerPoint(m, payment_matrix, i, j + 1));
                    P_i_bool.add(true);
                }
            }
        }
        //Вывод ответа
        int answer = 0;
        while (!P_i_bool.get(answer)) answer++;
        for (int i = 0; i < P_i.size(); i++)
            if ((P_i.get(i).affine_coordinate > P_i.get(answer).affine_coordinate) && P_i_bool.get(i)) answer = i;
        System.out.print("Цена игры=" + P_i.get(answer).affine_coordinate + "\n");
        System.out.print("Стратегия=[");
        for (int i = 0; i < P_i.get(answer).barycentric_coordinates.length; i++)
            System.out.print(P_i.get(answer).barycentric_coordinates[i] + ";");
        System.out.print("]");
    }


}

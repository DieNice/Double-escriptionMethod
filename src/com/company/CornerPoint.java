package com.company;

public class CornerPoint {
    public float[] barycentric_coordinates;
    public float affine_coordinate;
    public boolean[] marginal_hyperplanes;
    public boolean[] boundary_hyperplanes;

    public CornerPoint(int numplanes, float[][] matrix, int j, int k) {
        int m = matrix.length;
        int n = matrix[0].length;
        affine_coordinate = matrix[j][k];
        barycentric_coordinates = new float[m];
        marginal_hyperplanes = new boolean[n];
        for (int i = 0; i < n; i++) marginal_hyperplanes[i] = false;
        marginal_hyperplanes[k] = true;

        boundary_hyperplanes = new boolean[m];
        for (int i = 0; i < m; i++) {
            if (i == j) {
                barycentric_coordinates[i] = 1;
                boundary_hyperplanes[i] = false;
            } else {
                barycentric_coordinates[i] = 0;
                boundary_hyperplanes[i] = true;
            }
        }
    }

    public CornerPoint(int numplanes, int j, float d1, float d2, CornerPoint p1, CornerPoint p2) {
        barycentric_coordinates = new float[numplanes];
        affine_coordinate = (-d2 * p1.affine_coordinate + d1 * p2.affine_coordinate) / (d1 - d2);
        for (int i = 0; i < numplanes; i++) {
            barycentric_coordinates[i] = (-d2 * p1.barycentric_coordinates[i] + d1 * p2.barycentric_coordinates[i]) / (d1 - d2);
        }
        marginal_hyperplanes = new boolean[p1.marginal_hyperplanes.length];
        boundary_hyperplanes = new boolean[p1.boundary_hyperplanes.length];
        for (int i = 0; i < marginal_hyperplanes.length; i++) {
            if (p1.marginal_hyperplanes[i] && p2.marginal_hyperplanes[i]) marginal_hyperplanes[i] = true;
        }
        marginal_hyperplanes[j + 1] = true;
        for (int i = 0; i < boundary_hyperplanes.length; i++) {
            if (p1.boundary_hyperplanes[i] && p2.boundary_hyperplanes[i]) boundary_hyperplanes[i] = true;
        }
    }
}

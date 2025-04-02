package org.example.distributions;

import java.util.Random;

public class Uniform {

    public static double[] generate(double a, double b, int n) {
        if (n > 50000) throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");

        Random random = new Random();
        double[] valores = new double[n];

        for (int i = 0; i < n; i++) {
            valores[i] = a + (b - a) * random.nextDouble();
        }
        return valores;
    }
}

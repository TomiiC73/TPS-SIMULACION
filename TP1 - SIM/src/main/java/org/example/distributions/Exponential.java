package org.example.distributions;

import org.apache.commons.math3.distribution.ExponentialDistribution;

public class Exponential {

    public static double[] generate(double lambda, int n) {

        if (n > 50000) throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");

        ExponentialDistribution distribution = new ExponentialDistribution(1 / lambda);
        double[] valores = new double[n];
        for (int i = 0; i < n; i++) {
            valores[i] = distribution.sample();
        }
        return valores;
    }
}

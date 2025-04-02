package org.example.distributions;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Normal {

    public static double[] generate(double media, double desviacion, int n) {
        if (n > 50000) throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");

        NormalDistribution distribution = new NormalDistribution(media, desviacion);
        double[] valores = new double[n];
        for (int i = 0; i < n; i++) {
            valores[i] = distribution.sample();
        }
        return valores;
    }
}

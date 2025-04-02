package org.example.distributions;

import org.apache.commons.math3.distribution.PoissonDistribution;

public class Poisson {

    public static double[] generate(double lambda, int n) {

        if (n > 50000) throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");

        PoissonDistribution distribution = new PoissonDistribution(lambda);
        double[] valores = new double[n];

        // Generar los valores de Poisson y normalizarlos entre 0 y 1
        double maxPoissonValue = Double.MIN_VALUE;  // Para encontrar el valor máximo de la distribución Poisson

        // Paso 1: Generar valores Poisson y encontrar el valor máximo
        for (int i = 0; i < n; i++) {
            valores[i] = distribution.sample();
            if (valores[i] > maxPoissonValue) {
                maxPoissonValue = valores[i];  // Encontrar el valor máximo
            }
        }

        // Paso 2: Normalizar los valores Poisson para que estén entre 0 y 1
        for (int i = 0; i < n; i++) {
            valores[i] /= maxPoissonValue;  // Dividir por el valor máximo para normalizar en el rango [0, 1)
        }

        return valores;
    }
}

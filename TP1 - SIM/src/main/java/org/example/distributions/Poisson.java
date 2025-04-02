package org.example.distributions;

import java.util.Random;

public class Poisson {

    public Poisson() {
    }

    public static double[] generate(double lambda, int n) {
        if (n > 50000) {
            throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");
        }

        if (lambda <= 0) {
            throw new IllegalArgumentException("El valor de lambda debe ser mayor a 0.");
        }

        double[] valores = new double[n];
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            valores[i] = generarPoisson(lambda, random);
        }

        return valores;
    }

    // Algoritmo de Knuth para generar un valor Poisson
    private static int generarPoisson(double lambda, Random random) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);

        return k - 1;  // Se resta 1 porque k se incrementa antes de chequear la condición
    }
}

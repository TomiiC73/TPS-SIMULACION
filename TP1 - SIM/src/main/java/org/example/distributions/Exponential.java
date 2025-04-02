package org.example.distributions;

import java.util.Random;

public class Exponential {

    public Exponential() {
    }

    public static double[] generate(double lambda, int n) {

        if (n > 50000) {
            throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");
        }

        if (lambda <= 0) {
            throw new IllegalArgumentException("El valor de lambda debe ser mayor a 0.");
        }

        Random random = new Random();
        double[] valores = new double[n];

        for (int i = 0; i < n; i++) {
            double u = random.nextDouble(); // Genera RND entre 0 y 1
            double valor = - (1.0 / lambda) * Math.log(1 - u);
            valores[i] = valor;
        }

        return valores;
    }
}

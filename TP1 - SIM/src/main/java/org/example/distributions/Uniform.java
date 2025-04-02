package org.example.distributions;

import java.util.Random;

public class Uniform {

    public Uniform() {
    }

    public static double[] generate(int n) {
        if (n > 50000) throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");

        Random random = new Random();
        double[] valores = new double[n];

        for (int i = 0; i < n; i++) {
            valores[i] = random.nextDouble();  // Genera un número aleatorio entre 0 y 1
        }
        return valores;
    }
}

package org.example.distributions;

import java.util.Random;

public class Uniform {

    public Uniform() {
    }

    public static double[] generate(double A, double B, int n) {
        if (n > 50000) {
            throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");
        }

        if (A <= 0) {
            throw new IllegalArgumentException("El valor de A debe ser mayor a 0.");
        }

        if (B <= A) {
            throw new IllegalArgumentException("El valor de B debe ser mayor que A.");
        }

        Random random = new Random();
        double[] valores = new double[n];

        for (int i = 0; i < n; i++) {
            double u = random.nextDouble(); // RND ∈ [0, 1)
            valores[i] = A + (B - A) * u;
        }

        return valores;
    }
}

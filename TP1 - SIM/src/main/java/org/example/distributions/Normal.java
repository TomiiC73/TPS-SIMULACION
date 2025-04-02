package org.example.distributions;

import java.util.Random;

public class Normal {

    public Normal() {
    }

    public static double[] generate(double media, double desviacion, int n) {
        if (n > 50000) {
            throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");
        }

        if (desviacion <= 0) {
            throw new IllegalArgumentException("La desviación estándar debe ser mayor a 0.");
        }

        Random random = new Random();
        double[] valores = new double[n];

        int i = 0;
        while (i < n - 1) {
            double u1 = random.nextDouble();
            double u2 = random.nextDouble();

            double z0 = Math.sqrt(-2.0 * Math.log(1 - u1)) * Math.cos(2 * Math.PI * u2);
            double z1 = Math.sqrt(-2.0 * Math.log(1 - u1)) * Math.sin(2 * Math.PI * u2);

            valores[i] = z0 * desviacion + media;
            valores[i + 1] = z1 * desviacion + media;
            i += 2;
        }

        // Si n es impar, generamos un último valor usando solo N1
        if (n % 2 != 0) {
            double u1 = random.nextDouble();
            double u2 = random.nextDouble();

            double z0 = Math.sqrt(-2.0 * Math.log(1 - u1)) * Math.cos(2 * Math.PI * u2);
            valores[n - 1] = z0 * desviacion + media;
        }

        return valores;
    }
}

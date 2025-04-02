package org.example.distributions;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Exponential {

    public Exponential() {
    }

    public static double[] generate(double lambda, int n) {

        if (n > 50000) throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");

        // Validación de lambda para asegurar que se generen valores entre 0 y 1
        if (lambda <= 0 || lambda > 1) {
            throw new IllegalArgumentException("El valor de lambda debe estar en el rango (0, 1] para que los valores estén entre 0 y 1.");
        }

        ExponentialDistribution distribution = new ExponentialDistribution(1 / lambda);
        double[] valores = new double[n];

        // Generar los valores de la distribución exponencial
        for (int i = 0; i < n; i++) {
            double valor = distribution.sample();

            // Validar que los valores generados estén en el rango [0, 1)
            if (valor >= 1) {
                valores[i] = 1 - Double.MIN_VALUE;  // Asignar un valor muy cercano a 1, pero no 1
            } else {
                valores[i] = valor;
            }
        }
        return valores;
    }
}

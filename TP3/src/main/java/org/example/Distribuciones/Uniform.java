package org.example.Distribuciones;

import java.util.Random;

public class Uniform {

    // Metodo para generar n valores aleatorios con distribucion uniforme
    public static double[] generate(double A, double B, int n) {

        // Si el numero de valores a generar es mayor a 50000, lanza una excepcion
        if (n > 50000) {
            throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");
        }

        // Si el valor de A es menor o igual a 0, lanza una excepcion
        if (A <= 0) {
            throw new IllegalArgumentException("El valor de A debe ser mayor a 0.");
        }

        // Si el valor de B es menor o igual a A, lanza una excepcion
        if (B <= A) {
            throw new IllegalArgumentException("El valor de B debe ser mayor que A.");
        }

        Random random = new Random();  // Crea un objeto Random para generar numeros aleatorios
        double[] valores = new double[n];  // Crea un arreglo para almacenar los valores generados

        // Bucle para generar n valores con distribucion uniforme
        for (int i = 0; i < n; i++) {
            double u = random.nextDouble();  // Genera un numero aleatorio entre 0 y 1
            valores[i] = A + (B - A) * u;  // Ajusta el valor aleatorio para que caiga en el rango [A, B)
        }

        return valores;  // Devuelve el arreglo con los valores generados
    }
}

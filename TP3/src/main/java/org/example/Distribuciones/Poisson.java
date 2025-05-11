package org.example.Distribuciones;

import java.util.Random;

public class Poisson {

    // Metodo para generar n valores aleatorios con distribucion Poisson
    public static double[] generate(double lambda, int n) {

        // Si el numero de valores a generar es mayor a 50000, lanza una excepcion
        if (n > 50000) {
            throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");
        }

        // Si el valor de lambda es menor o igual a 0, lanza una excepcion
        if (lambda <= 0) {
            throw new IllegalArgumentException("El valor de lambda debe ser mayor a 0.");
        }

        double[] valores = new double[n];  // Crea un arreglo para almacenar los valores generados
        Random random = new Random();  // Crea un objeto Random para generar numeros aleatorios

        // Bucle para generar n valores usando el algoritmo de Poisson
        for (int i = 0; i < n; i++) {
            valores[i] = generarPoisson(lambda, random);  // Genera un valor Poisson y lo almacena en el arreglo
        }

        return valores;  // Devuelve el arreglo con los valores generados
    }

    // Algoritmo para generar un valor Poisson
    private static int generarPoisson(double lambda, Random random) {
        double L = Math.exp(-lambda);  // Calcula L = e^(-lambda)
        double p = 1.0;  // Inicializa p en 1.0
        int k = 0;  // Inicializa k, el contador de eventos

        // Bucle para generar valores hasta que p sea menor que L
        do {
            k++;  // Incrementa el contador de eventos
            p *= random.nextDouble();  // Multiplica p por un valor aleatorio entre 0 y 1
        } while (p > L);  // Sigue hasta que p sea menor que L

        return k - 1;  // Se resta 1 porque k se incrementa antes de chequear la condicion
    }
}

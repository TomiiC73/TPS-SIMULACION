package org.example.Distribuciones;

import java.util.Random;

public class Exponential {

    // Metodo para generar n valores aleatorios con distribucion exponencial
    public static double[] generate(double media, int n, double RND) {

        // Si el numero de valores a generar es mayor a 50000, lanza una excepcion
        if (n > 50000) {
            throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");
        }

        // Si el valor de lambda es menor o igual a 0, lanza una excepcion
        if (media <= 0) {
            throw new IllegalArgumentException("El valor de lambda debe ser mayor a 0.");
        }

        //Random random = new Random();  // Crea un objeto Random para generar numeros aleatorios
        double[] valores = new double[n];  // Crea un arreglo de tipo double para almacenar los valores generados

        // Bucle para generar n valores
        for (int i = 0; i < n; i++) {
            double valor = - (media) * Math.log(1 - RND);  // Calcula el valor de la distribucion exponencial usando la formula
            valores[i] = valor;  // Asigna el valor calculado al arreglo
        }

        return valores;  // Devuelve el arreglo con los valores generados
    }
}

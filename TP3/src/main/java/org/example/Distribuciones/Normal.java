package org.example.Distribuciones;

import java.util.Random;

public class Normal {

    // Metodo para generar n valores aleatorios con distribución normal
    public static double[] generate(double media, double desviacion, int n, double RND1, double RND2) {

        // Si el número de valores a generar es mayor a 50000, lanza una excepción
        if (n > 50000) {
            throw new IllegalArgumentException("La cantidad de valores no puede ser mayor a 50000");
        }

        // Si la desviación estándar es menor o igual a 0, lanza una excepción
        if (desviacion <= 0) {
            throw new IllegalArgumentException("La desviación estándar debe ser mayor a 0.");
        }

        //Random random = new Random();  // Crea un objeto Random para generar numeros aleatorios
        double[] valores = new double[n];  // Crea un arreglo para almacenar los valores generados

        int i = 0;  // Inicializa el indice para el ciclo while
        // Bucle para generar n valores con el metodo (produce dos valores por iteracion)
        while (i < n - 1) {
            // Fórmula para obtener dos valores de una distribucion normal estandar (z0, z1)
            double z0 = Math.sqrt(-2.0 * Math.log(1 - RND1)) * Math.cos(2 * Math.PI * RND2);
            double z1 = Math.sqrt(-2.0 * Math.log(1 - RND1)) * Math.sin(2 * Math.PI * RND2);

            // Ajusta z0 y z1 para la distribución normal con media y desviacion estandar especificas
            valores[i] = Math.max(0,z0 * desviacion + media);
            valores[i + 1] = z1 * desviacion + media;
            i += 2;  // Incrementa el indice en 2 porque se generaron dos valores
        }

        // Si n es impar, genera un ultimo valor usando solo z0 (el valor restante)
        if (n % 2 != 0) {
            // Calcula z0 para obtener el ultimo valor de la distribucion normal
            double z0 = Math.sqrt(-2.0 * Math.log(1 - RND1)) * Math.cos(2 * Math.PI * RND2);
            valores[n - 1] = z0 * desviacion + media;  // Ajusta z0 a la media y desviacion estándar
        }

        return valores;  // Devuelve el arreglo con los valores generados
    }
}

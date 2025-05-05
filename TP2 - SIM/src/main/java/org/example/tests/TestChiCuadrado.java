package org.example.tests;

import java.util.Arrays;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

public class TestChiCuadrado {

    // Metodo para calcular el valor critico de una distribucion Chi-Cuadrado
    public static double valorCritico(int gradosLibertad, double nivelDeAceptacion) {
        ChiSquaredDistribution chid = new ChiSquaredDistribution(gradosLibertad);  // Crea una distribucion Chi-Cuadrado con los grados de libertad especificados
        return chid.inverseCumulativeProbability(nivelDeAceptacion);  // Devuelve el valor critico para el nivel de aceptacion proporcionado
    }

    // Enum que define los tipos de distribuciones soportadas
    public enum Distribucion {
        UNIFORME, NORMAL, EXPONENCIAL, POISSON  // Las distribuciones soportadas son uniforme, normal, exponencial y poisson
    }

    // Metodo para calcular el estadistico Chi-Cuadrado para un conjunto de datos
    public static double calcular(double[] datos, int intervalos, Distribucion tipo, double... params) {
        double[] observadas = new double[intervalos];  // Arreglo para almacenar las frecuencias observadas
        double[] esperadas = new double[intervalos];  // Arreglo para almacenar las frecuencias esperadas

        // Encuentra el valor minimo y maximo de los datos
        double min = Arrays.stream(datos).min().orElse(0);  // Encuentra el minimo de los datos
        double max = Arrays.stream(datos).max().orElse(0);  // Encuentra el maximo de los datos
        double ancho = (max - min) / intervalos;  // Calcula el ancho de cada intervalo

        // Contar frecuencias observadas
        for (double valor : datos) {
            int i = (int) ((valor - min) / ancho);  // Determina en que intervalo cae el valor
            if (i == intervalos)  // Para el borde superior, asegurarse de que no se salga del rango
                i--;
            observadas[i]++;  // Incrementa la frecuencia observada para ese intervalo
        }

        // Calcular las frecuencias esperadas segun el tipo de distribucion
        switch (tipo) {
            case UNIFORME:
                Arrays.fill(esperadas, (double) datos.length / intervalos);  // En una distribucion uniforme, todas las frecuencias esperadas son iguales
                break;

            case EXPONENCIAL:
                double lambda = params[0];  // Obtiene el parametro lambda de la distribucion exponencial
                for (int i = 0; i < intervalos; i++) {
                    double li = min + i * ancho;  // Limite inferior del intervalo
                    double ls = li + ancho;  // Limite superior del intervalo
                    double prob = Math.exp(-lambda * li) - Math.exp(-lambda * ls);  // Probabilidad de caer en el intervalo [li, ls]
                    esperadas[i] = prob * datos.length;  // Calcula la frecuencia esperada para ese intervalo
                }
                break;

            case NORMAL:
                double mu = params[0];  // Media de la distribucion normal
                double sigma = params[1];  // Desviacion estandar de la distribucion normal
                for (int i = 0; i < intervalos; i++) {
                    double li = min + i * ancho;  // Limite inferior del intervalo
                    double ls = li + ancho;  // Limite superior del intervalo
                    double prob = normalCDF(ls, mu, sigma) - normalCDF(li, mu, sigma);  // Probabilidad de caer en el intervalo [li, ls] segun la CDF normal
                    esperadas[i] = prob * datos.length;  // Calcula la frecuencia esperada para ese intervalo
                }
                break;

            case POISSON:
                lambda = params[0];  // Obtiene el parametro lambda de la distribucion Poisson
                int maxK = (int) Arrays.stream(datos).max().orElse(0);  // Encuentra el valor maximo de los datos
                int[] observadasPoisson = new int[maxK + 1];  // Arreglo para contar las frecuencias observadas en la distribucion Poisson
                for (double d : datos) {
                    int k = (int) Math.round(d);  // Redondea los datos para obtener el valor discreto de Poisson
                    if (k <= maxK)
                        observadasPoisson[k]++;  // Incrementa la frecuencia observada para ese valor
                }
                double chi = 0;  // Inicializa el estadistico Chi-Cuadrado
                for (int k = 0; k <= maxK; k++) {
                    double prob = poissonPMF(k, lambda);  // Calcula la funcion de masa de probabilidad para Poisson
                    double esperada = prob * datos.length;  // Calcula la frecuencia esperada para ese valor
                    if (esperada > 0) {
                        chi += Math.pow(observadasPoisson[k] - esperada, 2) / esperada;  // Calcula la contribucion al estadistico Chi-Cuadrado
                    }
                }
                return chi;  // Devuelve el estadistico Chi-Cuadrado para Poisson
        }

        // Calcular estadistico Chi-Cuadrado en base a las frecuencias observadas y esperadas
        double chi = 0;
        for (int i = 0; i < intervalos; i++) {
            if (esperadas[i] > 0) {
                chi += Math.pow(observadas[i] - esperadas[i], 2) / esperadas[i];  // Calcula la contribucion al estadistico Chi-Cuadrado
            }
        }

        return chi;  // Devuelve el estadistico Chi-Cuadrado
    }

    // Metodo para calcular la funcion de distribucion acumulada (CDF) de la distribucion normal
    private static double normalCDF(double x, double mu, double sigma) {
        return 0.5 * (1 + erf((x - mu) / (sigma * Math.sqrt(2))));  // Usa la funcion error para calcular la CDF normal
    }

    // Metodo para calcular la funcion de error (error function)
    private static double erf(double z) {
        double t = 1.0 / (1.0 + 0.3275911 * Math.abs(z));  // Calcula un valor intermedio
        double[] a = { 0.254829592, -0.284496736, 1.421413741, -1.453152027, 1.061405429 };  // Coeficientes de la funcion error
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * Math.pow(t, i + 1);  // Calcula la suma de los terminos
        }
        double sign = z >= 0 ? 1 : -1;  // Determina el signo de la funcion error
        return sign * (1 - sum * Math.exp(-z * z));  // Devuelve el valor de la funcion error
    }

    // Metodo para calcular la funcion de masa de probabilidad (PMF) de Poisson
    private static double poissonPMF(int k, double lambda) {
        return Math.pow(lambda, k) * Math.exp(-lambda) / factorial(k);  // Calcula la probabilidad de Poisson para k
    }

    // Metodo para calcular el factorial de un numero
    private static long factorial(int n) {
        long res = 1;
        for (int i = 2; i <= n; i++)  // Calcula el factorial de n
            res *= i;
        return res;  // Devuelve el resultado
    }
}

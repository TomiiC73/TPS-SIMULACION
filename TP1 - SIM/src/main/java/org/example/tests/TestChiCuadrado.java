package org.example.tests;

import java.util.Arrays;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

public class TestChiCuadrado {

    public static double valorCritico(int gradosLibertad, double nivelDeAceptacion) {
        ChiSquaredDistribution chid = new ChiSquaredDistribution(gradosLibertad);
        return chid.inverseCumulativeProbability(nivelDeAceptacion);
    }

    public enum Distribucion {
        UNIFORME, NORMAL, EXPONENCIAL, POISSON
    }

    public static double calcular(double[] datos, int intervalos, Distribucion tipo, double... params) {
        double[] observadas = new double[intervalos];
        double[] esperadas = new double[intervalos];

        double min = Arrays.stream(datos).min().orElse(0);
        double max = Arrays.stream(datos).max().orElse(0);
        double ancho = (max - min) / intervalos;

        // Contar frecuencias observadas
        for (double valor : datos) {
            int i = (int) ((valor - min) / ancho);
            if (i == intervalos)
                i--; // Para el borde superior
            observadas[i]++;
        }

        switch (tipo) {
            case UNIFORME:
                Arrays.fill(esperadas, (double) datos.length / intervalos);
                break;

            case EXPONENCIAL:
                double lambda = params[0];
                for (int i = 0; i < intervalos; i++) {
                    double li = min + i * ancho;
                    double ls = li + ancho;
                    double prob = Math.exp(-lambda * li) - Math.exp(-lambda * ls);
                    esperadas[i] = prob * datos.length;
                }
                break;

            case NORMAL:
                double mu = params[0];
                double sigma = params[1];
                for (int i = 0; i < intervalos; i++) {
                    double li = min + i * ancho;
                    double ls = li + ancho;
                    double prob = normalCDF(ls, mu, sigma) - normalCDF(li, mu, sigma);
                    esperadas[i] = prob * datos.length;
                }
                break;

            case POISSON:
                lambda = params[0];
                int maxK = (int) Arrays.stream(datos).max().orElse(0);
                int[] observadasPoisson = new int[maxK + 1];
                for (double d : datos) {
                    int k = (int) Math.round(d);
                    if (k <= maxK)
                        observadasPoisson[k]++;
                }
                double chi = 0;
                for (int k = 0; k <= maxK; k++) {
                    double prob = poissonPMF(k, lambda);
                    double esperada = prob * datos.length;
                    if (esperada > 0) {
                        chi += Math.pow(observadasPoisson[k] - esperada, 2) / esperada;
                    }
                }
                return chi;
        }

        // Calcular estadístico Chi-Cuadrado
        double chi = 0;
        for (int i = 0; i < intervalos; i++) {
            if (esperadas[i] > 0) {
                chi += Math.pow(observadas[i] - esperadas[i], 2) / esperadas[i];
            }
        }

        return chi;
    }

    private static double normalCDF(double x, double mu, double sigma) {
        return 0.5 * (1 + erf((x - mu) / (sigma * Math.sqrt(2))));
    }

    private static double erf(double z) {
        double t = 1.0 / (1.0 + 0.3275911 * Math.abs(z));
        double[] a = { 0.254829592, -0.284496736, 1.421413741, -1.453152027, 1.061405429 };
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * Math.pow(t, i + 1);
        }
        double sign = z >= 0 ? 1 : -1;
        return sign * (1 - sum * Math.exp(-z * z));
    }

    private static double poissonPMF(int k, double lambda) {
        return Math.pow(lambda, k) * Math.exp(-lambda) / factorial(k);
    }

    private static long factorial(int n) {
        long res = 1;
        for (int i = 2; i <= n; i++)
            res *= i;
        return res;
    }
}

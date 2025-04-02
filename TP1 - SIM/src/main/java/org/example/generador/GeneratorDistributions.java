package org.example.generador;

import org.example.distributions.*;
import org.example.visualization.Visualizador;

import java.util.Scanner;

public class GeneratorDistributions {

    public static void main(String[] args) {

        // Crear un objeto Scanner para leer la entrada del usuario
        Scanner scanner = new Scanner(System.in);

        // Solicitar la cantidad de valores a generar
        System.out.print("Ingrese la cantidad de valores a generar (máximo 50,000): ");
        int n = scanner.nextInt();

        // Validar que n no sea mayor que 50,000
        if (n > 50000) {
            System.out.println("La cantidad de valores no puede ser mayor que 50,000.");
            return;  // Terminar el programa si la cantidad es inválida
        }

        // Solicitar el número de intervalos para el histograma
        System.out.print("Ingrese el número de intervalos para el histograma: ");
        int intervalos = scanner.nextInt();

        // Validar que el número de intervalos sea positivo
        if (intervalos <= 0) {
            System.out.println("El número de intervalos debe ser mayor que 0.");
            return;  // Terminar el programa si la cantidad es inválida
        }

        // Elegir la distribución
        System.out.println("Seleccione la distribución a generar:");
        System.out.println("1. Uniforme");
        System.out.println("2. Exponencial");
        System.out.println("3. Poisson");
        System.out.println("4. Normal");
        System.out.print("Ingrese el número de la distribución: ");
        int opcion = scanner.nextInt();

        // Declarar variables para los parámetros de la distribución
        double a, b, lam, mu, sigma;

        // Según la opción elegida, pedir los parámetros necesarios
        switch (opcion) {
            case 1:  // Uniforme
                // Generar y visualizar la distribución uniforme
                double[] uniforme = Uniform.generate(n);
                Visualizador.visualizar(uniforme, "Uniforme", intervalos);
                break;

            case 2:  // Exponencial
                System.out.print("Ingrese el parámetro lambda: ");
                lam = scanner.nextDouble();

                // Generar y visualizar la distribución exponencial
                double[] exponencial = Exponential.generate(lam, n);
                Visualizador.visualizar(exponencial, "Exponencial", intervalos);
                for (double v : exponencial) {
                    System.out.printf("%.2f ", v);
                }
                break;

            case 3:  // Poisson
                System.out.print("Ingrese el parámetro lambda: ");
                lam = scanner.nextDouble();

                // Generar y visualizar la distribución Poisson
                double[] poisson = Poisson.generate(lam, n);
                Visualizador.visualizar(poisson, "Poisson", intervalos);
                for (double v : poisson) {
                    System.out.println(v);
                }
                break;

            case 4:  // Normal
                System.out.print("Ingrese el valor de la media (mu): ");
                mu = scanner.nextDouble();
                System.out.print("Ingrese el valor de la desviación estándar (sigma): ");
                sigma = scanner.nextDouble();

                // Generar y visualizar la distribución normal
                double[] normal = Normal.generate(mu, sigma, n);
                Visualizador.visualizar(normal, "Normal", intervalos);
                break;

            default:
                System.out.println("Opción no válida.");
                break;
        }

        // Cerrar el scanner
        scanner.close();
    }
}

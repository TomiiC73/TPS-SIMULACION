package org.example.generador;

import org.example.distributions.*;
import org.example.visualization.Visualizador;

import java.util.Scanner;

public class GeneratorDistributions {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Solicitar la cantidad de valores a generar
        System.out.print("Ingrese la cantidad de valores a generar (máximo 50,000): ");
        int n = scanner.nextInt();

        if (n > 50000) {
            System.out.println("La cantidad de valores no puede ser mayor que 50,000.");
            return;
        }

        // Solicitar número de intervalos
        System.out.print("Ingrese el número de intervalos para el histograma: ");
        int intervalos = scanner.nextInt();

        if (intervalos <= 0) {
            System.out.println("El número de intervalos debe ser mayor que 0.");
            return;
        }

        // Elegir distribución
        System.out.println("Seleccione la distribución a generar:");
        System.out.println("1. Uniforme");
        System.out.println("2. Exponencial");
        System.out.println("3. Poisson");
        System.out.println("4. Normal");
        System.out.print("Ingrese el número de la distribución: ");
        int opcion = scanner.nextInt();

        switch (opcion) {
            case 1:  // Uniforme
                System.out.print("Ingrese el valor de A (> 0): ");
                double a = scanner.nextDouble();
                System.out.print("Ingrese el valor de B (> A): ");
                double b = scanner.nextDouble();

                double[] uniforme = Uniform.generate(a, b, n);
                Visualizador.visualizar(uniforme, "Uniforme", intervalos);
                break;

            case 2:  // Exponencial
                System.out.print("Ingrese el parámetro lambda (> 0): ");
                double lam = scanner.nextDouble();

                double[] exponencial = Exponential.generate(lam, n);
                Visualizador.visualizar(exponencial, "Exponencial", intervalos);
                break;

            case 3:  // Poisson
                System.out.print("Ingrese el parámetro lambda (> 0): ");
                lam = scanner.nextDouble();

                double[] poisson = Poisson.generate(lam, n);
                Visualizador.visualizar(poisson, "Poisson", intervalos);
                break;

            case 4:  // Normal
                System.out.print("Ingrese el valor de la media (mu): ");
                double mu = scanner.nextDouble();
                System.out.print("Ingrese el valor de la desviación estándar (sigma > 0): ");
                double sigma = scanner.nextDouble();

                double[] normal = Normal.generate(mu, sigma, n);
                Visualizador.visualizar(normal, "Normal", intervalos);
                break;

            default:
                System.out.println("Opción no válida.");
                break;
        }

        scanner.close();
    }
}

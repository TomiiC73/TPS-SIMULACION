package org.example.generador;

import org.example.distributions.*;
import org.example.tests.TestChiCuadrado;
import org.example.visualization.Visualizador;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class GeneratorDistributions {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GeneratorDistributions().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Generador de Distribuciones");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(0, 2));
        frame.setLocationRelativeTo(null);

        JTextField nField = new JTextField();
        JTextField intervalosField = new JTextField();
        JTextField nivelDeAceptacionField = new JTextField();
        String[] distribuciones = {"Uniforme", "Exponencial", "Poisson", "Normal"};
        JComboBox<String> distribucionBox = new JComboBox<>(distribuciones);
        JButton generarButton = new JButton("Generar");

        frame.add(new JLabel("Cantidad de valores (máx 50,000):"));
        frame.add(nField);
        frame.add(new JLabel("Número de intervalos:"));
        frame.add(intervalosField);
        frame.add(new JLabel("Nivel de Aceptación:"));
        frame.add(nivelDeAceptacionField);
        frame.add(new JLabel("Seleccione la distribución:"));
        frame.add(distribucionBox);
        frame.add(new JLabel());
        frame.add(generarButton);

        generarButton.addActionListener(e -> {
            try {
                int n = Integer.parseInt(nField.getText());
                if (n > 50000 || n <= 0) {
                    JOptionPane.showMessageDialog(frame, "La muestra debe ser mayor a 0 y menor a 50000", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int intervalos = Integer.parseInt(intervalosField.getText());
                if (intervalos <= 0) {
                    JOptionPane.showMessageDialog(frame, "Los intervalos deben ser mayores que 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double nivelDeAceptacion = Double.parseDouble(nivelDeAceptacionField.getText());
                if (0 > nivelDeAceptacion || nivelDeAceptacion > 1) {
                    JOptionPane.showMessageDialog(frame, "El nivel de aceptación debe estar entre el 0 y el 1", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String seleccion = (String) distribucionBox.getSelectedItem();
                double[] valores = new double[n];
                double chi = 0;
                double valorCritico = 0;
                boolean pasaTest = false;
                int gradosDeLibertad = 0;

                switch (seleccion) {
                    case "Uniforme":
                        double a = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese el valor de A (> 0):"));
                        double b = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese el valor de B (> A):"));
                        if (a <= 0 || b <= a) throw new IllegalArgumentException("Valores de A y B inválidos.");
                        valores = Uniform.generate(a, b, n);
                        chi = TestChiCuadrado.calcular(valores, intervalos, TestChiCuadrado.Distribucion.UNIFORME);
                        gradosDeLibertad = intervalos - 1;
                        break;

                    case "Exponencial":
                        double lambdaExp = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese el parámetro lambda (> 0):"));
                        if (lambdaExp <= 0) throw new IllegalArgumentException("Lambda debe ser mayor que 0.");
                        valores = Exponential.generate(lambdaExp, n);
                        chi = TestChiCuadrado.calcular(valores, intervalos, TestChiCuadrado.Distribucion.EXPONENCIAL, lambdaExp);
                        gradosDeLibertad = intervalos - 1;
                        break;

                    case "Poisson":
                        double lambdaPoi = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese el parámetro lambda (> 0):"));
                        if (lambdaPoi <= 0) throw new IllegalArgumentException("Lambda debe ser mayor que 0.");
                        valores = Poisson.generate(lambdaPoi, n);
                        chi = TestChiCuadrado.calcular(valores, intervalos, TestChiCuadrado.Distribucion.POISSON, lambdaPoi);
                        gradosDeLibertad = intervalos - 1;
                        break;

                    case "Normal":
                        double mu = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese la media (mu):"));
                        double sigma = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese la desviación estándar (sigma > 0):"));
                        if (sigma <= 0) throw new IllegalArgumentException("Sigma debe ser mayor que 0.");
                        valores = Normal.generate(mu, sigma, n);
                        chi = TestChiCuadrado.calcular(valores, intervalos, TestChiCuadrado.Distribucion.NORMAL, mu, sigma);
                        gradosDeLibertad = intervalos - 3;
                        break;
                }

                Visualizador.visualizar(valores, seleccion, intervalos);

                // Valor crítico aproximado para alfa = 0.05 y k = intervalos - 1
                valorCritico = TestChiCuadrado.valorCritico(gradosDeLibertad, nivelDeAceptacion);
                pasaTest = chi < valorCritico;

                JOptionPane.showMessageDialog(
                        frame,
                        String.format("Chi-Cuadrado: %.4f\nValor Crítico (α=%.4f): %.4f\nResultado: %s",
                                chi, (1.0 - nivelDeAceptacion),valorCritico, pasaTest ? "Se acepta la hipótesis (pasa el test)" : "Se rechaza la hipótesis"),
                        "Resultado del Test Chi-Cuadrado",
                        JOptionPane.INFORMATION_MESSAGE
                );

                this.exportarNumerosGenerados(valores);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        frame.setVisible(true);
    }

    public void exportarNumerosGenerados(double[] valores){
        try (FileWriter writer = new FileWriter("nrosAleatorios.txt")) {
            for (int i = 0; i < valores.length; i++) {
                writer.write(valores[i] + "\n");       // Escribe el número seguido de un salto de línea
            }
            System.out.println("Números exportados correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


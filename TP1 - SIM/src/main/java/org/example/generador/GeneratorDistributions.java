package org.example.generador;

import org.example.distributions.*;
import org.example.visualization.Visualizador;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class GeneratorDistributions {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GeneratorDistributions().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Generador de Distribuciones");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(0, 2));

        JTextField nField = new JTextField();
        JTextField intervalosField = new JTextField();
        String[] distribuciones = {"Uniforme", "Exponencial", "Poisson", "Normal"};
        JComboBox<String> distribucionBox = new JComboBox<>(distribuciones);
        JButton generarButton = new JButton("Generar");

        frame.add(new JLabel("Cantidad de valores (máx 50,000):"));
        frame.add(nField);
        frame.add(new JLabel("Número de intervalos:"));
        frame.add(intervalosField);
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

                String seleccion = (String) distribucionBox.getSelectedItem();
                double[] valores = new double[n];

                switch (seleccion) {
                    case "Uniforme":
                        double a = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese el valor de A (> 0):"));
                        double b = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese el valor de B (> A):"));
                        if (a <= 0 || b <= a) throw new IllegalArgumentException("Valores de A y B inválidos.");
                        valores = Uniform.generate(a, b, n);
                        break;
                    case "Exponencial":
                    case "Poisson":
                        double lambda = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese el parámetro lambda (> 0):"));
                        if (lambda <= 0) throw new IllegalArgumentException("Lambda debe ser mayor que 0.");
                        valores = seleccion.equals("Exponencial") ? Exponential.generate(lambda, n) : Poisson.generate(lambda, n);
                        break;
                    case "Normal":
                        double mu = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese la media (mu):"));
                        double sigma = Double.parseDouble(JOptionPane.showInputDialog(frame, "Ingrese la desviación estándar (sigma > 0):"));
                        if (sigma <= 0) throw new IllegalArgumentException("Sigma debe ser mayor que 0.");
                        valores = Normal.generate(mu, sigma, n);
                        break;
                }

                Visualizador.visualizar(valores, seleccion, intervalos);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Ingrese valores numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "El número de la muestra debe ser mayor a 0 y menor a 50000", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setVisible(true);
    }
}

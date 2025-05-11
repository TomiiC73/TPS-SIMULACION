package org.example.generador;

import javax.swing.*;
import java.awt.*;

public class GeneratorDistributions {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GeneratorDistributions().createAndShowGUI());  // Ejecuta la creacion de la GUI en el hilo de eventos
    }

    // Metodo para crear y mostrar la interfaz grafica
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Generador de Distribuciones");  // Crea el marco de la ventana
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Define la operacion al cerrar la ventana
        frame.setSize(400, 300);  // Define el tamaño de la ventana
        frame.setLayout(new GridLayout(0, 2));  // Establece el layout de la ventana
        frame.setLocationRelativeTo(null);  // Centra la ventana en la pantalla

        JTextField nField = new JTextField();  // Campo de texto para la cantidad de valores
        JTextField intervalosField = new JTextField();  // Campo de texto para el número de intervalos
        JTextField nivelDeAceptacionField = new JTextField();  // Campo de texto para el nivel de aceptación
        String[] distribuciones = {"Uniforme", "Exponencial", "Poisson", "Normal"};  // Lista de distribuciones disponibles
        JComboBox<String> distribucionBox = new JComboBox<>(distribuciones);  // ComboBox para seleccionar la distribución
        JButton generarButton = new JButton("Generar");  // Botón para generar los valores

        // Agrega los componentes a la ventana
        frame.add(new JLabel("Cantidad de valores (máx 50,000):"));
        frame.add(nField);
        frame.add(new JLabel("Número de intervalos:"));
        frame.add(intervalosField);
        frame.add(new JLabel("Seleccione la distribución:"));
        frame.add(distribucionBox);
        frame.add(new JLabel());
        frame.add(generarButton);

        // Define la accion al hacer clic en el boton "Generar"
        generarButton.addActionListener(e -> {
            try {
                // Verifica que los valores de entrada sean correctos
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

                // Obtiene la distribucion seleccionada
                String seleccion = (String) distribucionBox.getSelectedItem();
                double[] valores = new double[n];
                double chi = 0;
                double valorCritico = 0;
                boolean pasaTest = false;
                int gradosDeLibertad = 0;

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Ingrese valores numericos validos.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Ocurrió un error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Hace visible la ventana
        frame.setVisible(true);
    }
}

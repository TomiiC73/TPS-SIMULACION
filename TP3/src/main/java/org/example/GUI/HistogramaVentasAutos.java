package org.example.GUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HistogramaVentasAutos {

    public static void mostrarHistogramaVentas(int mesesSimular, List<Integer> ventasPorMes) {
        // 1. Crear el dataset para el histograma
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Contar frecuencia de cada valor de ventas
        Map<Integer, Integer> frecuenciaVentas = new HashMap<>();
        for (int ventas : ventasPorMes) {
            frecuenciaVentas.put(ventas, frecuenciaVentas.getOrDefault(ventas, 0) + 1);
        }

        // Agregar datos al dataset
        for (Map.Entry<Integer, Integer> entry : frecuenciaVentas.entrySet()) {
            dataset.addValue(entry.getValue(), "Meses", entry.getKey().toString());
        }

        // 2. Crear el gráfico
        JFreeChart chart = ChartFactory.createBarChart(
                "Distribución de Ventas por Mes (" + mesesSimular + " meses)", // Título
                "Número de Ventas", // Etiqueta eje X
                "Cantidad de Meses", // Etiqueta eje Y
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // 3. Mostrar en un JFrame
        JFrame frame = new JFrame("Histograma de Ventas");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Función de ejemplo para generar datos aleatorios
    public static int[] generarDatosEjemplo(int meses) {
        int[] ventas = new int[meses];
        Random rand = new Random();

        // Generar datos con distribución aproximadamente normal
        for (int i = 0; i < meses; i++) {
            // Media 10, desviación 3
            double valor = rand.nextGaussian() * 3 + 10;
            ventas[i] = (int) Math.round(Math.max(0, valor));
        }
        return ventas;
    }

    public static void main(String[] args) {
        int meses = 1000;
        int[] ventasPorMes = generarDatosEjemplo(meses);
        for (int ventas: ventasPorMes) {
            System.out.println(ventas);
        }

        /*SwingUtilities.invokeLater(() -> {
            mostrarHistogramaVentas(meses, ventasPorMes);
        });*/
    }
}
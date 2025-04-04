package org.example.visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;

import javax.swing.*;

public class Visualizador {

    public static void visualizar(double[] datos, String nombreDistribucion, int intervalos) {
        // Crear un conjunto de datos para el histograma
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(nombreDistribucion, datos, intervalos); // Usar los bins proporcionados por el usuario

        // Crear el gráfico
        JFreeChart chart = ChartFactory.createHistogram(
                nombreDistribucion + " - Histograma",
                "Valor", "Frecuencia",
                dataset
        );

        // Mostrar el gráfico en un JFrame
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

package org.example.visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;

import javax.swing.*;

public class Visualizador {

    public static void visualizar(double[] datos, String nombreDistribucion) {
        // Crear un conjunto de datos para el histograma
        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries(nombreDistribucion, datos, 30);

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
        frame.setVisible(true);
    }
}

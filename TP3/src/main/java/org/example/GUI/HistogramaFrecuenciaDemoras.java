package org.example.GUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class HistogramaFrecuenciaDemoras {

    public static void mostrarHistogramaDemoras(HashMap<Integer,Integer> frecuenciaDeDemorasProveedor) {
        // 1. Crear el dataset para el histograma
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Agregar datos al dataset
        for (HashMap.Entry<Integer, Integer> entry : frecuenciaDeDemorasProveedor.entrySet()) {
            dataset.addValue(entry.getValue(), "Frecuencia", entry.getKey().toString());
        }

        // 2. Crear el gráfico
        JFreeChart chart = ChartFactory.createBarChart(
                "Distribución de Frecuencia de Demoras del Proveedor", // Título
                "Demora en meses", // Etiqueta eje X
                "Cantidad de veces", // Etiqueta eje Y
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        // Cambiar color de barras
        BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        Color azulClaro = new Color(0, 0, 150);
        renderer.setSeriesPaint(0, azulClaro);

        // 3. Mostrar en un JFrame
        JFrame frame = new JFrame("Histograma de Demoras");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

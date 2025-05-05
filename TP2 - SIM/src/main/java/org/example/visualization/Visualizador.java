package org.example.visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;

import javax.swing.*;  // Importa las clases de Swing para crear la interfaz grafica

public class Visualizador {

    // Metodo para visualizar un histograma de los datos proporcionados
    public static void visualizar(double[] datos, String nombreDistribucion, int intervalos) {
        // Crear un conjunto de datos para el histograma
        HistogramDataset dataset = new HistogramDataset();

        // Agregar los datos y los intervalos al conjunto de datos
        dataset.addSeries(nombreDistribucion, datos, intervalos); // Usar los bins proporcionados por el usuario

        // Crear el grafico con el conjunto de datos
        JFreeChart chart = ChartFactory.createHistogram(
                nombreDistribucion + " - Histograma",  // Titulo del grafico
                "Valor",  // Etiqueta en el eje X
                "Frecuencia",  // Etiqueta en el eje Y
                dataset  // Datos del histograma
        );

        // Crear un JFrame para mostrar el grafico
        JFrame frame = new JFrame();  // Crea una ventana (marco) para mostrar el grafico
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Define que hacer cuando se cierre la ventana
        frame.getContentPane().add(new ChartPanel(chart));  // Agrega el grafico al panel de contenido de la ventana
        frame.pack();  // Ajusta el tamaño de la ventana al contenido
        frame.setLocationRelativeTo(null);  // Centra la ventana en la pantalla
        frame.setVisible(true);  // Muestra la ventana
    }
}

package org.example.generador;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.Distribuciones.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class SimulacionInventarioAutosGUI {

    private static final Color COLOR_FONDO = new Color(245, 247, 250);
    private static final Color COLOR_ENCABEZADO = new Color(74, 111, 165);
    private static final Color COLOR_BOTON = new Color(74, 111, 165);
    private static final Color COLOR_TEXTO_BOTON = Color.WHITE;
    private static final Color COLOR_TABLA_FONDO = Color.WHITE;
    private static final Color COLOR_TABLA_ALTERNADO = new Color(240, 250, 255);
    private static final Color COLOR_METRICAS_FONDO = new Color(248, 250, 252);
    private static final Color COLOR_BORDE = new Color(241, 238, 241);

    private int[] ventasCoches;
    private int[] frecuenciaVentas;
    private double[] probabilidadEntrega;
    private double costoAlmacenamiento;
    private double costoVentaPerdida;
    private double costoPedido;
    private int puntoReorden;
    private int cantidadPedido;
    private String distribucionEntrega;
    private double[] parametrosDistribucion;

    private Random random = new Random();
    private JFrame frame;
    private JTable table;
    private JTextArea metricsArea;
    private JPanel inputPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulacionInventarioAutosGUI simulacion = new SimulacionInventarioAutosGUI();
            simulacion.mostrarVentanaInicial();
        });
    }

    public void mostrarVentanaInicial() {
        frame = new JFrame("Simulación de Inventario de Autos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(COLOR_FONDO);

        // Panel de entrada de parámetros
        inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(COLOR_FONDO);

        // Campos para parámetros básicos
        agregarCampo("Meses a simular:", "36");
        agregarCampo("Stock inicial:", "25");
        agregarCampo("Fila inicio a mostrar:", "1");
        agregarCampo("Fila fin a mostrar:", "36");

        // Botón para configurar parámetros avanzados
        JButton btnConfigAvanzada = new JButton("Configuración Avanzada");
        btnConfigAvanzada.setBackground(COLOR_BOTON);
        btnConfigAvanzada.setForeground(COLOR_TEXTO_BOTON);
        btnConfigAvanzada.setFont(new Font("Lato", Font.BOLD, 14));
        btnConfigAvanzada.addActionListener(e -> mostrarConfiguracionAvanzada());

        // Botón para ejecutar simulación
        JButton btnSimular = new JButton("Ejecutar Simulación");
        btnSimular.setBackground(COLOR_BOTON);
        btnSimular.setForeground(COLOR_TEXTO_BOTON);
        btnSimular.setFont(new Font("Lato", Font.BOLD, 14));
        btnSimular.setFocusPainted(false);
        btnSimular.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnSimular.addActionListener(e -> validarYEJecutarSimulacion());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(COLOR_FONDO);
        buttonPanel.add(btnConfigAvanzada);
        buttonPanel.add(btnSimular);

        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void agregarCampo(String etiqueta, String valorPorDefecto) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Lato", Font.BOLD, 18));

        JTextField txt = new JTextField(valorPorDefecto);
        txt.setFont(new Font("Lato", Font.PLAIN, 18));

        inputPanel.add(lbl);
        inputPanel.add(txt);
    }

    private void mostrarConfiguracionAvanzada() {
        JDialog dialog = new JDialog(frame, "Configuración Avanzada", true);
        dialog.setSize(1100, 700);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(COLOR_FONDO);

        JPanel panel = new JPanel(new GridLayout(0, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(COLOR_FONDO);

        // Configuración de fuentes
        Font fuenteLabels = new Font("Lato", Font.BOLD, 16);
        Font fuenteTextFields = new Font("Lato", Font.PLAIN, 16);
        Font fuenteCombobox = new Font("Lato", Font.PLAIN, 16);
        Font fuenteBoton = new Font("Lato", Font.BOLD, 16);

        // Campos para costos
        JLabel lblCostoAlmacen = new JLabel("Costo de almacenamiento por auto/mes:");
        lblCostoAlmacen.setFont(fuenteLabels);
        lblCostoAlmacen.setForeground(Color.BLACK);
        JTextField txtCostoAlmacen = new JTextField("6000");
        txtCostoAlmacen.setFont(fuenteTextFields);
        panel.add(lblCostoAlmacen);
        panel.add(txtCostoAlmacen);

        JLabel lblCostoVentaPerdida = new JLabel("Costo por venta perdida: $");
        lblCostoVentaPerdida.setFont(fuenteLabels);
        lblCostoVentaPerdida.setForeground(Color.BLACK);
        JTextField txtCostoVentaPerdida = new JTextField("14350");
        txtCostoVentaPerdida.setFont(fuenteTextFields);
        panel.add(lblCostoVentaPerdida);
        panel.add(txtCostoVentaPerdida);

        JLabel lblCostoPedido = new JLabel("Costo por pedido: $");
        lblCostoPedido.setFont(fuenteLabels);
        lblCostoPedido.setForeground(Color.BLACK);
        JTextField txtCostoPedido = new JTextField("25700");
        txtCostoPedido.setFont(fuenteTextFields);
        panel.add(lblCostoPedido);
        panel.add(txtCostoPedido);

        // Campos para política de inventario
        JLabel lblPuntoReorden = new JLabel("Punto de reorden:");
        lblPuntoReorden.setFont(fuenteLabels);
        lblPuntoReorden.setForeground(Color.BLACK);
        JTextField txtPuntoReorden = new JTextField("12");
        txtPuntoReorden.setFont(fuenteTextFields);
        panel.add(lblPuntoReorden);
        panel.add(txtPuntoReorden);

        JLabel lblCantidadPedido = new JLabel("Cantidad a pedir:");
        lblCantidadPedido.setFont(fuenteLabels);
        lblCantidadPedido.setForeground(Color.BLACK);
        JTextField txtCantidadPedido = new JTextField("20");
        txtCantidadPedido.setFont(fuenteTextFields);
        panel.add(lblCantidadPedido);
        panel.add(txtCantidadPedido);

        // Campos para distribución de ventas
        JLabel lblVentas = new JLabel("Coches vendidos por mes (separados por comas):");
        lblVentas.setFont(fuenteLabels);
        lblVentas.setForeground(Color.BLACK);
        JTextField txtVentas = new JTextField("6,7,8,9,10,11,12");
        txtVentas.setFont(fuenteTextFields);
        panel.add(lblVentas);
        panel.add(txtVentas);

        JLabel lblFrecuencias = new JLabel("Frecuencias de ventas (separadas por comas):");
        lblFrecuencias.setFont(fuenteLabels);
        lblFrecuencias.setForeground(Color.BLACK);
        JTextField txtFrecuencias = new JTextField("3,4,6,12,9,1,1");
        txtFrecuencias.setFont(fuenteTextFields);
        panel.add(lblFrecuencias);
        panel.add(txtFrecuencias);

        // Configuración de distribución de tiempo de entrega
        JLabel lblDistribucion = new JLabel("Distribución para tiempo de entrega:");
        lblDistribucion.setFont(fuenteLabels);
        lblDistribucion.setForeground(Color.BLACK);
        String[] distribuciones = {"Uniforme", "Normal", "Poisson", "Exponencial"};
        JComboBox<String> comboDistribuciones = new JComboBox<>(distribuciones);
        comboDistribuciones.setFont(fuenteCombobox);
        panel.add(lblDistribucion);
        panel.add(comboDistribuciones);

        // Elegir entre carga manual o carga por estadísticos
        JLabel lblTipoCarga = new JLabel("Distribución para tiempo de entrega:");
        lblTipoCarga.setFont(fuenteLabels);
        lblTipoCarga.setForeground(Color.BLACK);
        String[] tipoCarga = {"Manual", "Estadísticos"};
        JComboBox<String> comboTipoCarga = new JComboBox<>(tipoCarga);
        comboDistribuciones.setFont(fuenteCombobox);
        panel.add(lblTipoCarga);
        panel.add(comboTipoCarga);

        // Campos para distribución de ventas
        JLabel lblDemora = new JLabel("Demora del proveedor (separados por comas):");
        lblDemora.setFont(fuenteLabels);
        lblDemora.setForeground(Color.BLACK);
        JTextField txtDemora = new JTextField("1,2,3,4");
        lblDemora.setFont(fuenteTextFields);
        panel.add(lblDemora);
        panel.add(txtDemora);

        // Campos para probabilidades de entrega
        JLabel lblProbEntrega = new JLabel("Probabilidades de demora (suma = 1, separadas por comas):");
        lblProbEntrega.setFont(fuenteLabels);
        lblProbEntrega.setForeground(Color.BLACK);
        JTextField txtProbEntrega = new JTextField("0.44,0.33,0.16,0.07");
        txtProbEntrega.setFont(fuenteTextFields);
        panel.add(lblProbEntrega);
        panel.add(txtProbEntrega);

        JLabel lblParametros = new JLabel("Parámetros (separados por comas):");
        lblParametros.setFont(fuenteLabels);
        lblParametros.setForeground(Color.BLACK);
        JTextField txtParametros = new JTextField("1,4");
        txtParametros.setFont(fuenteTextFields);
        panel.add(lblParametros);
        panel.add(txtParametros);

        txtDemora.setEnabled(true);
        txtProbEntrega.setEnabled(true);
        txtParametros.setEnabled(false);

        comboTipoCarga.addActionListener(e -> {
            try{
                String seleccion = (String) comboTipoCarga.getSelectedItem();
                if ("Manual".equals(seleccion)) {
                    txtDemora.setEnabled(true);
                    txtProbEntrega.setEnabled(true);
                    txtParametros.setEnabled(false);
                } else {
                    txtDemora.setEnabled(false);
                    txtProbEntrega.setEnabled(false);
                    txtParametros.setEnabled(true);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // Configurar el botón con fuente grande
        JButton btnGuardar = new JButton("Guardar Configuración");
        btnGuardar.setFont(fuenteBoton);
        btnGuardar.setBackground(COLOR_BOTON);
        btnGuardar.setForeground(COLOR_TEXTO_BOTON);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnGuardar.addActionListener(e -> {
            try {
                ventasCoches = parsearEnteros(txtVentas.getText());
                frecuenciaVentas = parsearEnteros(txtFrecuencias.getText());
                probabilidadEntrega = parsearDoubles(txtProbEntrega.getText());
                costoAlmacenamiento = Double.parseDouble(txtCostoAlmacen.getText());
                costoVentaPerdida = Double.parseDouble(txtCostoVentaPerdida.getText());
                costoPedido = Double.parseDouble(txtCostoPedido.getText());
                puntoReorden = Integer.parseInt(txtPuntoReorden.getText());
                cantidadPedido = Integer.parseInt(txtCantidadPedido.getText());
                distribucionEntrega = (String) comboDistribuciones.getSelectedItem();

                // Obtenemos parámetros del txtParametros y lo parseamos a double
                String[] parametrosStr = txtParametros.getText().split(",");
                parametrosDistribucion = new double[parametrosStr.length];
                for(int i = 0; i < parametrosStr.length; i++) {
                    parametrosDistribucion[i] = Double.parseDouble(parametrosStr[i].trim());
                }

                if (ventasCoches.length != frecuenciaVentas.length) {
                    throw new IllegalArgumentException("La cantidad de valores de ventas debe coincidir con las frecuencias");
                }

                double sumaProb = 0;
                for (double prob : probabilidadEntrega) {
                    sumaProb += prob;
                }
                if (Math.abs(sumaProb - 1.0) > 0.001) {
                    throw new IllegalArgumentException("Las probabilidades de entrega deben sumar 1");
                }

                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Por favor ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnGuardar, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
    private int[] parsearEnteros(String input) {
        String[] partes = input.split(",");
        int[] resultado = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            resultado[i] = Integer.parseInt(partes[i].trim());
        }
        return resultado;
    }

    private double[] parsearDoubles(String input) {
        String[] partes = input.split(",");
        double[] resultado = new double[partes.length];
        for (int i = 0; i < partes.length; i++) {
            resultado[i] = Double.parseDouble(partes[i].trim());
        }
        return resultado;
    }

    private void validarYEJecutarSimulacion() {
        try {
            // Obtener componentes del panel de entrada
            Component[] components = inputPanel.getComponents();

            int mesesSimular = Integer.parseInt(((JTextField)components[1]).getText());
            int stockInicial = Integer.parseInt(((JTextField)components[3]).getText());
            int filaInicio = Integer.parseInt(((JTextField)components[5]).getText());
            int filaFin = Integer.parseInt(((JTextField)components[7]).getText());

            // Validar que se haya configurado la parte avanzada
            if (ventasCoches == null) {
                throw new IllegalStateException("Debe configurar los parámetros avanzados primero");
            }

            ejecutarSimulacion(mesesSimular, filaInicio, filaFin, stockInicial);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Por favor ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void ejecutarSimulacion(int mesesSimular, int filaInicioMostrar, int filaFinMostrar, int stockInicial) {
        List<ResultadoMes> resultados = new ArrayList<>();
        int inventario = stockInicial;
        int pedidoPendiente = 0;
        int mesesRestantesEntrega = 0; // Inicialmente no hay pedidos en camino
        double costoTotal = 0;
        double costoTotalAlmacenamiento = 0;
        double costoTotalVentaPerdida = 0;
        double costoTotalPedido = 0;
        //double costoTotalAcumulado = 0;
        double costoTotalPromedio = 0;

        for (int mes = 1; mes <= mesesSimular; mes++) {
            int inventarioInicial = inventario;

            // 1. Decrementar el contador al INICIO del mes (antes de procesar el pedido)
            if (mesesRestantesEntrega > 0) {
                mesesRestantesEntrega--;
            }

            // 2. Verificar si el pedido llegó EN ESTE MES (después de decrementar)
            if (mesesRestantesEntrega == 0 && pedidoPendiente > 0) {
                inventario += pedidoPendiente; // Sumar al inventario
                inventarioInicial = inventario;
                pedidoPendiente = 0; // Reiniciar
            }

            // Resto de la lógica del mes (ventas, costos, etc.)
            // Generar ventas
            double randomVentas = random.nextDouble();
            int ventas = generarVentas(randomVentas);
            int ventasReales = Math.min(ventas, inventario);
            int ventasPerdidas = ventas - ventasReales;
            inventario -= ventasReales;

            // Calcular costos
            double costoAlmacen = inventario * this.costoAlmacenamiento;
            double costoVentasPerd = ventasPerdidas * this.costoVentaPerdida;

            double costoPed = 0; // Al inicio es 0
            double randomEntrega = 0;

            // Hacer nuevo pedido si es necesario
            boolean hacerPedido = (inventario <= this.puntoReorden) && (pedidoPendiente == 0);
            if (hacerPedido) {
                costoPed = this.costoPedido;
                pedidoPendiente = this.cantidadPedido;
                randomEntrega = random.nextDouble();
                mesesRestantesEntrega = GeneradorDistribuciones.generarTiempoEntrega(
                        distribucionEntrega,
                        randomEntrega,
                        parametrosDistribucion
                );
            }

            double costoMes = costoAlmacen + costoVentasPerd + costoPed;
            costoTotalVentaPerdida += costoVentasPerd;
            costoTotalAlmacenamiento += costoAlmacen;
            costoTotalPedido += costoPed;
            costoTotal += costoMes;
            costoTotalPromedio = costoTotal / mes;

            ResultadoMes resultado = new ResultadoMes(
                    mes,
                    inventarioInicial,
                    inventario,
                    ventas,
                    ventasReales,
                    ventasPerdidas,
                    pedidoPendiente,
                    mesesRestantesEntrega,
                    costoAlmacen,
                    costoTotalAlmacenamiento,
                    costoVentasPerd,
                    costoTotalVentaPerdida,
                    costoPed,
                    costoTotalPedido,
                    costoMes,
                    costoTotal,
                    randomVentas,
                    randomEntrega,
                    costoTotalPromedio
            );
            resultados.add(resultado);
        }

        mostrarResultadosGUI(resultados, filaInicioMostrar, filaFinMostrar);
    }

    private int generarVentas(double randomValue) {
        double acumulado = 0.0;
        double totalFrecuencia = Arrays.stream(frecuenciaVentas).sum();

        for (int i = 0; i < ventasCoches.length; i++) {
            acumulado += frecuenciaVentas[i] / totalFrecuencia;
            if (randomValue <= acumulado) {
                return ventasCoches[i];
            }
        }

        return ventasCoches[ventasCoches.length - 1];
    }

    private void mostrarResultadosGUI(List<ResultadoMes> resultados, int inicio, int fin) {
        JFrame resultadosFrame = new JFrame("Resultados de Simulación");
        resultadosFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultadosFrame.setSize(1400, 800); // Aumenté el ancho para acomodar más columnas
        resultadosFrame.setLayout(new BorderLayout());
        resultadosFrame.getContentPane().setBackground(COLOR_FONDO);

        // Panel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(COLOR_ENCABEZADO);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel("RESULTADOS DE LA SIMULACIÓN");
        titleLabel.setFont(new Font("Lato", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        resultadosFrame.add(titlePanel, BorderLayout.NORTH);

        // Columnas de la tabla (actualizadas)
        String[] columnNames = {
                "Mes",
                "Inventario Inicial",
                "RND Ventas",
                "Ventas",
                "Ventas Concretadas",
                "Stock Out",
                "Pedido",
                "Inventario Final",
                "RND Entrega",
                "Entrega",
                "Costo Almacenaje",
                "Costo Almacenaje ++",
                "Costo Stock Out",
                "Costo Stock Out ++",
                "Costo Pedido",
                "Costo Pedido ++",
                "Costo Mes",
                "Costo Total",
                "Costo Total Promedio"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : Object.class;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Lato", Font.PLAIN, 14)); // Reduje el tamaño de fuente para más columnas
        table.setRowHeight(25); // Reduje la altura de fila
        table.setGridColor(COLOR_BORDE);
        table.setBackground(COLOR_TABLA_FONDO);
        table.setSelectionBackground(new Color(200, 230, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Renderizadores
        DefaultTableCellRenderer centerRenderer = new CenterRenderer();
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Aplicar renderizadores
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 0 || i == 2 || i == 8 || i == 16) { // Le añado colorcito
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            } else { // Columnas numéricas alineadas a la derecha
                table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            }
        }

        // Configurar encabezados
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_ENCABEZADO);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBackground(COLOR_ENCABEZADO);
        headerRenderer.setForeground(Color.WHITE);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Configurar anchos de columnas
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] columnWidths = {50, 115, 110, 60, 60, 70, 60, 120, 100, 60, 135, 140, 120, 130, 130, 130, 120, 120, 120};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));

        // Llenar la tabla con los datos
        List<ResultadoMes> resultadosMostrar = new ArrayList<>();


        for (int i = inicio - 1; i < fin; i++) {
            if(i == resultados.size() - 1){
                break;
            }
            resultadosMostrar.add(resultados.get(i));
        }
        resultadosMostrar.add(resultados.getLast());

        for (ResultadoMes resultado : resultadosMostrar) {
            Object[] rowData = {
                    resultado.mes,
                    resultado.inventarioInicial,
                    String.format("%.4f", resultado.randomVentas),
                    resultado.ventas,
                    resultado.ventasReales,
                    resultado.ventasPerdidas,
                    resultado.pedidoPendiente,
                    resultado.inventarioFinal,
                    resultado.randomEntrega > 0 ? String.format("%.4f", resultado.randomEntrega) : "-",
                    resultado.mesesRestantesEntrega > 0 ? resultado.mesesRestantesEntrega : "-",
                    String.format("$%,.2f", resultado.costoAlmacenamiento),
                    String.format("$%,.2f", resultado.costoTotalAlmacenamiento),
                    String.format("$%,.2f", resultado.costoVentasPerdidas),
                    String.format("$%,.2f", resultado.costoTotalVentasPerdidas),
                    String.format("$%,.2f", resultado.costoPedido),
                    String.format("$%,.2f", resultado.costoTotalPedidos),
                    String.format("$%,.2f", resultado.costoMes),
                    String.format("$%,.2f", resultado.costoTotal),
                    String.format("$%,.2f", resultado.costoTotalPromedio)
            };
            model.addRow(rowData);
        }

        // Panel de métricas
        metricsArea = new JTextArea();
        metricsArea.setEditable(false);
        metricsArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        metricsArea.setBackground(COLOR_METRICAS_FONDO);
        metricsArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Generar métricas
        generarMetricas(resultados);

        // Dividir la ventana
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, new JScrollPane(metricsArea));
        splitPane.setResizeWeight(0.7);
        splitPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        resultadosFrame.add(splitPane, BorderLayout.CENTER);
        resultadosFrame.setLocationRelativeTo(frame);
        resultadosFrame.setVisible(true);
    }

    private void generarMetricas(List<ResultadoMes> resultados) {
        StringBuilder metricsBuilder = new StringBuilder();
        metricsBuilder.append("MÉTRICAS DETALLADAS DE LA SIMULACIÓN\n");
        metricsBuilder.append("====================================\n\n");

        // 1. Métricas básicas
        metricsBuilder.append("■ DATOS GENERALES\n");
        metricsBuilder.append(String.format("  - Duración de la simulación: %,d meses\n", resultados.size()));
        metricsBuilder.append(String.format("  - Stock inicial: %,d autos\n", resultados.get(0).inventarioInicial));
        metricsBuilder.append(String.format("  - Punto de reorden configurado: %,d autos\n", puntoReorden));
        metricsBuilder.append(String.format("  - Cantidad de pedido configurada: %,d autos\n", cantidadPedido));
        metricsBuilder.append("\n");

        // 2. Análisis de pedidos
        long totalPedidos = resultados.stream().filter(r -> r.costoPedido > 0).count();
        metricsBuilder.append("■ ANÁLISIS DE PEDIDOS\n");
        metricsBuilder.append(String.format("  - Total de pedidos realizados: %,d\n", totalPedidos));
        metricsBuilder.append(String.format("  - Costo total en pedidos: $%,.2f\n",
                resultados.stream().mapToDouble(r -> r.costoPedido).sum()));
        metricsBuilder.append(String.format("  - Costo promedio por pedido: $%,.2f\n",
                totalPedidos > 0 ? resultados.stream().mapToDouble(r -> r.costoPedido).sum() / totalPedidos : 0));
        metricsBuilder.append("\n");

        // 3. Análisis detallado de costos
        double costoTotal = resultados.get(resultados.size()-1).costoTotal;
        double costoPromedioMensual = costoTotal / resultados.size();
        double costoTotalAlmacenamiento = resultados.stream().mapToDouble(r -> r.costoAlmacenamiento).sum();
        double costoPromedioAlmacenamiento = costoTotalAlmacenamiento / resultados.size();
        double costoTotalVentasPerdidas = resultados.stream().mapToDouble(r -> r.costoVentasPerdidas).sum();

        metricsBuilder.append("■ ANÁLISIS DE COSTOS\n");
        metricsBuilder.append(String.format("  - Costo total acumulado: $%,.2f\n", costoTotal));
        metricsBuilder.append(String.format("  - Costo promedio mensual: $%,.2f\n", costoPromedioMensual));
        metricsBuilder.append("\n  Desglose de costos:\n");
        metricsBuilder.append(String.format("  - Almacenamiento: $%,.2f (%.1f%% del total)\n",
                costoTotalAlmacenamiento, (costoTotalAlmacenamiento * 100 / costoTotal)));
        metricsBuilder.append(String.format("  - Ventas perdidas: $%,.2f (%.1f%% del total)\n",
                costoTotalVentasPerdidas, (costoTotalVentasPerdidas * 100 / costoTotal)));
        metricsBuilder.append(String.format("  - Pedidos: $%,.2f (%.1f%% del total)\n",
                resultados.stream().mapToDouble(r -> r.costoPedido).sum(),
                (resultados.stream().mapToDouble(r -> r.costoPedido).sum() * 100 / costoTotal)));
        metricsBuilder.append("\n");

        // 4. Análisis de ventas
        int totalVentas = resultados.stream().mapToInt(r -> r.ventasReales).sum();
        int totalVentasPerdidas = resultados.stream().mapToInt(r -> r.ventasPerdidas).sum();
        double porcentajeVentasPerdidas = (double)totalVentasPerdidas * 100 / (totalVentas + totalVentasPerdidas);

        metricsBuilder.append("■ ANÁLISIS DE VENTAS\n");
        metricsBuilder.append(String.format("  - Total de autos vendidos: %,d\n", totalVentas));
        metricsBuilder.append(String.format("  - Total de ventas perdidas: %,d (%.1f%% de oportunidades)\n",
                totalVentasPerdidas, porcentajeVentasPerdidas));
        metricsBuilder.append("\n");

        // 5. Análisis de inventario
        int maxInventario = resultados.stream().mapToInt(r -> r.inventarioInicial).max().orElse(0);
        int minInventario = resultados.stream().mapToInt(r -> r.inventarioFinal).min().orElse(0);

        metricsBuilder.append("■ ANÁLISIS DE INVENTARIO\n");
        metricsBuilder.append(String.format("  - Máximo inventario: %,d autos\n", maxInventario));
        metricsBuilder.append(String.format("  - Mínimo inventario: %,d autos\n", minInventario));
        metricsBuilder.append("\n");

        // Métricas de efectividad del punto de reorden
        int mesesBajoReorden = (int) resultados.stream()
                .filter(r -> r.inventarioFinal <= puntoReorden)
                .count();
        double efectividadReorden = (double) mesesBajoReorden / resultados.size() * 100;

        metricsBuilder.append("■ EFECTIVIDAD DEL PUNTO DE REORDEN\n");
        metricsBuilder.append(String.format("  - Veces que cayó bajo punto de reorden: %,d/%,d meses\n",
                mesesBajoReorden, resultados.size()));
        metricsBuilder.append(String.format("  - Porcentaje de efectividad: %.1f%%\n", efectividadReorden));

        // Métricas de exceso de inventario
        int limiteExceso = puntoReorden + cantidadPedido;
        int mesesExcesoInventario = (int) resultados.stream()
                .filter(r -> r.inventarioFinal > limiteExceso)
                .count();
        double porcentajeExceso = (double) mesesExcesoInventario / resultados.size() * 100;

        metricsBuilder.append("\n■ ANÁLISIS DE EXCESO DE INVENTARIO\n");
        metricsBuilder.append(String.format("  - Límite de exceso (Reorden + Cantidad Pedido): %,d autos\n", limiteExceso));
        metricsBuilder.append(String.format("  - Meses con exceso: %,d (%.1f%% del tiempo)\n",
                mesesExcesoInventario, porcentajeExceso));
        metricsBuilder.append("  - [Valor alto indica sobrestock costoso]\n");

        // Configurar el área de texto
        metricsArea.setText(metricsBuilder.toString());

        // Crear y mostrar gráficos comparativos
        crearYMostrarGraficos(resultados);
    }

    private void crearYMostrarGraficos(List<ResultadoMes> resultados) {
        // Crear un nuevo panel para los gráficos
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2));
        chartsPanel.setBackground(COLOR_METRICAS_FONDO);

        // Crear los gráficos
        chartsPanel.add(new ChartPanel(createCostComparisonChart(resultados)));
        chartsPanel.add(new ChartPanel(createCostDistributionChart(resultados)));

        // Crear un nuevo panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(metricsArea), BorderLayout.CENTER);
        mainPanel.add(chartsPanel, BorderLayout.SOUTH);

        // Mostrar en un nuevo diálogo
        JDialog chartDialog = new JDialog();
        chartDialog.setTitle("Gráficos de Resultados");
        chartDialog.setContentPane(mainPanel);
        chartDialog.pack();
        chartDialog.setLocationRelativeTo(frame);
        chartDialog.setVisible(true);
    }
    private JFreeChart createCostComparisonChart(List<ResultadoMes> resultados) {
        // Calcular promedios
        double avgOrderCost = resultados.stream().mapToDouble(r -> r.costoPedido).average().orElse(0);
        double avgStorageCost = resultados.stream().mapToDouble(r -> r.costoAlmacenamiento).average().orElse(0);
        double avgStockOutCost = resultados.stream().mapToDouble(r -> r.costoVentasPerdidas).average().orElse(0);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(avgOrderCost, "Costos", "Pedidos");
        dataset.addValue(avgStorageCost, "Costos", "Almacenamiento");
        dataset.addValue(avgStockOutCost, "Costos", "Ventas Perdidas");

        JFreeChart chart = ChartFactory.createBarChart(
                "Comparación de Costos Promedio Mensual",
                "Tipo de Costo",
                "Costo Promedio ($)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // Personalización del gráfico
        chart.setBackgroundPaint(COLOR_METRICAS_FONDO);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(COLOR_BORDE);
        plot.getRenderer().setSeriesPaint(0, new Color(74, 111, 165));
        plot.getRangeAxis().setUpperMargin(0.05);

        return chart;
    }

    private JFreeChart createCostDistributionChart(List<ResultadoMes> resultados) {
        // Calcular totales
        double totalAlmacenamiento = resultados.stream().mapToDouble(r -> r.costoAlmacenamiento).sum();
        double totalVentasPerdidas = resultados.stream().mapToDouble(r -> r.costoVentasPerdidas).sum();
        double totalPedidos = resultados.stream().mapToDouble(r -> r.costoPedido).sum();

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Almacenamiento", totalAlmacenamiento);
        dataset.setValue("Ventas Perdidas", totalVentasPerdidas);
        dataset.setValue("Pedidos", totalPedidos);

        JFreeChart chart = ChartFactory.createPieChart(
                "Distribución Porcentual de Costos",
                dataset,
                true, true, false);

        // Personalización del gráfico
        chart.setBackgroundPaint(COLOR_METRICAS_FONDO);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionPaint("Almacenamiento", new Color(205, 202, 0));
        plot.setSectionPaint("Ventas Perdidas", new Color(220, 80, 80));
        plot.setSectionPaint("Pedidos", new Color(181, 13, 223));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})",
                NumberFormat.getCurrencyInstance(), NumberFormat.getPercentInstance()));

        return chart;
    }

    private static class CenterRenderer extends DefaultTableCellRenderer {
        public CenterRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setVerticalAlignment(JLabel.CENTER);
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                setBackground(row % 2 == 0 ? COLOR_TABLA_FONDO : COLOR_TABLA_ALTERNADO);
            }

            if (value instanceof String && ((String)value).startsWith("-")) {
                setForeground(Color.RED);
            } else {
                setForeground(Color.BLACK);
            }

            setFont(getFont().deriveFont(column == 0 ? Font.BOLD : Font.PLAIN));

            return this;
        }
    }

    private static class ResultadoMes {
        int mes;
        int inventarioInicial;
        int inventarioFinal;
        int ventas;
        int ventasReales;
        int ventasPerdidas;
        int pedidoPendiente;
        int mesesRestantesEntrega;
        double costoAlmacenamiento;
        double costoTotalAlmacenamiento;
        double costoVentasPerdidas;
        double costoTotalVentasPerdidas;
        double costoPedido;
        double costoTotalPedidos;
        double costoMes;
        double costoTotal;
        double randomVentas;
        double randomEntrega;
        double costoTotalPromedio;

        public ResultadoMes(int mes, int inventarioInicial, int inventarioFinal, int ventas, int ventasReales, int ventasPerdidas,
                            int pedidoPendiente, int mesesRestantesEntrega,
                            double costoAlmacenamiento, double costoTotalAlmacenamiento,
                            double costoVentasPerdidas, double costoTotalVentasPerdidas,
                            double costoPedido, double costoTotalPedidos,
                            double costoMes, double costoTotal,
                            double randomVentas, double randomEntrega, double costoTotalPromedio) {
            this.mes = mes;
            this.inventarioInicial = inventarioInicial;
            this.inventarioFinal = inventarioFinal;
            this.ventas = ventas;
            this.ventasReales = ventasReales;
            this.ventasPerdidas = ventasPerdidas;
            this.pedidoPendiente = pedidoPendiente;
            this.mesesRestantesEntrega = mesesRestantesEntrega;
            this.costoAlmacenamiento = costoAlmacenamiento;
            this.costoTotalAlmacenamiento = costoTotalAlmacenamiento;
            this.costoVentasPerdidas = costoVentasPerdidas;
            this.costoTotalVentasPerdidas = costoTotalVentasPerdidas;
            this.costoPedido = costoPedido;
            this.costoTotalPedidos = costoTotalPedidos;
            this.costoMes = costoMes;
            this.costoTotal = costoTotal;
            this.randomVentas = randomVentas;
            this.randomEntrega = randomEntrega;
            this.costoTotalPromedio = costoTotalPromedio;
        }
    }

    public class GeneradorDistribuciones {
        public static int generarTiempoEntrega(String tipoDistribucion,double RND, double[] parametros) {
            switch(tipoDistribucion.toUpperCase()) {
                case "UNIFORME":
                    if(parametros.length != 2) throw new IllegalArgumentException("Uniforme necesita 2 parámetros [A, B]");
                    double[] uniforme = Uniform.generate(parametros[0], parametros[1], 1, RND);
                    int comodin = (int) Math.round(uniforme[0]);
                    return comodin;

                case "NORMAL":
                    if(parametros.length != 2) throw new IllegalArgumentException("Normal necesita 2 parámetros [media, desviación]");
                    double[] normal = Normal.generate(parametros[0], parametros[1], 1);
                    return (int) Math.max(1, Math.min(4, Math.ceil(normal[0])));

                case "POISSON":
                    if(parametros.length != 1) throw new IllegalArgumentException("Poisson necesita 1 parámetro [lambda]");
                    double[] poisson = Poisson.generate(parametros[0], 1);
                    return (int) Math.max(1, Math.min(4, Math.ceil(poisson[0])));

                case "EXPONENCIAL":
                    if(parametros.length != 1) throw new IllegalArgumentException("Exponencial necesita 1 parámetro [lambda]");
                    double[] exponencial = Exponential.generate(parametros[0], 1);
                    return (int) Math.max(1, Math.min(4, Math.ceil(exponencial[0])));

                default:
                    throw new IllegalArgumentException("Distribución no soportada: " + tipoDistribucion);
            }
        }
    }
}
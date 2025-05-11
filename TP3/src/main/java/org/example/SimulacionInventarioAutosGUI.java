package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.example.Distribuciones.*;

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

        // Campos para distribución de ventas
        JLabel lblVentas = new JLabel("Valores de ventas (separados por comas) $:");
        lblVentas.setFont(fuenteLabels);
        lblVentas.setForeground(Color.BLACK);
        JTextField txtVentas = new JTextField("6,7,8,9,10,11");
        txtVentas.setFont(fuenteTextFields);
        panel.add(lblVentas);
        panel.add(txtVentas);

        JLabel lblFrecuencias = new JLabel("Frecuencias de ventas (separadas por comas):");
        lblFrecuencias.setFont(fuenteLabels);
        lblFrecuencias.setForeground(Color.BLACK);
        JTextField txtFrecuencias = new JTextField("3,4,6,12,9,1");
        txtFrecuencias.setFont(fuenteTextFields);
        panel.add(lblFrecuencias);
        panel.add(txtFrecuencias);

        // Campos para probabilidades de entrega
        JLabel lblProbEntrega = new JLabel("Probabilidades de entrega (suma = 1, separadas por comas):");
        lblProbEntrega.setFont(fuenteLabels);
        lblProbEntrega.setForeground(Color.BLACK);
        JTextField txtProbEntrega = new JTextField("0.44,0.33,0.16,0.07");
        txtProbEntrega.setFont(fuenteTextFields);
        panel.add(lblProbEntrega);
        panel.add(txtProbEntrega);

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

        // Configuración de distribución de tiempo de entrega
        JLabel lblDistribucion = new JLabel("Distribución para tiempo de entrega:");
        lblDistribucion.setFont(fuenteLabels);
        lblDistribucion.setForeground(Color.BLACK);
        String[] distribuciones = {"Uniforme", "Normal", "Poisson", "Exponencial"};
        JComboBox<String> comboDistribuciones = new JComboBox<>(distribuciones);
        comboDistribuciones.setFont(fuenteCombobox);
        panel.add(lblDistribucion);
        panel.add(comboDistribuciones);

        JLabel lblParametros = new JLabel("Parámetros (separados por comas):");
        lblParametros.setFont(fuenteLabels);
        lblParametros.setForeground(Color.BLACK);
        JTextField txtParametros = new JTextField("1,4");
        txtParametros.setFont(fuenteTextFields);
        panel.add(lblParametros);
        panel.add(txtParametros);

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
        int mesesRestantesEntrega = 0;
        double costoTotal = 0;

        for (int mes = 1; mes <= mesesSimular; mes++) {
            int inventarioInicial = inventario;

            if (mesesRestantesEntrega == 0 && pedidoPendiente > 0) {
                inventario += pedidoPendiente;
                pedidoPendiente = 0;
            }

            // Generar y guardar random para ventas
            double randomVentas = random.nextDouble();
            int ventas = generarVentas(randomVentas);

            int ventasReales = Math.min(ventas, inventario);
            int ventasPerdidas = ventas - ventasReales;

            inventario -= ventasReales;

            double costoAlmacen = inventario * this.costoAlmacenamiento;
            double costoVentasPerd = ventasPerdidas * this.costoVentaPerdida;
            double costoPed = 0;
            double randomEntrega = 0;

            boolean hacerPedido = (inventario <= this.puntoReorden) && (pedidoPendiente == 0);
            // Reemplazar la generación del plazo de entrega:
            if (hacerPedido) {
                costoPed = this.costoPedido;
                pedidoPendiente = this.cantidadPedido;
                randomEntrega = random.nextDouble();
                mesesRestantesEntrega = GeneradorDistribuciones.generarTiempoEntrega(
                        distribucionEntrega,
                        parametrosDistribucion
                );
            }

            if (mesesRestantesEntrega > 0) {
                mesesRestantesEntrega--;
            }

            double costoMes = costoAlmacen + costoVentasPerd + costoPed;
            costoTotal += costoMes;

            ResultadoMes resultado = new ResultadoMes(
                    mes,
                    inventarioInicial,
                    inventario,
                    ventasReales,
                    ventasPerdidas,
                    pedidoPendiente,
                    mesesRestantesEntrega,
                    costoAlmacen,
                    costoVentasPerd,
                    costoPed,
                    costoMes,
                    costoTotal,
                    randomVentas,
                    randomEntrega
            );
            resultados.add(resultado);
        }

        mostrarResultadosGUI(resultados, filaInicioMostrar, filaFinMostrar);
    }

    private int generarVentas(double randomValue) {
        double prob = randomValue;
        double acumulado = 0;
        int totalFrecuencia = 0;

        for (int f : frecuenciaVentas) {
            totalFrecuencia += f;
        }

        for (int i = 0; i < ventasCoches.length; i++) {
            acumulado += (double)frecuenciaVentas[i] / totalFrecuencia;
            if (prob <= acumulado) {
                return ventasCoches[i];
            }
        }

        return ventasCoches[ventasCoches.length - 1];
    }

    private int generarPlazoEntrega(double randomValue) {
        double prob = randomValue;
        double acumulado = 0;

        for (int i = 0; i < probabilidadEntrega.length; i++) {
            acumulado += probabilidadEntrega[i];
            if (prob <= acumulado) {
                return i + 1;
            }
        }

        return probabilidadEntrega.length;
    }

    private void mostrarResultadosGUI(List<ResultadoMes> resultados, int inicio, int fin) {
        JFrame resultadosFrame = new JFrame("Resultados de Simulación");
        resultadosFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultadosFrame.setSize(1200, 800);
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

        // Crear tabla con los resultados
        String[] columnNames = {
                "Mes", "Inventario inicial", "Inventario Final", "RND Ventas", "Ventas", "Stock Out", "Pedido", "RND Entrega", "Entrega",
                "Costo Almacenaje", "Costo Stock Out", "Costo Pedido", "Costo Mes", "Costo Total"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : Object.class;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Lato", Font.PLAIN, 17));
        table.setRowHeight(30);
        table.setGridColor(COLOR_BORDE);
        table.setBackground(COLOR_TABLA_FONDO);
        table.setSelectionBackground(new Color(200, 230, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Crear renderizador centrado para todas las celdas
        DefaultTableCellRenderer centerRenderer = new CenterRenderer();

        // Crear renderizador alineado a la derecha para columnas numéricas
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Aplicar renderizadores a las columnas
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i < 6 || i >= 11) {  // Columnas no numéricas centradas
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            } else {  // Columnas numéricas alineadas a la derecha
                table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            }
        }

        // Configurar encabezados
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_ENCABEZADO);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBackground(COLOR_ENCABEZADO);
        headerRenderer.setForeground(Color.WHITE);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Configurar anchos de columnas
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] columnWidths = {50, 80,80, 70, 70, 70, 70, 110, 110, 110, 110, 120, 100, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));

        // Llenar la tabla con los datos
        List<ResultadoMes> resultadosMostrar = new ArrayList<>();
        for (int i = inicio - 1; i < fin && i < resultados.size(); i++) {
            resultadosMostrar.add(resultados.get(i));
        }
        resultadosMostrar.add(resultados.get(resultados.size() - 1));

        for (ResultadoMes resultado : resultadosMostrar) {
            Object[] rowData = {
                    resultado.mes,
                    resultado.inventarioInicial,
                    resultado.inventarioFinal,
                    String.format("%.4f", resultado.randomVentas),
                    resultado.ventasReales,
                    resultado.ventasPerdidas,
                    resultado.pedidoPendiente,
                    resultado.randomEntrega > 0 ? String.format("%.4f", resultado.randomEntrega) : "-",
                    resultado.mesesRestantesEntrega > 0 ? resultado.mesesRestantesEntrega : "-",
                    String.format("$%,.2f", resultado.costoAlmacenamiento),
                    String.format("$%,.2f", resultado.costoVentasPerdidas),
                    String.format("$%,.2f", resultado.costoPedido),
                    String.format("$%,.2f", resultado.costoMes),
                    String.format("$%,.2f", resultado.costoTotal),
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
        // Configurar el tamaño de fuente más grande (16 puntos)
        Font fuenteGrande = new Font("Consolas", Font.PLAIN, 16);
        metricsArea.setFont(fuenteGrande);

        double costoPromedioMensual = resultados.stream()
                .mapToDouble(r -> r.costoMes)
                .average()
                .orElse(0);

        int totalVentasPerdidas = resultados.stream()
                .mapToInt(r -> r.ventasPerdidas)
                .sum();

        int totalPedidos = (int)resultados.stream()
                .filter(r -> r.costoPedido > 0)
                .count();

        double maxCostoMes = resultados.stream()
                .mapToDouble(r -> r.costoMes)
                .max()
                .orElse(0);

        double minCostoMes = resultados.stream()
                .mapToDouble(r -> r.costoMes)
                .min()
                .orElse(0);

        StringBuilder metricsText = new StringBuilder();
        metricsText.append("╔════════════════════════════════════════════╗\n");
        metricsText.append("║          MÉTRICAS DE SIMULACIÓN            ║\n");
        metricsText.append("╠══════════════════════════╦═════════════════╣\n");
        metricsText.append(String.format("║ %-24s ║ $%,14.2f ║%n", "Costo total acumulado", resultados.get(resultados.size() - 1).costoTotal));
        metricsText.append(String.format("║ %-24s ║ $%,14.2f ║%n", "Costo promedio mensual", costoPromedioMensual));
        metricsText.append(String.format("║ %-24s ║ $%,14.2f ║%n", "Costo máximo mensual", maxCostoMes));
        metricsText.append(String.format("║ %-24s ║ $%,14.2f ║%n", "Costo mínimo mensual", minCostoMes));
        metricsText.append("╠══════════════════════════╬═════════════════╣\n");
        metricsText.append(String.format("║ %-24s ║ %,15d ║%n", "Ventas perdidas totales", totalVentasPerdidas));
        metricsText.append(String.format("║ %-24s ║ %,15d ║%n", "Total de pedidos", totalPedidos));
        metricsText.append("╚══════════════════════════╩═════════════════╝\n");

        metricsArea.setText(metricsText.toString());
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
        int ventasReales;
        int ventasPerdidas;
        int pedidoPendiente;
        int mesesRestantesEntrega;
        double costoAlmacenamiento;
        double costoVentasPerdidas;
        double costoPedido;
        double costoMes;
        double costoTotal;
        double randomVentas;
        double randomEntrega;

        public ResultadoMes(int mes, int inventarioInicial, int inventarioFinal, int ventasReales, int ventasPerdidas,
                            int pedidoPendiente, int mesesRestantesEntrega,
                            double costoAlmacenamiento, double costoVentasPerdidas, double costoPedido,
                            double costoMes, double costoTotal, double randomVentas, double randomEntrega) {
            this.mes = mes;
            this.inventarioInicial = inventarioInicial;
            this.inventarioFinal = inventarioFinal;
            this.ventasReales = ventasReales;
            this.ventasPerdidas = ventasPerdidas;
            this.pedidoPendiente = pedidoPendiente;
            this.mesesRestantesEntrega = mesesRestantesEntrega;
            this.costoAlmacenamiento = costoAlmacenamiento;
            this.costoVentasPerdidas = costoVentasPerdidas;
            this.costoPedido = costoPedido;
            this.costoMes = costoMes;
            this.costoTotal = costoTotal;
            this.randomVentas = randomVentas;
            this.randomEntrega = randomEntrega;
        }
    }

    public class GeneradorDistribuciones {
        public static int generarTiempoEntrega(String tipoDistribucion, double[] parametros) {
            switch(tipoDistribucion.toUpperCase()) {
                case "UNIFORME":
                    if(parametros.length != 2) throw new IllegalArgumentException("Uniforme necesita 2 parámetros [A, B]");
                    double[] uniforme = Uniform.generate(parametros[0], parametros[1], 1);
                    return (int) Math.ceil(uniforme[0]); // Redondear hacia arriba

                case "NORMAL":
                    if(parametros.length != 2) throw new IllegalArgumentException("Normal necesita 2 parámetros [media, desviación]");
                    double[] normal = Normal.generate(parametros[0], parametros[1], 1);
                    return (int) Math.max(1, Math.min(4, Math.ceil(normal[0]))); // Asegurar entre 1 y 4

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
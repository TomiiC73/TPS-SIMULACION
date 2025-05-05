package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulacionInventarioAutosGUI {

    // Colores personalizados
    private static final Color COLOR_FONDO = new Color(240, 245, 249);
    private static final Color COLOR_ENCABEZADO = new Color(9, 126, 126);
    private static final Color COLOR_BOTON = new Color(56, 133, 138);
    private static final Color COLOR_TEXTO_BOTON = Color.WHITE;
    private static final Color COLOR_TABLA_FONDO = Color.WHITE;
    private static final Color COLOR_TABLA_ALTERNADO = new Color(240, 250, 255);
    private static final Color COLOR_METRICAS_FONDO = new Color(248, 250, 252);
    private static final Color COLOR_BORDE = new Color(241, 238, 241);

    // Distribución de ventas mensuales (Tabla 1)
    private static final int[] VENTAS_COCHES = {6, 7, 8, 9, 10, 11};
    private static final int[] FRECUENCIA_VENTAS = {3, 4, 6, 12, 9, 1};
    private static final double[] PROBABILIDAD_ENTREGA = {0.44, 0.33, 0.16, 0.07};

    // Costos
    private static final double COSTO_ALMACENAMIENTO = 6000; // por auto por mes
    private static final double COSTO_VENTA_PERDIDA = 14350; // por auto no vendido
    private static final double COSTO_PEDIDO = 25700; // por pedido

    // Política de inventario
    private static final int PUNTO_REORDEN = 12;
    private static final int CANTIDAD_PEDIDO = 20;

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
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(COLOR_FONDO);

        // Panel de entrada de parámetros
        inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(COLOR_FONDO);

        JLabel lblMeses = new JLabel("Meses a simular:");
        lblMeses.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField txtMeses = new JTextField("36");

        JLabel lblStock = new JLabel("Stock inicial:");
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField txtStock = new JTextField("25");

        JLabel lblInicio = new JLabel("Fila inicio a mostrar:");
        lblInicio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField txtInicio = new JTextField("1");

        JLabel lblFin = new JLabel("Fila fin a mostrar:");
        lblFin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField txtFin = new JTextField("36");

        inputPanel.add(lblMeses);
        inputPanel.add(txtMeses);
        inputPanel.add(lblStock);
        inputPanel.add(txtStock);
        inputPanel.add(lblInicio);
        inputPanel.add(txtInicio);
        inputPanel.add(lblFin);
        inputPanel.add(txtFin);

        JButton btnSimular = new JButton("Ejecutar Simulación");
        btnSimular.setBackground(COLOR_BOTON);
        btnSimular.setForeground(COLOR_TEXTO_BOTON);
        btnSimular.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSimular.setFocusPainted(false);
        btnSimular.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnSimular.addActionListener(e -> {
            try {
                int mesesSimular = Integer.parseInt(txtMeses.getText());
                int stockInicial = Integer.parseInt(txtStock.getText());
                int filaInicio = Integer.parseInt(txtInicio.getText());
                int filaFin = Integer.parseInt(txtFin.getText());

                ejecutarSimulacion(mesesSimular, filaInicio, filaFin, stockInicial);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Por favor ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(COLOR_FONDO);
        buttonPanel.add(btnSimular);

        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void ejecutarSimulacion(int mesesSimular, int filaInicioMostrar, int filaFinMostrar, int stockInicial) {
        List<ResultadoMes> resultados = new ArrayList<>();
        int inventario = stockInicial;
        int pedidoPendiente = 0;
        int mesesRestantesEntrega = 0;
        double costoTotal = 0;

        for (int mes = 1; mes <= mesesSimular; mes++) {
            if (mesesRestantesEntrega == 0 && pedidoPendiente > 0) {
                inventario += pedidoPendiente;
                pedidoPendiente = 0;
            }

            int ventas = generarVentas();
            int ventasReales = Math.min(ventas, inventario);
            int ventasPerdidas = ventas - ventasReales;

            inventario -= ventasReales;

            double costoAlmacenamiento = inventario * COSTO_ALMACENAMIENTO;
            double costoVentasPerdidas = ventasPerdidas * COSTO_VENTA_PERDIDA;
            double costoPedido = 0;

            boolean hacerPedido = (inventario <= PUNTO_REORDEN) && (pedidoPendiente == 0);
            if (hacerPedido) {
                costoPedido = COSTO_PEDIDO;
                pedidoPendiente = CANTIDAD_PEDIDO;
                mesesRestantesEntrega = generarPlazoEntrega();
            }

            if (mesesRestantesEntrega > 0) {
                mesesRestantesEntrega--;
            }

            double costoMes = costoAlmacenamiento + costoVentasPerdidas + costoPedido;
            costoTotal += costoMes;

            ResultadoMes resultado = new ResultadoMes(
                    mes, inventario, ventasReales, ventasPerdidas,
                    pedidoPendiente, mesesRestantesEntrega,
                    costoAlmacenamiento, costoVentasPerdidas, costoPedido, costoMes, costoTotal
            );
            resultados.add(resultado);
        }

        mostrarResultadosGUI(resultados, filaInicioMostrar, filaFinMostrar);
    }

    private int generarVentas() {
        double prob = random.nextDouble();
        double acumulado = 0;
        int totalFrecuencia = 0;

        for (int f : FRECUENCIA_VENTAS) {
            totalFrecuencia += f;
        }

        for (int i = 0; i < VENTAS_COCHES.length; i++) {
            acumulado += (double)FRECUENCIA_VENTAS[i] / totalFrecuencia;
            if (prob <= acumulado) {
                return VENTAS_COCHES[i];
            }
        }

        return VENTAS_COCHES[VENTAS_COCHES.length - 1];
    }

    private int generarPlazoEntrega() {
        double prob = random.nextDouble();
        double acumulado = 0;

        for (int i = 0; i < PROBABILIDAD_ENTREGA.length; i++) {
            acumulado += PROBABILIDAD_ENTREGA[i];
            if (prob <= acumulado) {
                return i + 1;
            }
        }

        return 4;
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

        JLabel titleLabel = new JLabel("RESULTADOS DE SIMULACIÓN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        resultadosFrame.add(titlePanel, BorderLayout.NORTH);

        // Crear tabla con los resultados
        String[] columnNames = {
                "Mes", "Inventario", "Ventas", "P.Perd", "Pedido", "Entrega",
                "Costo Alm.", "Costo Perd.", "Costo Pedido", "Costo Mes", "Costo Total"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : Object.class;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
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
            // Las primeras 6 columnas (de Mes a Entrega) centradas
            if (i < 6) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            // Las columnas de costos alineadas a la derecha
            else {
                table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            }
        }

        // Centrar también los encabezados de columna
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
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // Mes
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Inventario
        table.getColumnModel().getColumn(2).setPreferredWidth(70);  // Ventas
        table.getColumnModel().getColumn(3).setPreferredWidth(70);  // P.Perd
        table.getColumnModel().getColumn(4).setPreferredWidth(70);  // Pedido
        table.getColumnModel().getColumn(5).setPreferredWidth(70);  // Entrega
        table.getColumnModel().getColumn(6).setPreferredWidth(110); // Costo Alm.
        table.getColumnModel().getColumn(7).setPreferredWidth(110); // Costo Perd.
        table.getColumnModel().getColumn(8).setPreferredWidth(110); // Costo Pedido
        table.getColumnModel().getColumn(9).setPreferredWidth(110); // Costo Mes
        table.getColumnModel().getColumn(10).setPreferredWidth(120); // Costo Total

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));

        // Llenar la tabla con los datos
        for (ResultadoMes resultado : resultados) {
            Object[] rowData = {
                    resultado.mes,
                    resultado.inventario,
                    resultado.ventasReales,
                    resultado.ventasPerdidas,
                    resultado.pedidoPendiente,
                    resultado.mesesRestantesEntrega > 0 ? resultado.mesesRestantesEntrega : "-",
                    String.format("$%,.2f", resultado.costoAlmacenamiento),
                    String.format("$%,.2f", resultado.costoVentasPerdidas),
                    String.format("$%,.2f", resultado.costoPedido),
                    String.format("$%,.2f", resultado.costoMes),
                    String.format("$%,.2f", resultado.costoTotal)
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

            // Alternar colores de filas
            if (!isSelected) {
                if (row % 2 == 0) {
                    setBackground(COLOR_TABLA_FONDO);
                } else {
                    setBackground(COLOR_TABLA_ALTERNADO);
                }
            }

            // Cambiar color para valores negativos (si los hubiera)
            if (value instanceof String && ((String)value).startsWith("-")) {
                setForeground(Color.RED);
            } else {
                setForeground(Color.BLACK);
            }

            // Estilo diferente para la primera columna (mes)
            if (column == 0) {
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                setFont(getFont().deriveFont(Font.PLAIN));
            }

            return this;
        }
    }

    private static class ResultadoMes {
        int mes;
        int inventario;
        int ventasReales;
        int ventasPerdidas;
        int pedidoPendiente;
        int mesesRestantesEntrega;
        double costoAlmacenamiento;
        double costoVentasPerdidas;
        double costoPedido;
        double costoMes;
        double costoTotal;

        public ResultadoMes(int mes, int inventario, int ventasReales, int ventasPerdidas,
                            int pedidoPendiente, int mesesRestantesEntrega,
                            double costoAlmacenamiento, double costoVentasPerdidas,
                            double costoPedido, double costoMes, double costoTotal) {
            this.mes = mes;
            this.inventario = inventario;
            this.ventasReales = ventasReales;
            this.ventasPerdidas = ventasPerdidas;
            this.pedidoPendiente = pedidoPendiente;
            this.mesesRestantesEntrega = mesesRestantesEntrega;
            this.costoAlmacenamiento = costoAlmacenamiento;
            this.costoVentasPerdidas = costoVentasPerdidas;
            this.costoPedido = costoPedido;
            this.costoMes = costoMes;
            this.costoTotal = costoTotal;
        }
    }
}
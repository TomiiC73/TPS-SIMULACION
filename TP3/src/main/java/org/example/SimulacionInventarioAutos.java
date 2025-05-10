package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulacionInventarioAutos {

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

    public static void main(String[] args) {

        //int mesesSimular = Integer.parseInt(args[0]);
        //int filaInicioMostrar = Integer.parseInt(args[1]);
        //int filaFinMostrar = Integer.parseInt(args[2]);
        //int stockInicial = Integer.parseInt(args[3]);

        int mesesSimular = 36;
        int filaInicioMostrar = 1;
        int filaFinMostrar = 33;
        int stockInicial = 25;

        SimulacionInventarioAutos simulacion = new SimulacionInventarioAutos();
        simulacion.ejecutarSimulacion(mesesSimular, filaInicioMostrar, filaFinMostrar, stockInicial);
    }

    public void ejecutarSimulacion(int mesesSimular, int filaInicioMostrar, int filaFinMostrar, int stockInicial) {
        List<ResultadoMes> resultados = new ArrayList<>();
        int inventario = stockInicial;
        int pedidoPendiente = 0;
        int mesesRestantesEntrega = 0;
        double costoTotal = 0;

        System.out.println(String.format("%-4s | %-10s | %-6s | %-6s | %-6s | %-7s | %-12s | %-12s | %-12s | %-12s",
                "Mes", "Inventario", "Ventas", "P.Perd", "Pedido", "Entrega",
                "Costo Alm.", "Costo Perd.", "Costo Pedido", "Costo Mes"));
        System.out.println("-----|------------|--------|--------|--------|---------|--------------|--------------|--------------|-------------|");

        for (int mes = 1; mes <= mesesSimular; mes++) {
            // 1. Verificar si llega pedido
            if (mesesRestantesEntrega == 0 && pedidoPendiente > 0) {
                inventario += pedidoPendiente;
                pedidoPendiente = 0;
            }

            // 2. Generar ventas del mes
            int ventas = generarVentas();
            int ventasReales = Math.min(ventas, inventario);
            int ventasPerdidas = ventas - ventasReales;

            // 3. Actualizar inventario
            inventario -= ventasReales;

            // 4. Calcular costos
            double costoAlmacenamiento = inventario * COSTO_ALMACENAMIENTO;
            double costoVentasPerdidas = ventasPerdidas * COSTO_VENTA_PERDIDA;
            double costoPedido = 0;

            // 5. Decidir si hacer pedido
            boolean hacerPedido = (inventario <= PUNTO_REORDEN) && (pedidoPendiente == 0);
            if (hacerPedido) {
                costoPedido = COSTO_PEDIDO;
                pedidoPendiente = CANTIDAD_PEDIDO;
                mesesRestantesEntrega = generarPlazoEntrega();
            }

            // 6. Actualizar meses restantes para entrega
            if (mesesRestantesEntrega > 0) {
                mesesRestantesEntrega--;
            }

            // 7. Calcular costo total del mes
            double costoMes = costoAlmacenamiento + costoVentasPerdidas + costoPedido;
            costoTotal += costoMes;

            // 8. Guardar resultados
            ResultadoMes resultado = new ResultadoMes(
                    mes, inventario, ventasReales, ventasPerdidas,
                    pedidoPendiente, mesesRestantesEntrega,
                    costoAlmacenamiento, costoVentasPerdidas, costoPedido, costoMes, costoTotal
            );
            resultados.add(resultado);
        }

        // Mostrar resultados según parámetros
        mostrarResultados(resultados, filaInicioMostrar, filaFinMostrar);

        // Generar métricas y gráficos (simplificado)
        generarMetricas(resultados);
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
                return i + 1; // Los plazos son de 1 a 4 meses
            }
        }

        return 4;
    }

    private void mostrarResultados(List<ResultadoMes> resultados, int inicio, int fin) {
        // Mostrar filas solicitadas
        for (int i = inicio - 1; i < fin && i < resultados.size(); i++) {
            System.out.println(resultados.get(i));
        }

        // Mostrar siempre la última fila
        if (resultados.size() > 0 && (fin < resultados.size() || inicio > resultados.size())) {
            System.out.println("\nÚltima fila de la simulación:");
            System.out.println(resultados.get(resultados.size() - 1));
        }
    }

    private void generarMetricas(List<ResultadoMes> resultados) {
        // Aquí iría la lógica para generar métricas y gráficos
        // Esto es un ejemplo simplificado

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

        System.out.println("\nMétricas importantes:");
        System.out.printf("Costo total acumulado: $%,.2f%n", resultados.get(resultados.size() - 1).costoTotal);
        System.out.printf("Costo promedio mensual: $%,.2f%n", costoPromedioMensual);
        System.out.printf("Ventas perdidas totales: %d autos%n", totalVentasPerdidas);
        System.out.printf("Total de pedidos realizados: %d%n", totalPedidos);

        // En una implementación real, aquí podrías generar gráficos usando una biblioteca como JFreeChart
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

        @Override
        public String toString() {
            return String.format("%-4d | %-10d | %-6d | %-6d | %-6d | %-7s | %-,12.2f | %-,12.2f | %-,12.2f | %-,12.2f",
                    mes,
                    inventario,
                    ventasReales,
                    ventasPerdidas,
                    pedidoPendiente,
                    mesesRestantesEntrega > 0 ? mesesRestantesEntrega : "-",
                    costoAlmacenamiento,
                    costoVentasPerdidas,
                    costoPedido,
                    costoMes);
        }
    }
}
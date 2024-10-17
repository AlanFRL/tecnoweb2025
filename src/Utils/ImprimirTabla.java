package Utils;

import java.util.List;

public class ImprimirTabla {

    // Método público para mostrar la tabla
    public String mostrarTabla(List<String[]> data) {
        if (data == null || data.isEmpty()) {
            return "No hay datos para mostrar.";
        }

        StringBuilder sb = new StringBuilder();

        // Calcular el ancho de las columnas
        int[] columnWidths = new int[data.get(0).length];
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], row[i].length());
            }
        }

        // Imprimir la tabla
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                sb.append(padRight(row[i], columnWidths[i])).append(" | ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String padRight(String text, int length) {
        return String.format("%-" + length + "s", text);
    }
}

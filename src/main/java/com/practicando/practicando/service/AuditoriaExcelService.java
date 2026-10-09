
package com.practicando.practicando.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.practicando.practicando.model.Auditoria;

@Service
public class AuditoriaExcelService {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public byte[] generarExcel(List<Auditoria> auditorias)
            throws IOException {

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream salida =
                        new ByteArrayOutputStream()
        ) {

            Sheet hoja = workbook.createSheet("Auditoria");

            // ESTILO DEL ENCABEZADO
            CellStyle estiloEncabezado =
                    workbook.createCellStyle();

            Font fuente = workbook.createFont();
            fuente.setBold(true);
            fuente.setColor(IndexedColors.WHITE.getIndex());

            estiloEncabezado.setFont(fuente);

            estiloEncabezado.setFillForegroundColor(
                    IndexedColors.DARK_BLUE.getIndex()
            );

            estiloEncabezado.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND
            );

            // ENCABEZADOS
            String[] columnas = {
                    "ID",
                    "Usuario",
                    "Acción",
                    "Módulo",
                    "Descripción",
                    "Fecha"
            };

            Row encabezado = hoja.createRow(0);

            for (int i = 0; i < columnas.length; i++) {

                Cell celda = encabezado.createCell(i);
                celda.setCellValue(columnas[i]);
                celda.setCellStyle(estiloEncabezado);
            }

            // DATOS
            int numeroFila = 1;

            for (Auditoria auditoria : auditorias) {

                Row fila = hoja.createRow(numeroFila++);

                fila.createCell(0).setCellValue(
                        auditoria.getId()
                );

                fila.createCell(1).setCellValue(
                        textoSeguro(auditoria.getUsername())
                );

                fila.createCell(2).setCellValue(
                        textoSeguro(auditoria.getAccion())
                );

                fila.createCell(3).setCellValue(
                        textoSeguro(auditoria.getModulo())
                );

                fila.createCell(4).setCellValue(
                        textoSeguro(auditoria.getDescripcion())
                );

                LocalDateTime fecha = auditoria.getFecha();

                fila.createCell(5).setCellValue(
                        fecha != null
                                ? fecha.format(FORMATO_FECHA)
                                : ""
                );
            }

            // ANCHO DE COLUMNAS
            hoja.setColumnWidth(0, 12 * 256);
            hoja.setColumnWidth(1, 22 * 256);
            hoja.setColumnWidth(2, 28 * 256);
            hoja.setColumnWidth(3, 20 * 256);
            hoja.setColumnWidth(4, 55 * 256);
            hoja.setColumnWidth(5, 23 * 256);

            // MANTENER VISIBLE EL ENCABEZADO
            hoja.createFreezePane(0, 1);

            // ACTIVAR FILTROS DE EXCEL
            hoja.setAutoFilter(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            0,
                            Math.max(0, numeroFila - 1),
                            0,
                            columnas.length - 1
                    )
            );

            workbook.write(salida);

            return salida.toByteArray();
        }
    }

    // Evita que Excel interprete datos de texto
    // como fórmulas al abrir el archivo.
    private String textoSeguro(String valor) {

        if (valor == null) {
            return "";
        }

        String texto = valor.stripLeading();

        if (!texto.isEmpty()
                && "=+-@\t\r\n".indexOf(texto.charAt(0)) >= 0) {

            return "'" + valor;
        }

        return valor;
    }
}

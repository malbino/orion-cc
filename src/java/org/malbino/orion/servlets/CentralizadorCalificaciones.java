/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.servlets;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.malbino.orion.pojos.centralizador.Centralizador;
import org.malbino.orion.pojos.centralizador.EstudianteCentralizador;
import org.malbino.orion.pojos.centralizador.NotaCentralizador;

/**
 *
 * @author tincho
 */
@WebServlet(
        name = "CentralizadorCalificaciones",
        urlPatterns = {
            "/reportes/centralizadorCalificaciones/CentralizadorCalificaciones"
        }
)
public class CentralizadorCalificaciones extends HttpServlet {

    private static final String CONTENIDO_PDF = "application/pdf";

    private static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
    private static final Font SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
    private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA, 6, Font.BOLD, BaseColor.BLACK);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);
    private static final Font NEGRITA_PEQUENA = FontFactory.getFont(FontFactory.HELVETICA, 4, Font.BOLD, BaseColor.BLACK);

    //8.5 x 13 pulgadas (1 pulgada = 72 puntos)
    private static final Rectangle OFICIO_FOLIO = new Rectangle(612, 936);
    private static final int MARGEN_IZQUIERDO = -80;
    private static final int MARGEN_DERECHO = -80;
    private static final int MARGEN_SUPERIOR = 30;
    private static final int MARGEN_INFERIOR = 30;

    private static final String TITULO_CC = "CENTRALIZADOR DE CALIFICACIONES";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.generarPDF(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.generarPDF(request, response);
    }

    public void generarPDF(HttpServletRequest request, HttpServletResponse response) {
        Centralizador centralizador = (Centralizador) request.getSession().getAttribute("centralizador");

        if (centralizador != null) {
            try {
                response.setContentType(CONTENIDO_PDF);

                Document document = new Document(OFICIO_FOLIO.rotate(), MARGEN_IZQUIERDO, MARGEN_DERECHO, MARGEN_SUPERIOR, MARGEN_INFERIOR);
                PdfWriter.getInstance(document, response.getOutputStream());

                generarDocumento(document, centralizador);
            } catch (IOException | DocumentException ex) {

            }
        }

    }

    public void generarDocumento(Document document, Centralizador centralizador) throws DocumentException, BadElementException, IOException {
        document.open();

        int columnasFijas = 11;
        int columnasmodulos = centralizador.getModulosCentralizador().length * 2;
        int columnas = columnasFijas + columnasmodulos;

        PdfPTable table = new PdfPTable(columnas);

        PdfPCell cell = new PdfPCell(new Phrase(centralizador.getInstituto(), TITULO));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(242, 242, 242));
        cell.setColspan(columnas);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("DIRECCIÓN ACADÉMICA", TITULO));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(columnas);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(TITULO_CC + " - " + centralizador.getGestion(), TITULO));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(columnas);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("CARRERA:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setColspan(columnasFijas);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(centralizador.getCarrera(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setColspan(columnasmodulos);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("CAMPUS:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setColspan(columnasFijas);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(centralizador.getCampus(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setColspan(columnasmodulos);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("TURNO:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setColspan(columnasFijas);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(centralizador.getTurno(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setColspan(columnasmodulos);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("PARALELO:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setColspan(columnasFijas);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(centralizador.getParalelo(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setColspan(columnasmodulos);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("N°", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBackgroundColor(new BaseColor(242, 242, 242));
        cell.setColspan(1);
        cell.setRowspan(2);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("APELLIDOS Y NOMBRES", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBackgroundColor(new BaseColor(242, 242, 242));
        cell.setColspan(10);
        cell.setRowspan(2);
        table.addCell(cell);

        for (String moduloCentralizador : centralizador.getModulosCentralizador()) {
            cell = new PdfPCell(new Phrase(moduloCentralizador, NEGRITA));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell.setBackgroundColor(new BaseColor(242, 242, 242));
            cell.setColspan(2);
            cell.setRotation(90);
            table.addCell(cell);
        }

        for (String moduloCentralizador : centralizador.getModulosCentralizador()) {
            cell = new PdfPCell(new Phrase("NF", NEGRITA));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setBackgroundColor(new BaseColor(242, 242, 242));
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("RE", NEGRITA));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setBackgroundColor(new BaseColor(242, 242, 242));
            cell.setColspan(1);
            table.addCell(cell);
        }

        for (EstudianteCentralizador estudianteCentralizador : centralizador.getEstudiantesCentralizador()) {
            cell = new PdfPCell(new Phrase(estudianteCentralizador.getNumero().toString(), NORMAL));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell.setBackgroundColor(new BaseColor(220, 230, 242));
            cell.setColspan(1);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(estudianteCentralizador.getEstudiante(), NORMAL));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            cell.setBackgroundColor(new BaseColor(220, 230, 242));
            cell.setColspan(10);
            table.addCell(cell);

            for (NotaCentralizador notaCentralizador : estudianteCentralizador.getNotasCentralizador()) {
                cell = new PdfPCell(new Phrase(notaCentralizador.notaFinal(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBackgroundColor(new BaseColor(220, 230, 242));
                cell.setColspan(1);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(notaCentralizador.recuperatorio(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBackgroundColor(new BaseColor(220, 230, 242));
                cell.setColspan(1);
                table.addCell(cell);
            }
        }

        document.add(table);

        document.close();
    }
}

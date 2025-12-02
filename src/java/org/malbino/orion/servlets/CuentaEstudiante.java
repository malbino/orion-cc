/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.servlets;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.Estudiante;

/**
 *
 * @author tincho
 */
@WebServlet(
        name = "CuentaEstudiante",
        urlPatterns = {
            "/inscripciones/cambioCarrera/CuentaEstudiante",
            "/inscripciones/estudianteNuevo/CuentaEstudiante",
            "/inscripciones/estudianteRegular/CuentaEstudiante"
        }
)
public class CuentaEstudiante extends HttpServlet {

    private static final String CONTENIDO_PDF = "application/pdf";

    private static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
    private static final Font SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
    private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);

    private static final Font BOLDITALIC = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLDITALIC, BaseColor.BLACK);
    private static final Font ITALIC = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.ITALIC, BaseColor.BLACK);

    private static final int MARGEN_IZQUIERDO = 0;
    private static final int MARGEN_DERECHO = -40;
    private static final int MARGEN_SUPERIOR = 30;
    private static final int MARGEN_INFERIOR = 30;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.generarPDF(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.generarPDF(request, response);
    }

    public void generarPDF(HttpServletRequest request, HttpServletResponse response) {
        Estudiante estudiante = (Estudiante) request.getSession().getAttribute("est");
        Carrera carrera = (Carrera) request.getSession().getAttribute("car");

        if (estudiante != null && carrera != null) {
            try {
                response.setContentType(CONTENIDO_PDF);

                Document document = new Document(PageSize.HALFLETTER.rotate(), MARGEN_IZQUIERDO, MARGEN_DERECHO, MARGEN_SUPERIOR, MARGEN_INFERIOR);
                PdfWriter.getInstance(document, response.getOutputStream());

                document.open();

                document.add(titulo(estudiante, carrera));
                document.add(cuenta(request, estudiante));

                document.close();
            } catch (IOException | DocumentException ex) {
                Logger.getLogger(CuentaEstudiante.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public PdfPTable titulo(Estudiante estudiante, Carrera carrera) throws BadElementException, IOException {
        PdfPTable table = new PdfPTable(100);

        //cabecera
        String realPath = getServletContext().getRealPath("/resources/uploads/" + carrera.getInstituto().getLogo()
        );
        Image image = Image.getInstance(realPath);
        image.scaleToFit(70, 70);
        image.setAlignment(Image.ALIGN_CENTER);
        PdfPCell cell = new PdfPCell();
        cell.addElement(image);
        cell.setRowspan(4);
        cell.setColspan(20);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("CUENTA ESTUDIANTE,", TITULO));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setColspan(80);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(estudiante.toString(), SUBTITULO));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setColspan(80);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(carrera.toString(), SUBTITULO));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setColspan(80);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        return table;
    }

    public PdfPTable cuenta(HttpServletRequest request, Estudiante estudiante) throws BadElementException, IOException {
        PdfPTable table = new PdfPTable(100);

        Phrase phrase = new Phrase();
        phrase.add(new Chunk("INSTRUCCIONES DE USO", BOLDITALIC));
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(100);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        phrase = new Phrase();
        phrase.add(new Chunk(" ", BOLDITALIC));
        cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(100);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        phrase = new Phrase();
        phrase.add(new Chunk("Para ingresar al sistema:\n\n", ITALIC));
        phrase.add(new Chunk("1) Abre un navegador web y dirigete a la siguiente dirección ", ITALIC));

        String requestURL = request.getRequestURL().toString().replaceAll(request.getRequestURI(), "");
        phrase.add(new Chunk(requestURL + "\n", BOLDITALIC));

        phrase.add(new Chunk("2) Ingresa tu usuario y contraseña\n\n", ITALIC));
        phrase.add(new Chunk("Usuario: ", BOLDITALIC));
        phrase.add(new Chunk(estudiante.getUsuario() + "\n", ITALIC));
        phrase.add(new Chunk("Contraseña: ", BOLDITALIC));
        phrase.add(new Chunk(estudiante.getContrasenaSinEncriptar() + "\n\n", ITALIC));
        phrase.add(new Chunk("3) Utiliza el menu para acceder a las opciones del sistema\n\n", ITALIC));
        phrase.add(new Chunk(
                "En el sistema pordras realizar tu Inscripción por Internet, revisar tu Historial Académico, "
                + "revisar tu Historial Económico y actualizar tus datos personales.",
                BOLDITALIC));
        cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(100);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        return table;
    }
}

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
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.malbino.orion.entities.Cuota;
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Nota;
import org.malbino.orion.enums.ModalidadEvaluacion;
import org.malbino.orion.facades.NotaFacade;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author tincho
 */
@WebServlet(
        name = "FichaInscripcion",
        urlPatterns = {
            "/inscripciones/cambioCarrera/FichaInscripcion",
            "/inscripciones/estudianteNuevo/FichaInscripcion",
            "/inscripciones/estudianteRegular/FichaInscripcion"
        }
)
public class FichaInscripcion extends HttpServlet {

    private static final String CONTENIDO_PDF = "application/pdf";

    private static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.NORMAL, BaseColor.BLACK);
    private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
    private static final Font NORMAL_NORMALA = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);

    private static final int MARGEN_IZQUIERDO = -40;
    private static final int MARGEN_DERECHO = -40;
    private static final int MARGEN_SUPERIOR = 40;
    private static final int MARGEN_INFERIOR = 40;

    @EJB
    NotaFacade notaFacade;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.generarPDF(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.generarPDF(request, response);
    }

    public void generarPDF(HttpServletRequest request, HttpServletResponse response) {
        Inscrito inscrito = (Inscrito) request.getSession().getAttribute("inscrito");

        if (inscrito != null) {
            try {
                response.setContentType(CONTENIDO_PDF);

                Document document = new Document(PageSize.LETTER, MARGEN_IZQUIERDO, MARGEN_DERECHO, MARGEN_SUPERIOR, MARGEN_INFERIOR);
                PdfWriter.getInstance(document, response.getOutputStream());

                document.open();

                document.add(cabecera(inscrito));
                if (inscrito.getGestionAcademica().getModalidadEvaluacion().equals(ModalidadEvaluacion.MODULAR_2P)) {
                    document.add(modulos(inscrito));
                }
                document.add(cuotas(inscrito));
                document.add(cuenta(inscrito, request));
                document.add(pie(inscrito));

                document.close();
            } catch (IOException | DocumentException ex) {

            }
        }
    }

    public PdfPTable cabecera(Inscrito inscrito) throws BadElementException, IOException {
        PdfPTable table = new PdfPTable(100);

        PdfPCell cell = new PdfPCell(new Phrase("FICHA DE INSCRIPCIÓN", TITULO));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(80);
        table.addCell(cell);

        String realPath = getServletContext().getRealPath("/resources/uploads/" + inscrito.getCarrera().getInstituto().getLogo());
        Image image = Image.getInstance(realPath);
        image.scaleToFit(40, 40);
        image.setAlignment(Image.ALIGN_CENTER);
        cell = new PdfPCell();
        cell.addElement(image);
        cell.setColspan(20);
        cell.setRowspan(2);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(inscrito.getCarrera().getInstituto().getNombre(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(80);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(100);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        //fila 1
        cell = new PdfPCell(new Phrase("Matricula:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(33);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Estudiante:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(34);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Carrera:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(33);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(String.valueOf(inscrito.getEstudiante().getMatricula()), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(33);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(inscrito.getEstudiante().toString(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(34);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(inscrito.getCarrera().toString(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(33);
        table.addCell(cell);

        //fial 2
        cell = new PdfPCell(new Phrase("Campus:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(33);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Gestión Académica:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(34);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(33);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(inscrito.getCampus().toString(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(33);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(inscrito.getGestionAcademica().codigo(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(34);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(33);
        table.addCell(cell);

        return table;
    }

    public PdfPTable cuenta(Inscrito inscrito, HttpServletRequest request) throws BadElementException, IOException {
        PdfPTable table = new PdfPTable(100);

        PdfPCell cell = new PdfPCell(new Phrase("CUENTA", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(100);
        cell.setPaddingTop(8);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("INSTRUCCIONES DE USO", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(100);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        Phrase phrase = new Phrase();
        phrase.add(new Chunk("Para ingresar al sistema:\n", NORMAL));
        phrase.add(new Chunk("1) Abre un navegador web y dirigete a la siguiente dirección ", NORMAL));

        String requestURL = request.getRequestURL().toString().replaceAll(request.getRequestURI(), "");
        phrase.add(new Chunk(requestURL + "\n", NEGRITA));

        phrase.add(new Chunk("2) Ingresa tu ", NORMAL));
        phrase.add(new Chunk("Usuario: ", NEGRITA));
        phrase.add(new Chunk(inscrito.getEstudiante().getUsuario() + " ", NORMAL));
        phrase.add(new Chunk("y ", NORMAL));
        phrase.add(new Chunk("Contraseña: ", NEGRITA));
        phrase.add(new Chunk(inscrito.getEstudiante().getContrasenaSinEncriptar() + "\n", NORMAL));
        phrase.add(new Chunk("3) Utiliza el menu para acceder a las opciones del sistema", NORMAL));
        cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(100);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(100);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        return table;
    }

    public PdfPTable modulos(Inscrito inscrito) throws BadElementException, IOException {
        PdfPTable table = new PdfPTable(100);

        PdfPCell cell = new PdfPCell(new Phrase("MODULOS", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(100);
        cell.setPaddingTop(8);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Codigo", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(20);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Nombre", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(50);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Horas", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Prerequisitos", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(20);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        List<Nota> notas = notaFacade.listaNotas(inscrito.getId_inscrito());
        for (int i = 0; i < notas.size(); i++) {
            Nota nota = notas.get(i);

            if (i % 2 == 0) {
                cell = new PdfPCell(new Phrase(nota.getModulo().getCodigo(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(20);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(nota.getModulo().getNombre(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(50);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(nota.getModulo().getHoras().toString(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(nota.getModulo().prerequisitosToString(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(20);
                table.addCell(cell);
            } else {
                cell = new PdfPCell(new Phrase(nota.getModulo().getCodigo(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(20);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(nota.getModulo().getNombre(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(50);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(nota.getModulo().getHoras().toString(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(nota.getModulo().prerequisitosToString(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(20);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

            }
        }

        cell = new PdfPCell(new Phrase("Totales:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(70);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(inscrito.horasNotas(), NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(20);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        return table;
    }

    public PdfPTable cuotas(Inscrito inscrito) throws BadElementException, IOException {
        PdfPTable table = new PdfPTable(100);

        PdfPCell cell = new PdfPCell(new Phrase("CUOTAS", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(100);
        cell.setPaddingTop(8);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Codigo", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Unidad Medida", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(15);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Descripcion", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(20);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Monto\n(bs.)", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Pagado\n(bs.)", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Adeudado\n(bs.)", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Fecha de Vencimiento", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Condicion", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(15);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        List<Cuota> cuotas = inscrito.getCuotas();
        for (int i = 0; i < cuotas.size(); i++) {
            Cuota cuota = cuotas.get(i);

            if (i % 2 == 0) {
                cell = new PdfPCell(new Phrase(cuota.getCodigo(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.getUnidadMedida(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(15);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.getDescripcion(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(20);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.monto(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.pagado(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.adeudado(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.fechaVencimiento(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.condicion().name(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(15);
                table.addCell(cell);
            } else {
                cell = new PdfPCell(new Phrase(cuota.getCodigo(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.getUnidadMedida(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(15);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.getDescripcion(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(20);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.monto(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.pagado(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.adeudado(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.fechaVencimiento(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(10);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(cuota.condicion().name(), NORMAL));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(15);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }
        }

        cell = new PdfPCell(new Phrase("Totales:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(45);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(inscrito.montoCuotas(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(inscrito.pagadoCuotas(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(inscrito.adeudadoCuotas(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(25);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        return table;
    }

    public PdfPTable pie(Inscrito inscrito) throws BadElementException, IOException {
        PdfPTable table = new PdfPTable(100);

        PdfPCell cell = new PdfPCell(new Phrase(Fecha.getFecha_ddMMMMyyyy(), NORMAL_NORMALA));
        cell.setColspan(100);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(60f);
        cell.setPaddingTop(8);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(100);
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Cualquier raspadura o enmienda invalida el presente documento", NORMAL_NORMALA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(100);
        table.addCell(cell);

        return table;
    }

}

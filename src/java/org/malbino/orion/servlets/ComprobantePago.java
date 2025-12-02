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
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.malbino.orion.entities.Comprobante;
import org.malbino.orion.entities.Detalle;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.facades.ComprobanteFacade;
import org.malbino.orion.facades.DetalleFacade;
import org.malbino.orion.util.Encriptador;
import org.malbino.orion.util.Redondeo;

/**
 *
 * @author tincho
 */
@WebServlet(
        name = "ComprobantePago",
        urlPatterns = {
            "/cajas/comprobanteConcepto/ComprobantePago",
            "/cajas/comprobanteCuota/ComprobantePago",
            "/cajas/comprobantes/ComprobantePago",
            "/fileEstudiante/historialEconomico/comprobantePago"
        }
)
public class ComprobantePago extends HttpServlet {

    private static final String CONTENIDO_PDF = "application/pdf";

    private static final Font TITULO = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLD, BaseColor.BLACK);
    private static final Font NEGRITA = FontFactory.getFont(FontFactory.HELVETICA, 5, Font.BOLD, BaseColor.BLACK);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 5, Font.NORMAL, BaseColor.BLACK);

    private static final int MARGEN_IZQUIERDO = -20;
    private static final int MARGEN_DERECHO = -20;
    private static final int MARGEN_SUPERIOR = 20;
    private static final int MARGEN_INFERIOR = 20;

    @EJB
    ComprobanteFacade comprobanteFacade;
    @EJB
    DetalleFacade detalleFacade;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.generarPDF(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.generarPDF(request, response);
    }

    public void generarPDF(HttpServletRequest request, HttpServletResponse response) {
        Integer id_comprobante = (Integer) request.getSession().getAttribute("id_comprobante");
        Estudiante estudiante = (Estudiante) request.getSession().getAttribute("est");

        if (id_comprobante != null) {
            Comprobante comprobante = comprobanteFacade.find(id_comprobante);
            try {
                response.setContentType(CONTENIDO_PDF);

                Document document = new Document(PageSize.HALFLETTER, MARGEN_IZQUIERDO, MARGEN_DERECHO, MARGEN_SUPERIOR, MARGEN_INFERIOR);
                PdfWriter.getInstance(document, response.getOutputStream());

                document.open();

                document.add(comprobante(comprobante, estudiante));

                document.close();
            } catch (IOException | DocumentException ex) {
                Logger.getLogger(ComprobantePago.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public PdfPTable comprobante(Comprobante comprobante, Estudiante estudiante) throws BadElementException, IOException {
        PdfPTable table = new PdfPTable(100);

        //cabecera
        // datos instituto
        PdfPTable subtable = new PdfPTable(1);
        PdfPCell cell = new PdfPCell(new Phrase(comprobante.getInscrito().getCarrera().getInstituto().getNombre(), NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.getInscrito().getCampus().getCiudad(), NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.getInscrito().getCampus().getDireccion(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("Telefono: " + comprobante.getInscrito().getCampus().getTelefono(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("Celular: " + comprobante.getInscrito().getCampus().getCelular(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(subtable);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(30);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        // espacio
        subtable = new PdfPTable(1);
        cell = new PdfPCell(new Phrase(" ", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(subtable);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(35);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        // datos comprobante
        subtable = new PdfPTable(100);
        cell = new PdfPCell(new Phrase("COMPROBANTE Nº", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(50);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.getNumero().toString(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(50);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("CÓD. COMPROBANTE", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(50);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.getCodigo().toString(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(50);
        subtable.addCell(cell);

        cell = new PdfPCell(subtable);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(35);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        // titulo
        subtable = new PdfPTable(1);
        cell = new PdfPCell(new Phrase(" ", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("COMPROBANTE DE PAGO", TITULO));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("(Sin Derecho a Crédito Fiscal)", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(" ", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(subtable);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(100);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        // datos cliente
        subtable = new PdfPTable(200);
        cell = new PdfPCell(new Phrase("Fecha:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(40);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.fecha_ddMMyyyy(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(60);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("Registro:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(70);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.getInscrito().getCodigo().toString(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(30);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("Nombre Estudiante:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(40);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.getInscrito().getEstudiante().toString(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(60);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("Matricula:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(70);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.getInscrito().getEstudiante().getMatricula().toString(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(30);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("Carrera:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(40);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.getInscrito().getCarrera().toString(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(60);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("Gestion Academica:", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(70);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.getInscrito().getGestionAcademica().toString(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setColspan(30);
        subtable.addCell(cell);

        cell = new PdfPCell(subtable);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(100);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        // detalles
        subtable = new PdfPTable(100);

        cell = new PdfPCell(new Phrase(" ", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setColspan(100);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("CÓDIGO SERVICIO", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("CANTIDAD", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("UNIDAD DE MEDIDA", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(10);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("DESCRIPCIÓN", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(34);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("PRECIO UNITARIO", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(12);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("DESCUENTO", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(12);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("SUBTOTAL", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(12);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        subtable.addCell(cell);

        List<Detalle> detalles = detalleFacade.listaDetalles(comprobante.getId_comprobante());
        for (int i = 0; i < detalles.size(); i++) {
            Detalle detalle = detalles.get(i);

            cell = new PdfPCell(new Phrase(detalle.getCodigo(), NORMAL));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setColspan(10);
            subtable.addCell(cell);

            cell = new PdfPCell(new Phrase(Redondeo.formatear_csm(detalle.getCantidad()), NORMAL));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            cell.setColspan(10);
            subtable.addCell(cell);

            cell = new PdfPCell(new Phrase(detalle.getUnidadMedida(), NORMAL));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setColspan(10);
            subtable.addCell(cell);

            cell = new PdfPCell(new Phrase(detalle.getDescripcion(), NORMAL));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setColspan(34);
            subtable.addCell(cell);

            cell = new PdfPCell(new Phrase(Redondeo.formatear_csm(detalle.getPrecioUnitario()), NORMAL));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            cell.setColspan(12);
            subtable.addCell(cell);

            cell = new PdfPCell(new Phrase(Redondeo.formatear_csm(detalle.getDescuento()), NORMAL));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            cell.setColspan(12);
            subtable.addCell(cell);

            cell = new PdfPCell(new Phrase(Redondeo.formatear_csm(detalle.getSubtotal()), NORMAL));
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            cell.setColspan(12);
            subtable.addCell(cell);
        }

        cell = new PdfPCell(new Phrase("Son: " + comprobante.montoPagarLiteral(), NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(64);
        cell.setRowspan(3);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("SUBTOTAL Bs", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setColspan(24);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.subtotal(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setColspan(12);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("DESCUENTO Bs", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setColspan(24);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.descuento(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setColspan(12);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase("MONTO A PAGAR Bs", NEGRITA));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setColspan(24);
        subtable.addCell(cell);

        cell = new PdfPCell(new Phrase(comprobante.montoPagar(), NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setColspan(12);
        subtable.addCell(cell);

        cell = new PdfPCell(subtable);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(100);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        // pie
        subtable = new PdfPTable(100);

        cell = new PdfPCell(new Phrase(" ", NORMAL));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
        cell.setColspan(100);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        Phrase phrase = new Phrase();
        phrase.add(new Chunk("\"Este comprobante de pago es intransferible, exclusivo para fines institucionales y no valido para credito fiscal.\"", NORMAL));
        phrase.add(new Chunk("\n\n", NORMAL));
        phrase.add(new Chunk("\"Cualquier raspadura o enmienda invalida el presente documento.\"", NORMAL));
        cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setColspan(85);
        cell.setBorder(PdfPCell.NO_BORDER);
        subtable.addCell(cell);

        StringBuilder sb = new StringBuilder();
        sb.append("numero=").append(comprobante.getNumero());
        sb.append("&fecha=").append(comprobante.fecha_ddMMyyyy());
        sb.append("&estudiante=").append(comprobante.getInscrito().getEstudiante().toString());
        sb.append("&carrera=").append(comprobante.getInscrito().getCarrera().toString());
        sb.append("&gestionAcademica=").append(comprobante.getInscrito().getGestionAcademica().toString());
        sb.append("&montoPagar=").append(comprobante.montoPagar());
        String sbEncriptado = Encriptador.encriptar(sb.toString());
        BarcodeQRCode barcodeQRCode = new BarcodeQRCode(sbEncriptado, 1, 1, null);
        Image image = barcodeQRCode.getImage();
        image.scalePercent(80);
        cell = new PdfPCell(image);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(15);
        subtable.addCell(cell);

        cell = new PdfPCell(subtable);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        cell.setColspan(100);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        return table;
    }

}

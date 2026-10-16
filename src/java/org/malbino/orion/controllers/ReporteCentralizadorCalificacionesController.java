/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.malbino.orion.entities.Campus;
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.entities.Log;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.enums.Turno;
import org.malbino.orion.facades.GrupoFacade;
import org.malbino.orion.facades.negocio.CentralizadorCalificacionesFacade;
import org.malbino.orion.pojos.centralizador.EstudianteCentralizador;
import org.malbino.orion.pojos.centralizador.NotaCentralizador;
import org.malbino.orion.pojos.centralizador.Centralizador;
import org.malbino.orion.util.Fecha;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tincho
 */
@Named("ReporteCentralizadorCalificacionesController")
@SessionScoped
public class ReporteCentralizadorCalificacionesController extends AbstractController implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(ReporteCentralizadorCalificacionesController.class);

    private static String PATHNAME_10 = File.separator + "resources" + File.separator + "uploads" + File.separator + "centralizador_calificaciiones_10.xlsx";
    private static String PATHNAME_20 = File.separator + "resources" + File.separator + "uploads" + File.separator + "centralizador_calificaciiones_20.xlsx";
    private static String PATHNAME_30 = File.separator + "resources" + File.separator + "uploads" + File.separator + "centralizador_calificaciiones_30.xlsx";
    private static String PATHNAME_40 = File.separator + "resources" + File.separator + "uploads" + File.separator + "centralizador_calificaciiones_40.xlsx";
    private static String PATHNAME_50 = File.separator + "resources" + File.separator + "uploads" + File.separator + "centralizador_calificaciiones_50.xlsx";

    private static final int CAN_MIN_MOD = 10;

    @EJB
    private GrupoFacade grupoFacade;
    @EJB
    private CentralizadorCalificacionesFacade centralizadorCalificacionesFacade;

    @Inject
    private LoginController loginController;

    private GestionAcademica seleccionGestionAcademica;
    private Carrera seleccionCarrera;
    private Campus seleccionCampus;
    private Turno seleccionTurno;
    private String seleccionParalelo;

    private StreamedContent download;

    @PostConstruct
    public void init() {
        seleccionGestionAcademica = null;
        seleccionCarrera = null;
        seleccionCampus = null;
        seleccionTurno = null;
        seleccionParalelo = null;
    }

    public void reinit() {
        seleccionGestionAcademica = null;
        seleccionCarrera = null;
        seleccionCampus = null;
        seleccionTurno = null;
        seleccionParalelo = null;
    }

    public List<String> listaParalelos() {
        log.info("listaParalelos...");

        List<String> paralelos = new ArrayList<>();

        log.info("seleccionGestionAcademica=" + seleccionGestionAcademica);
        log.info("seleccionCarrera=" + seleccionCarrera);
        log.info("seleccionCampus=" + seleccionCampus);
        log.info("seleccionTurno=" + seleccionTurno);

        if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus != null && seleccionTurno != null) {
            paralelos = grupoFacade.listaParalelos(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionCampus.getId_campus(), seleccionTurno);
            log.info("paralelos=" + paralelos.size());
        }
        return paralelos;
    }

    public void generarPDF() throws IOException {
        if (seleccionGestionAcademica != null && seleccionCarrera != null) {
            //generamos el centralizador
            Centralizador centralizador = centralizadorCalificacionesFacade.centralizadorCalificaciones(seleccionGestionAcademica, seleccionCarrera, seleccionCampus, seleccionTurno, seleccionParalelo);
            this.insertarParametro("centralizador", centralizador);

            toCentralizadorCalificaciones();

            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.READ, "Generación reporte centralizador de calificaciones en formato PDF", loginController.getUsr().toString()));
        }
    }

    public XSSFWorkbook leerArchivo(String pathname) {
        XSSFWorkbook workbook = null;

        try ( FileInputStream file = new FileInputStream(this.realPath() + pathname)) {
            workbook = new XSSFWorkbook(file);
        } catch (IOException e) {
            this.mensajeDeError("Error: No se pudo leer el archivo.");
        }

        return workbook;
    }

    public void descargarArchivo(XSSFWorkbook workbook, String name) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            InputStream is = new ByteArrayInputStream(baos.toByteArray());

            download = DefaultStreamedContent.builder()
                    .name(name + ".xlsx")
                    .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .stream(() -> is)
                    .build();
        } catch (IOException ex) {
            this.mensajeDeError("Error: No se pudo descargar el archivo.");
        }
    }

    public void generarXLSX() {
        Centralizador centralizador = centralizadorCalificacionesFacade.centralizadorCalificaciones(seleccionGestionAcademica, seleccionCarrera, seleccionCampus, seleccionTurno, seleccionParalelo);

        XSSFWorkbook workbook = null;
        int cantidadModulos = centralizador.getModulosCentralizador().length;
        if (cantidadModulos == CAN_MIN_MOD) {
            workbook = leerArchivo(PATHNAME_10);
        } else if (cantidadModulos == (CAN_MIN_MOD * 2)) {
            workbook = leerArchivo(PATHNAME_20);
        } else if (cantidadModulos == (CAN_MIN_MOD * 3)) {
            workbook = leerArchivo(PATHNAME_30);
        } else if (cantidadModulos == (CAN_MIN_MOD * 4)) {
            workbook = leerArchivo(PATHNAME_40);
        } else if (cantidadModulos == (CAN_MIN_MOD * 5)) {
            workbook = leerArchivo(PATHNAME_50);
        }

        XSSFSheet sheet = workbook.getSheetAt(0);
        if (sheet != null) {
            Iterator<Row> rowIterator = sheet.rowIterator();

            int rowNum = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        if (cell.getStringCellValue().contains("<<INSTITUTO>>")) {
                            cell.setCellValue(cell.getStringCellValue().replace("<<INSTITUTO>>", centralizador.getInstituto()));
                        } else if (cell.getStringCellValue().contains("<<GESTION>>")) {
                            cell.setCellValue(cell.getStringCellValue().replace("<<GESTION>>", centralizador.getGestion()));
                        } else if (cell.getStringCellValue().contains("<<CARRERA>>")) {
                            cell.setCellValue(cell.getStringCellValue().replace("<<CARRERA>>", centralizador.getCarrera()));
                        } else if (cell.getStringCellValue().contains("<<CAMPUS>>")) {
                            cell.setCellValue(cell.getStringCellValue().replace("<<CAMPUS>>", centralizador.getCampus()));
                        } else if (cell.getStringCellValue().contains("<<TURNO>>")) {
                            cell.setCellValue(cell.getStringCellValue().replace("<<TURNO>>", centralizador.getTurno()));
                        } else if (cell.getStringCellValue().contains("<<PARALELO>>")) {
                            cell.setCellValue(cell.getStringCellValue().replace("<<PARALELO>>", centralizador.getParalelo()));
                        } else if (cell.getStringCellValue().contains("<<M1>>")) {
                            if (centralizador.getModulosCentralizador()[0] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M1>>", centralizador.getModulosCentralizador()[0]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M1>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M2>>")) {
                            if (centralizador.getModulosCentralizador()[1] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M2>>", centralizador.getModulosCentralizador()[1]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M2>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M3>>")) {
                            if (centralizador.getModulosCentralizador()[2] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M3>>", centralizador.getModulosCentralizador()[2]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M3>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M4>>")) {
                            if (centralizador.getModulosCentralizador()[3] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M4>>", centralizador.getModulosCentralizador()[3]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M4>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M5>>")) {
                            if (centralizador.getModulosCentralizador()[4] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M5>>", centralizador.getModulosCentralizador()[4]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M5>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M6>>")) {
                            if (centralizador.getModulosCentralizador()[5] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M6>>", centralizador.getModulosCentralizador()[5]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M6>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M7>>")) {
                            if (centralizador.getModulosCentralizador()[6] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M7>>", centralizador.getModulosCentralizador()[6]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M7>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M8>>")) {
                            if (centralizador.getModulosCentralizador()[7] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M8>>", centralizador.getModulosCentralizador()[7]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M8>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M9>>")) {
                            if (centralizador.getModulosCentralizador()[8] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M9>>", centralizador.getModulosCentralizador()[8]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M9>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M10>>")) {
                            if (centralizador.getModulosCentralizador()[9] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M10>>", centralizador.getModulosCentralizador()[9]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M10>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M11>>")) {
                            if (centralizador.getModulosCentralizador()[10] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M11>>", centralizador.getModulosCentralizador()[10]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M11>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M12>>")) {
                            if (centralizador.getModulosCentralizador()[11] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M12>>", centralizador.getModulosCentralizador()[11]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M12>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M13>>")) {
                            if (centralizador.getModulosCentralizador()[12] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M13>>", centralizador.getModulosCentralizador()[12]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M13>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M14>>")) {
                            if (centralizador.getModulosCentralizador()[13] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M14>>", centralizador.getModulosCentralizador()[13]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M14>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M15>>")) {
                            if (centralizador.getModulosCentralizador()[14] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M15>>", centralizador.getModulosCentralizador()[14]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M15>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M16>>")) {
                            if (centralizador.getModulosCentralizador()[15] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M16>>", centralizador.getModulosCentralizador()[15]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M16>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M17>>")) {
                            if (centralizador.getModulosCentralizador()[16] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M17>>", centralizador.getModulosCentralizador()[16]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M17>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M18>>")) {
                            if (centralizador.getModulosCentralizador()[17] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M18>>", centralizador.getModulosCentralizador()[17]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M18>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M19>>")) {
                            if (centralizador.getModulosCentralizador()[18] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M19>>", centralizador.getModulosCentralizador()[18]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M19>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M20>>")) {
                            if (centralizador.getModulosCentralizador()[19] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M20>>", centralizador.getModulosCentralizador()[19]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M20>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M21>>")) {
                            if (centralizador.getModulosCentralizador()[20] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M21>>", centralizador.getModulosCentralizador()[20]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M21>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M22>>")) {
                            if (centralizador.getModulosCentralizador()[21] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M22>>", centralizador.getModulosCentralizador()[21]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M22>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M23>>")) {
                            if (centralizador.getModulosCentralizador()[22] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M23>>", centralizador.getModulosCentralizador()[22]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M23>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M24>>")) {
                            if (centralizador.getModulosCentralizador()[23] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M24>>", centralizador.getModulosCentralizador()[23]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M24>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M25>>")) {
                            if (centralizador.getModulosCentralizador()[24] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M25>>", centralizador.getModulosCentralizador()[24]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M25>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M26>>")) {
                            if (centralizador.getModulosCentralizador()[25] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M26>>", centralizador.getModulosCentralizador()[25]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M26>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M27>>")) {
                            if (centralizador.getModulosCentralizador()[26] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M27>>", centralizador.getModulosCentralizador()[26]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M27>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M28>>")) {
                            if (centralizador.getModulosCentralizador()[27] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M28>>", centralizador.getModulosCentralizador()[27]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M28>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M29>>")) {
                            if (centralizador.getModulosCentralizador()[28] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M29>>", centralizador.getModulosCentralizador()[28]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M29>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M30>>")) {
                            if (centralizador.getModulosCentralizador()[29] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M30>>", centralizador.getModulosCentralizador()[29]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M30>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M31>>")) {
                            if (centralizador.getModulosCentralizador()[30] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M31>>", centralizador.getModulosCentralizador()[30]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M31>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M32>>")) {
                            if (centralizador.getModulosCentralizador()[31] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M32>>", centralizador.getModulosCentralizador()[31]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M32>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M33>>")) {
                            if (centralizador.getModulosCentralizador()[32] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M33>>", centralizador.getModulosCentralizador()[32]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M33>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M34>>")) {
                            if (centralizador.getModulosCentralizador()[33] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M34>>", centralizador.getModulosCentralizador()[33]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M34>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M35>>")) {
                            if (centralizador.getModulosCentralizador()[34] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M35>>", centralizador.getModulosCentralizador()[34]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M35>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M36>>")) {
                            if (centralizador.getModulosCentralizador()[35] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M36>>", centralizador.getModulosCentralizador()[35]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M36>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M37>>")) {
                            if (centralizador.getModulosCentralizador()[36] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M37>>", centralizador.getModulosCentralizador()[36]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M37>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M38>>")) {
                            if (centralizador.getModulosCentralizador()[37] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M38>>", centralizador.getModulosCentralizador()[37]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M38>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M39>>")) {
                            if (centralizador.getModulosCentralizador()[38] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M39>>", centralizador.getModulosCentralizador()[38]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M39>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M40>>")) {
                            if (centralizador.getModulosCentralizador()[39] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M40>>", centralizador.getModulosCentralizador()[39]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M40>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M41>>")) {
                            if (centralizador.getModulosCentralizador()[40] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M41>>", centralizador.getModulosCentralizador()[40]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M41>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M42>>")) {
                            if (centralizador.getModulosCentralizador()[41] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M42>>", centralizador.getModulosCentralizador()[41]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M42>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M43>>")) {
                            if (centralizador.getModulosCentralizador()[42] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M43>>", centralizador.getModulosCentralizador()[42]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M43>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M44>>")) {
                            if (centralizador.getModulosCentralizador()[43] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M44>>", centralizador.getModulosCentralizador()[43]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M44>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M45>>")) {
                            if (centralizador.getModulosCentralizador()[44] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M45>>", centralizador.getModulosCentralizador()[44]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M45>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M46>>")) {
                            if (centralizador.getModulosCentralizador()[45] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M46>>", centralizador.getModulosCentralizador()[45]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M46>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M47>>")) {
                            if (centralizador.getModulosCentralizador()[46] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M47>>", centralizador.getModulosCentralizador()[46]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M47>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M48>>")) {
                            if (centralizador.getModulosCentralizador()[47] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M48>>", centralizador.getModulosCentralizador()[47]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M48>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M49>>")) {
                            if (centralizador.getModulosCentralizador()[48] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M49>>", centralizador.getModulosCentralizador()[48]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M49>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<M50>>")) {
                            if (centralizador.getModulosCentralizador()[49] != null) {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M50>>", centralizador.getModulosCentralizador()[49]));
                            } else {
                                cell.setCellValue(cell.getStringCellValue().replace("<<M50>>", ""));
                            }
                        } else if (cell.getStringCellValue().contains("<<ESTUDIANTE>>")) {
                            rowNum = row.getRowNum();
                        }
                    }
                }
            }

            EstudianteCentralizador[] estudiantesSeguimiento = centralizador.getEstudiantesCentralizador();

            sheet.shiftRows(rowNum + 1, sheet.getLastRowNum(), estudiantesSeguimiento.length - 1, true, true);

            CellCopyPolicy cellCopyPolicy = new CellCopyPolicy.Builder().cellStyle(true).build();
            for (int i = 0; i < estudiantesSeguimiento.length - 1; i++) {
                sheet.copyRows(rowNum, rowNum, rowNum + i + 1, cellCopyPolicy);
            }

            for (int i = 0; i < estudiantesSeguimiento.length; i++) {
                EstudianteCentralizador estudianteSeguimiento = estudiantesSeguimiento[i];
                NotaCentralizador[] notasSeguimiento = estudianteSeguimiento.getNotasCentralizador();

                XSSFRow row = sheet.getRow(rowNum + i);
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                        if (cell.getNumericCellValue() == 0) {
                            cell.setCellValue(i + 1);
                        } else if (cell.getNumericCellValue() == 1.1) {
                            if (notasSeguimiento[0] != null) {
                                if (notasSeguimiento[0].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[0].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 1.2) {
                            if (notasSeguimiento[0] != null) {
                                if (notasSeguimiento[0].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[0].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 2.1) {
                            if (notasSeguimiento[1] != null) {
                                if (notasSeguimiento[1].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[1].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 2.2) {
                            if (notasSeguimiento[1] != null) {
                                if (notasSeguimiento[1].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[1].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 3.1) {
                            if (notasSeguimiento[2] != null) {
                                if (notasSeguimiento[2].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[2].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 3.2) {
                            if (notasSeguimiento[2] != null) {
                                if (notasSeguimiento[2].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[2].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 4.1) {
                            if (notasSeguimiento[3] != null) {
                                if (notasSeguimiento[3].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[3].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 4.2) {
                            if (notasSeguimiento[3] != null) {
                                if (notasSeguimiento[3].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[3].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 5.1) {
                            if (notasSeguimiento[4] != null) {
                                if (notasSeguimiento[4].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[4].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 5.2) {
                            if (notasSeguimiento[4] != null) {
                                if (notasSeguimiento[4].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[4].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 6.1) {
                            if (notasSeguimiento[5] != null) {
                                if (notasSeguimiento[5].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[5].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 6.2) {
                            if (notasSeguimiento[5] != null) {
                                if (notasSeguimiento[5].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[5].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 7.1) {
                            if (notasSeguimiento[6] != null) {
                                if (notasSeguimiento[6].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[6].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 7.2) {
                            if (notasSeguimiento[6] != null) {
                                if (notasSeguimiento[6].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[6].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 8.1) {
                            if (notasSeguimiento[7] != null) {
                                if (notasSeguimiento[7].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[7].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 8.2) {
                            if (notasSeguimiento[7] != null) {
                                if (notasSeguimiento[7].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[7].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 9.1) {
                            if (notasSeguimiento[8] != null) {
                                if (notasSeguimiento[8].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[8].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 9.2) {
                            if (notasSeguimiento[8] != null) {
                                if (notasSeguimiento[8].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[8].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 10.1) {
                            if (notasSeguimiento[9] != null) {
                                if (notasSeguimiento[9].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[9].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 10.2) {
                            if (notasSeguimiento[9] != null) {
                                if (notasSeguimiento[9].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[9].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 11.1) {
                            if (notasSeguimiento[10] != null) {
                                if (notasSeguimiento[10].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[10].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 11.2) {
                            if (notasSeguimiento[10] != null) {
                                if (notasSeguimiento[10].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[10].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 12.1) {
                            if (notasSeguimiento[11] != null) {
                                if (notasSeguimiento[11].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[11].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 12.2) {
                            if (notasSeguimiento[11] != null) {
                                if (notasSeguimiento[11].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[11].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 13.1) {
                            if (notasSeguimiento[12] != null) {
                                if (notasSeguimiento[12].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[12].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 13.2) {
                            if (notasSeguimiento[12] != null) {
                                if (notasSeguimiento[12].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[12].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 14.1) {
                            if (notasSeguimiento[13] != null) {
                                if (notasSeguimiento[13].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[13].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 14.2) {
                            if (notasSeguimiento[13] != null) {
                                if (notasSeguimiento[13].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[13].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 15.1) {
                            if (notasSeguimiento[14] != null) {
                                if (notasSeguimiento[14].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[14].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 15.2) {
                            if (notasSeguimiento[14] != null) {
                                if (notasSeguimiento[14].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[14].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 16.1) {
                            if (notasSeguimiento[15] != null) {
                                if (notasSeguimiento[15].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[15].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 16.2) {
                            if (notasSeguimiento[15] != null) {
                                if (notasSeguimiento[15].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[15].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 17.1) {
                            if (notasSeguimiento[16] != null) {
                                if (notasSeguimiento[16].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[16].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 17.2) {
                            if (notasSeguimiento[16] != null) {
                                if (notasSeguimiento[16].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[16].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 18.1) {
                            if (notasSeguimiento[17] != null) {
                                if (notasSeguimiento[17].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[17].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 18.2) {
                            if (notasSeguimiento[17] != null) {
                                if (notasSeguimiento[17].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[17].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 19.1) {
                            if (notasSeguimiento[18] != null) {
                                if (notasSeguimiento[18].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[18].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 19.2) {
                            if (notasSeguimiento[18] != null) {
                                if (notasSeguimiento[18].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[18].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 20.1) {
                            if (notasSeguimiento[19] != null) {
                                if (notasSeguimiento[19].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[19].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 20.2) {
                            if (notasSeguimiento[19] != null) {
                                if (notasSeguimiento[19].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[19].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 21.1) {
                            if (notasSeguimiento[20] != null) {
                                if (notasSeguimiento[20].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[20].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 21.2) {
                            if (notasSeguimiento[20] != null) {
                                if (notasSeguimiento[20].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[20].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 22.1) {
                            if (notasSeguimiento[21] != null) {
                                if (notasSeguimiento[21].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[21].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 22.2) {
                            if (notasSeguimiento[21] != null) {
                                if (notasSeguimiento[21].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[21].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 23.1) {
                            if (notasSeguimiento[22] != null) {
                                if (notasSeguimiento[22].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[22].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 23.2) {
                            if (notasSeguimiento[22] != null) {
                                if (notasSeguimiento[22].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[22].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 24.1) {
                            if (notasSeguimiento[23] != null) {
                                if (notasSeguimiento[23].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[23].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 24.2) {
                            if (notasSeguimiento[23] != null) {
                                if (notasSeguimiento[23].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[23].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 25.1) {
                            if (notasSeguimiento[24] != null) {
                                if (notasSeguimiento[24].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[24].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 25.2) {
                            if (notasSeguimiento[24] != null) {
                                if (notasSeguimiento[24].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[24].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 26.1) {
                            if (notasSeguimiento[25] != null) {
                                if (notasSeguimiento[25].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[25].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 26.2) {
                            if (notasSeguimiento[25] != null) {
                                if (notasSeguimiento[25].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[25].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 27.1) {
                            if (notasSeguimiento[26] != null) {
                                if (notasSeguimiento[26].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[26].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 27.2) {
                            if (notasSeguimiento[26] != null) {
                                if (notasSeguimiento[26].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[26].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 28.1) {
                            if (notasSeguimiento[27] != null) {
                                if (notasSeguimiento[27].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[27].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 28.2) {
                            if (notasSeguimiento[27] != null) {
                                if (notasSeguimiento[27].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[27].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 29.1) {
                            if (notasSeguimiento[28] != null) {
                                if (notasSeguimiento[28].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[28].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 29.2) {
                            if (notasSeguimiento[28] != null) {
                                if (notasSeguimiento[28].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[28].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 30.1) {
                            if (notasSeguimiento[29] != null) {
                                if (notasSeguimiento[29].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[29].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 30.2) {
                            if (notasSeguimiento[29] != null) {
                                if (notasSeguimiento[29].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[29].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 31.1) {
                            if (notasSeguimiento[30] != null) {
                                if (notasSeguimiento[30].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[30].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 31.2) {
                            if (notasSeguimiento[30] != null) {
                                if (notasSeguimiento[30].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[30].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 32.1) {
                            if (notasSeguimiento[31] != null) {
                                if (notasSeguimiento[31].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[31].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 32.2) {
                            if (notasSeguimiento[31] != null) {
                                if (notasSeguimiento[31].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[31].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 33.1) {
                            if (notasSeguimiento[32] != null) {
                                if (notasSeguimiento[32].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[32].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 33.2) {
                            if (notasSeguimiento[32] != null) {
                                if (notasSeguimiento[32].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[32].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 34.1) {
                            if (notasSeguimiento[33] != null) {
                                if (notasSeguimiento[33].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[33].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 34.2) {
                            if (notasSeguimiento[33] != null) {
                                if (notasSeguimiento[33].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[33].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 35.1) {
                            if (notasSeguimiento[34] != null) {
                                if (notasSeguimiento[34].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[34].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 35.2) {
                            if (notasSeguimiento[34] != null) {
                                if (notasSeguimiento[34].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[34].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 36.1) {
                            if (notasSeguimiento[35] != null) {
                                if (notasSeguimiento[35].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[35].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 36.2) {
                            if (notasSeguimiento[35] != null) {
                                if (notasSeguimiento[35].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[35].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 37.1) {
                            if (notasSeguimiento[36] != null) {
                                if (notasSeguimiento[36].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[36].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 37.2) {
                            if (notasSeguimiento[36] != null) {
                                if (notasSeguimiento[36].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[36].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 38.1) {
                            if (notasSeguimiento[37] != null) {
                                if (notasSeguimiento[37].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[37].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 38.2) {
                            if (notasSeguimiento[37] != null) {
                                if (notasSeguimiento[37].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[37].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 39.1) {
                            if (notasSeguimiento[38] != null) {
                                if (notasSeguimiento[38].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[38].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 39.2) {
                            if (notasSeguimiento[38] != null) {
                                if (notasSeguimiento[38].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[38].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 40.1) {
                            if (notasSeguimiento[39] != null) {
                                if (notasSeguimiento[39].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[39].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 40.2) {
                            if (notasSeguimiento[39] != null) {
                                if (notasSeguimiento[39].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[39].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 41.1) {
                            if (notasSeguimiento[40] != null) {
                                if (notasSeguimiento[40].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[40].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 41.2) {
                            if (notasSeguimiento[40] != null) {
                                if (notasSeguimiento[40].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[40].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 42.1) {
                            if (notasSeguimiento[41] != null) {
                                if (notasSeguimiento[41].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[41].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 42.2) {
                            if (notasSeguimiento[41] != null) {
                                if (notasSeguimiento[41].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[41].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 43.1) {
                            if (notasSeguimiento[42] != null) {
                                if (notasSeguimiento[42].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[42].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 43.2) {
                            if (notasSeguimiento[42] != null) {
                                if (notasSeguimiento[42].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[42].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 44.1) {
                            if (notasSeguimiento[43] != null) {
                                if (notasSeguimiento[43].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[43].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 44.2) {
                            if (notasSeguimiento[43] != null) {
                                if (notasSeguimiento[43].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[43].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 45.1) {
                            if (notasSeguimiento[44] != null) {
                                if (notasSeguimiento[44].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[44].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 45.2) {
                            if (notasSeguimiento[44] != null) {
                                if (notasSeguimiento[44].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[44].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 46.1) {
                            if (notasSeguimiento[45] != null) {
                                if (notasSeguimiento[45].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[45].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 46.2) {
                            if (notasSeguimiento[45] != null) {
                                if (notasSeguimiento[45].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[45].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 47.1) {
                            if (notasSeguimiento[46] != null) {
                                if (notasSeguimiento[46].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[46].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 47.2) {
                            if (notasSeguimiento[46] != null) {
                                if (notasSeguimiento[46].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[46].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 48.1) {
                            if (notasSeguimiento[47] != null) {
                                if (notasSeguimiento[47].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[47].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 48.2) {
                            if (notasSeguimiento[47] != null) {
                                if (notasSeguimiento[47].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[47].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 49.1) {
                            if (notasSeguimiento[48] != null) {
                                if (notasSeguimiento[48].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[48].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 49.2) {
                            if (notasSeguimiento[48] != null) {
                                if (notasSeguimiento[48].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[48].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 50.1) {
                            if (notasSeguimiento[49] != null) {
                                if (notasSeguimiento[49].getNotaFinal() != null) {
                                    cell.setCellValue(notasSeguimiento[49].getNotaFinal());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        } else if (cell.getNumericCellValue() == 50.2) {
                            if (notasSeguimiento[49] != null) {
                                if (notasSeguimiento[49].getRecuperatorio() != null) {
                                    cell.setCellValue(notasSeguimiento[49].getRecuperatorio());
                                } else {
                                    cell.setCellValue("");
                                }
                            } else {
                                cell.setCellValue("");
                            }
                        }
                    } else if (cell.getCellTypeEnum() == CellType.STRING) {
                        if (cell.getStringCellValue().contains("<<ESTUDIANTE>>")) {
                            cell.setCellValue(cell.getStringCellValue().replace("<<ESTUDIANTE>>", estudianteSeguimiento.getEstudiante()));
                        }
                    }
                }
            }

        }

        String name = seleccionGestionAcademica.toString() + " - " + seleccionCarrera.getNombre() + " - " + seleccionCampus.getCiudad() + " - " + seleccionTurno.toString() + " - " + seleccionParalelo;
        descargarArchivo(workbook, name);

        //log
        logFacade.create(new Log(Fecha.getDate(), EventoLog.READ, "Generación reporte seguimiento académico", loginController.getUsr().toString()));
    }

    public void toReporteCentralizadorCalificaciones() throws IOException {
        reinit();

        this.redireccionarViewId("/reportes/centralizadorCalificaciones/reporteCentralizadorCalificaciones");
    }

    public void toCentralizadorCalificaciones() throws IOException {
        this.redireccionarViewId("/reportes/centralizadorCalificaciones/centralizadorCalificaciones");
    }

    /**
     * @return the seleccionGestionAcademica
     */
    public GestionAcademica getSeleccionGestionAcademica() {
        return seleccionGestionAcademica;
    }

    /**
     * @param seleccionGestionAcademica the seleccionGestionAcademica to set
     */
    public void setSeleccionGestionAcademica(GestionAcademica seleccionGestionAcademica) {
        this.seleccionGestionAcademica = seleccionGestionAcademica;
    }

    /**
     * @return the seleccionCarrera
     */
    public Carrera getSeleccionCarrera() {
        return seleccionCarrera;
    }

    /**
     * @param seleccionCarrera the seleccionCarrera to set
     */
    public void setSeleccionCarrera(Carrera seleccionCarrera) {
        this.seleccionCarrera = seleccionCarrera;
    }

    /**
     * @return the seleccionCampus
     */
    public Campus getSeleccionCampus() {
        return seleccionCampus;
    }

    /**
     * @param seleccionCampus the seleccionCampus to set
     */
    public void setSeleccionCampus(Campus seleccionCampus) {
        this.seleccionCampus = seleccionCampus;
    }

    /**
     * @return the seleccionTurno
     */
    public Turno getSeleccionTurno() {
        return seleccionTurno;
    }

    /**
     * @param seleccionTurno the seleccionTurno to set
     */
    public void setSeleccionTurno(Turno seleccionTurno) {
        this.seleccionTurno = seleccionTurno;
    }

    /**
     * @return the seleccionParalelo
     */
    public String getSeleccionParalelo() {
        return seleccionParalelo;
    }

    /**
     * @param seleccionParalelo the seleccionParalelo to set
     */
    public void setSeleccionParalelo(String seleccionParalelo) {
        this.seleccionParalelo = seleccionParalelo;
    }

    /**
     * @return the download
     */
    public StreamedContent getDownload() {
        return download;
    }

    /**
     * @param download the download to set
     */
    public void setDownload(StreamedContent download) {
        this.download = download;
    }

}

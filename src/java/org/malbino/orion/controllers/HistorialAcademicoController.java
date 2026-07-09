/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.malbino.orion.entities.CarreraEstudiante;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.Log;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.entities.Nota;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.enums.Modalidad;
import org.malbino.orion.facades.CarreraEstudianteFacade;
import org.malbino.orion.facades.NotaFacade;
import org.malbino.orion.facades.negocio.FileEstudianteFacade;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Named("HistorialAcademicoController")
@SessionScoped
public class HistorialAcademicoController extends AbstractController implements Serializable {

    @Inject
    LoginController loginController;
    @EJB
    NotaFacade notaFacade;
    @EJB
    FileEstudianteFacade fileEstudianteFacade;
    @EJB
    CarreraEstudianteFacade carreraEstudianteFacade;

    private Estudiante seleccionEstudiante;
    private CarreraEstudiante seleccionCarreraEstudiante;
    private List<Nota> historialAcademico;

    private Nota nuevaNota;
    private Nota seleccionNota;

    private List<Log> logs;

    @PostConstruct
    public void init() {
        seleccionEstudiante = null;
        seleccionCarreraEstudiante = null;
        historialAcademico = new ArrayList();

        nuevaNota = new Nota();
        seleccionNota = null;

        logs = new ArrayList<>();
    }

    public void reinit() {
        if (seleccionEstudiante != null && seleccionCarreraEstudiante != null) {
            historialAcademico = notaFacade.historialAcademico(seleccionEstudiante, seleccionCarreraEstudiante.getCarrera());
        }

        nuevaNota = new Nota();
        seleccionNota = null;

        logs = new ArrayList<>();
    }

    public List<CarreraEstudiante> listaCarrerasEstudiante() {
        List<CarreraEstudiante> l = new ArrayList();
        if (seleccionEstudiante != null) {
            l = carreraEstudianteFacade.listaCarrerasEstudiante(seleccionEstudiante.getId_persona());
        }
        return l;
    }

    public List<Modulo> listaModulos() {
        List<Modulo> l = new ArrayList();
        if (seleccionCarreraEstudiante != null && seleccionEstudiante != null) {
            l = fileEstudianteFacade.oferta(seleccionCarreraEstudiante.getCarrera(), seleccionEstudiante);
        }
        return l;
    }

    @Override
    public Modalidad[] listaModalidades() {
        return Modalidad.values(Boolean.FALSE);
    }

    public void logNota() {
        logs = logFacade.listaLogNota(seleccionNota.getId_nota());
    }

    public void editarNotaFinal() throws IOException {
        if (seleccionNota.getRecuperatorio() == null) {
            if (fileEstudianteFacade.editarNota(seleccionNota)) {
                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.UPDATE, EntidadLog.NOTA, seleccionNota.getId_nota(), "Actualización nota final", loginController.getUsr().toString()));

                toHistorialAcademico();
            }
        } else {
            this.mensajeDeError("El recuperatorio debe ser nulo para editar la nota final.");
        }
    }

    public void editarRecuperatorio() throws IOException {
        List<Nota> listaNotasReprobadas = notaFacade.listaNotasReprobadas(seleccionNota.getGestionAcademica(), seleccionNota.getModulo().getCarrera(), seleccionNota.getEstudiante());
        if (listaNotasReprobadas.size() <= seleccionNota.getGestionAcademica().getModalidadEvaluacion().getCantidadMaximaReprobaciones()) {
            if (seleccionNota.getNotaFinal() != null
                    && seleccionNota.getNotaFinal() >= seleccionNota.getGestionAcademica().getModalidadEvaluacion().getNotaMinimmaPruebaRecuperacion()
                    && seleccionNota.getNotaFinal() < seleccionNota.getGestionAcademica().getModalidadEvaluacion().getNotaMinimaAprobacion()) {
                if (fileEstudianteFacade.editarNota(seleccionNota)) {
                    //log
                    logFacade.create(new Log(Fecha.getDate(), EventoLog.UPDATE, EntidadLog.NOTA, seleccionNota.getId_nota(), "Actualización nota recuperatorio", loginController.getUsr().toString()));

                    toHistorialAcademico();
                }
            } else {
                this.mensajeDeError("La nota final esta fuera del rango permitido.");
            }
        } else {
            this.mensajeDeError("Las modulos reprobadas exceden el maximo permitido.");
        }
    }

    public void crearNota() throws IOException {
        List<Nota> listaNotasModulo = notaFacade.listaNotasModulo(nuevaNota.getGestionAcademica().getId_gestionacademica(), seleccionEstudiante.getId_persona(), nuevaNota.getModulo().getId_modulo());
        if (listaNotasModulo.isEmpty()) {
            nuevaNota.setEstudiante(seleccionEstudiante);
            if (fileEstudianteFacade.crearNota(nuevaNota)) {
                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.CREATE, EntidadLog.NOTA, nuevaNota.getId_nota(), "Creación nota por historial académico", loginController.getUsr().toString()));

                toHistorialAcademico();
            }
        } else {
            this.mensajeDeError("Nota repetida.");
        }
    }

    public void editarNota() throws IOException {
        if (fileEstudianteFacade.editarNota(seleccionNota)) {
            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.UPDATE, EntidadLog.NOTA, seleccionNota.getId_nota(), "Actualización nota por historial académico", loginController.getUsr().toString()));

            toHistorialAcademico();
        }
    }

    public void eliminarNota() throws IOException {
        if (notaFacade.remove(seleccionNota)) {
            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.DELETE, EntidadLog.NOTA, seleccionNota.getId_nota(), "Borrado nota por historial académico", loginController.getUsr().toString()));

            toHistorialAcademico();
        }
    }

    public void toHistorialAcademico() throws IOException {
        reinit();

        this.redireccionarViewId("/fileEstudiante/historialAcademico/historialAcademico");
    }

    public void toEditarNotaFinal() throws IOException {
        this.redireccionarViewId("/fileEstudiante/historialAcademico/editarNotaFinal");
    }

    public void toEditarRecuperatorio() throws IOException {
        this.redireccionarViewId("/fileEstudiante/historialAcademico/editarRecuperatorio");
    }

    public void toNuevaNota() throws IOException {
        this.redireccionarViewId("/fileEstudiante/historialAcademico/nuevaNota");
    }

    public void toEditarNota() throws IOException {
        this.redireccionarViewId("/fileEstudiante/historialAcademico/editarNota");
    }

    public void toLogNota() throws IOException {
        this.logNota();

        this.redireccionarViewId("/fileEstudiante/historialAcademico/logNota");
    }

    /**
     * @return the seleccionEstudiante
     */
    public Estudiante getSeleccionEstudiante() {
        return seleccionEstudiante;
    }

    /**
     * @param seleccionEstudiante the seleccionEstudiante to set
     */
    public void setSeleccionEstudiante(Estudiante seleccionEstudiante) {
        this.seleccionEstudiante = seleccionEstudiante;
    }

    /**
     * @return the seleccionCarreraEstudiante
     */
    public CarreraEstudiante getSeleccionCarreraEstudiante() {
        return seleccionCarreraEstudiante;
    }

    /**
     * @param seleccionCarreraEstudiante the seleccionCarreraEstudiante to set
     */
    public void setSeleccionCarreraEstudiante(CarreraEstudiante seleccionCarreraEstudiante) {
        this.seleccionCarreraEstudiante = seleccionCarreraEstudiante;
    }

    /**
     * @return the historialAcademico
     */
    public List<Nota> getHistorialAcademico() {
        return historialAcademico;
    }

    /**
     * @param historialAcademico the historialAcademico to set
     */
    public void setHistorialAcademico(List<Nota> historialAcademico) {
        this.historialAcademico = historialAcademico;
    }

    /**
     * @return the nuevaNota
     */
    public Nota getNuevaNota() {
        return nuevaNota;
    }

    /**
     * @param nuevaNota the nuevaNota to set
     */
    public void setNuevaNota(Nota nuevaNota) {
        this.nuevaNota = nuevaNota;
    }

    /**
     * @return the seleccionNota
     */
    public Nota getSeleccionNota() {
        return seleccionNota;
    }

    /**
     * @param seleccionNota the seleccionNota to set
     */
    public void setSeleccionNota(Nota seleccionNota) {
        this.seleccionNota = seleccionNota;
    }

    /**
     * @return the logs
     */
    public List<Log> getLogs() {
        return logs;
    }

    /**
     * @param logs the logs to set
     */
    public void setLogs(List<Log> logs) {
        this.logs = logs;
    }

}

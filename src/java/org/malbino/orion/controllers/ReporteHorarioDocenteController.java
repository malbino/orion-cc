/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.controllers;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.malbino.orion.entities.Campus;
import org.malbino.orion.entities.Empleado;
import org.malbino.orion.entities.Log;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Named("ReporteHorarioDocenteController")
@SessionScoped
public class ReporteHorarioDocenteController extends AbstractController implements Serializable {

    @Inject
    LoginController loginController;

    private Campus seleccionCampus;
    private Empleado seleccionEmpleado;

    @PostConstruct
    public void init() {
        seleccionCampus = null;
        seleccionEmpleado = null;
    }

    public void reinit() {
        seleccionCampus = null;
        seleccionEmpleado = null;
    }

    public void generarReporte() throws IOException {
        if (seleccionEmpleado != null) {
            this.insertarParametro("id_campus", seleccionCampus.getId_campus());
            this.insertarParametro("id_persona", seleccionEmpleado.getId_persona());

            toHorarioDocente();

            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.READ, "Generación reporte horario por docente", loginController.getUsr().toString()));
        }
    }

    public void toReporteHorarioDocente() throws IOException {
        reinit();

        this.redireccionarViewId("/reportes/horarios/horarioDocente/reporteHorarioDocente");
    }

    public void toHorarioDocente() throws IOException {
        this.redireccionarViewId("/reportes/horarios/horarioDocente/horarioDocente");
    }

    /**
     * @return the seleccionEmpleado
     */
    public Empleado getSeleccionEmpleado() {
        return seleccionEmpleado;
    }

    /**
     * @param seleccionEmpleado the seleccionEmpleado to set
     */
    public void setSeleccionEmpleado(Empleado seleccionEmpleado) {
        this.seleccionEmpleado = seleccionEmpleado;
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
}

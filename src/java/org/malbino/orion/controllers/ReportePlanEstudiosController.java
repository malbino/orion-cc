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
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.Log;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Named("ReportePlanEstudiosController")
@SessionScoped
public class ReportePlanEstudiosController extends AbstractController implements Serializable {

    @Inject
    LoginController loginController;

    private Carrera seleccionCarrera;

    @PostConstruct
    public void init() {
        seleccionCarrera = null;
    }

    public void reinit() {
        seleccionCarrera = null;
    }

    public void generarReporte() throws IOException {
        if (seleccionCarrera != null) {
            this.insertarParametro("carrera", seleccionCarrera);

            toPlanEstudios();

            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.READ, "Generación reporte malla curricular", loginController.getUsr().toString()));
        }
    }

    public void toReportePlanEstudios() throws IOException {
        reinit();

        this.redireccionarViewId("/reportes/planEstudios/reportePlanEstudios");
    }

    public void toPlanEstudios() throws IOException {
        this.redireccionarViewId("/reportes/planEstudios/planEstudios");
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

}

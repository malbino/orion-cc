/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.malbino.orion.entities.CarreraEstudiante;
import org.malbino.orion.entities.Detalle;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.Log;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.facades.CarreraEstudianteFacade;
import org.malbino.orion.facades.DetalleFacade;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Named("HistorialEconomicoEstudianteController")
@SessionScoped
public class HistorialEconomicoEstudianteController extends AbstractController implements Serializable {

    @EJB
    CarreraEstudianteFacade carreraEstudianteFacade;
    @EJB
    DetalleFacade detalleFacade;
    @Inject
    LoginController loginController;

    private CarreraEstudiante seleccionCarreraEstudiante;
    private List<Detalle> historialEconomico;

    @PostConstruct
    public void init() {
        historialEconomico = new ArrayList();
    }

    public void reinit() {
        if (seleccionCarreraEstudiante != null) {
            Estudiante estudiante = estudianteFacade.find(loginController.getUsr().getId_persona());
            if (estudiante != null) {
                historialEconomico = detalleFacade.kardexEconomico(estudiante.getId_persona(), seleccionCarreraEstudiante.getCarrera().getId_carrera());

                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.READ, EntidadLog.ESTUDIANTE, estudiante.getId_persona(), "Visualización Historial Económico", loginController.getUsr().toString()));
            }
        }
    }

    public List<CarreraEstudiante> listaCarrerasEstudiante() {
        List<CarreraEstudiante> l = new ArrayList();
        if (loginController.getUsr() != null) {
            l = carreraEstudianteFacade.listaCarrerasEstudiante(loginController.getUsr().getId_persona());
        }
        return l;
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
     * @return the historialEconomico
     */
    public List<Detalle> getHistorialEconomico() {
        return historialEconomico;
    }

    /**
     * @param historialEconomico the historialEconomico to set
     */
    public void setHistorialEconomico(List<Detalle> historialEconomico) {
        this.historialEconomico = historialEconomico;
    }

}

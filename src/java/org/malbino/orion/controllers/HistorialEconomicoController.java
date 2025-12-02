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
import javax.inject.Named;
import org.malbino.orion.entities.CarreraEstudiante;
import org.malbino.orion.entities.Detalle;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.facades.CarreraEstudianteFacade;
import org.malbino.orion.facades.DetalleFacade;

/**
 *
 * @author Tincho
 */
@Named("HistorialEconomicoController")
@SessionScoped
public class HistorialEconomicoController extends AbstractController implements Serializable {

    @EJB
    CarreraEstudianteFacade carreraEstudianteFacade;
    @EJB
    DetalleFacade detalleFacade;

    private Estudiante seleccionEstudiante;
    private CarreraEstudiante seleccionCarreraEstudiante;
    private List<Detalle> historialEconomico;
    private Detalle seleccionDetalle;

    @PostConstruct
    public void init() {
        seleccionEstudiante = null;
        seleccionCarreraEstudiante = null;
        historialEconomico = new ArrayList();
        seleccionDetalle = null;
    }

    public void reinit() {
        if (seleccionEstudiante != null && seleccionCarreraEstudiante != null) {
            historialEconomico = detalleFacade.kardexEconomico(seleccionEstudiante.getId_persona(), seleccionCarreraEstudiante.getCarrera().getId_carrera());
        }
    }

    public List<CarreraEstudiante> listaCarrerasEstudiante() {
        List<CarreraEstudiante> l = new ArrayList();
        if (seleccionEstudiante != null) {
            l = carreraEstudianteFacade.listaCarrerasEstudiante(seleccionEstudiante.getId_persona());
        }
        return l;
    }

    @Override
    public List<GestionAcademica> listaGestionesAcademicas() {
        List<GestionAcademica> l = new ArrayList();
        if (seleccionCarreraEstudiante != null) {
            l = listaGestionesAcademicas();
        }
        return l;
    }

    public void imprimirComprobante() throws IOException {
        this.insertarParametro("id_comprobante", seleccionDetalle.getComprobante().getId_comprobante());

        this.toComprobantePago();
    }

    public void toHistorialEconomico() throws IOException {
        reinit();

        this.redireccionarViewId("/fileEstudiante/historialEconomico/historialEconomico");
    }

    public void toComprobantePago() throws IOException {
        this.redireccionarViewId("/fileEstudiante/historialEconomico/comprobantePago");
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

    /**
     * @return the seleccionDetalle
     */
    public Detalle getSeleccionDetalle() {
        return seleccionDetalle;
    }

    /**
     * @param seleccionDetalle the seleccionDetalle to set
     */
    public void setSeleccionDetalle(Detalle seleccionDetalle) {
        this.seleccionDetalle = seleccionDetalle;
    }
}

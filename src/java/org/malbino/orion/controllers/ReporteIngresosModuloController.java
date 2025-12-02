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
import org.malbino.orion.entities.Campus;
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.entities.Log;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.facades.ModuloFacade;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Named("ReporteIngresosModuloController")
@SessionScoped
public class ReporteIngresosModuloController extends AbstractController implements Serializable {

    @Inject
    LoginController loginController;

    @EJB
    ModuloFacade moduloFacade;

    private GestionAcademica seleccionGestionAcademica;
    private Carrera seleccionCarrera;
    private Campus seleccionCampus;
    private Modulo seleccionModulo;

    @PostConstruct
    public void init() {
        seleccionGestionAcademica = null;
        seleccionCarrera = null;
        seleccionCampus = null;
        seleccionModulo = null;
    }

    public void reinit() {
        seleccionGestionAcademica = null;
        seleccionCarrera = null;
        seleccionCampus = null;
        seleccionModulo = null;
    }

    public List<Modulo> listaModulos() {
        List<Modulo> l = new ArrayList<>();
        if (seleccionCarrera != null) {
            l = moduloFacade.listaModulos(seleccionCarrera);
        }
        return l;
    }

    public void generarReporte() throws IOException {
        if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus != null && seleccionModulo != null) {
            this.insertarParametro("id_gestionacademica", seleccionGestionAcademica.getId_gestionacademica());
            this.insertarParametro("id_carrera", seleccionCarrera.getId_carrera());
            this.insertarParametro("id_campus", seleccionCampus.getId_campus());
            this.insertarParametro("id_modulo", seleccionModulo.getId_modulo());

            toIngresosModulo();

            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.READ, "Generación reporte ingresos por modulo", loginController.getUsr().toString()));
        }
    }

    public void toReporteIngresosModulo() throws IOException {
        reinit();

        this.redireccionarViewId("/reportes/ingresos/modulo/reporteIngresosModulo");
    }

    public void toIngresosModulo() throws IOException {
        this.redireccionarViewId("/reportes/ingresos/modulo/ingresosModulo");
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
     * @return the seleccionModulo
     */
    public Modulo getSeleccionModulo() {
        return seleccionModulo;
    }

    /**
     * @param seleccionModulo the seleccionModulo to set
     */
    public void setSeleccionModulo(Modulo seleccionModulo) {
        this.seleccionModulo = seleccionModulo;
    }

}

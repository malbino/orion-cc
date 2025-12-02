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
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.ConceptoPago;
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.entities.Log;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Named("ReporteIngresosConceptoPagoController")
@SessionScoped
public class ReporteIngresosConceptoPagoController extends AbstractController implements Serializable {

    @Inject
    LoginController loginController;

    private GestionAcademica seleccionGestionAcademica;
    private Carrera seleccionCarrera;
    private Campus seleccionCampus;
    private ConceptoPago seleccionConceptoPago;

    @PostConstruct
    public void init() {
        seleccionGestionAcademica = null;
        seleccionCarrera = null;
        seleccionCampus = null;
        seleccionConceptoPago = null;
    }

    public void reinit() {
        seleccionGestionAcademica = null;
        seleccionCarrera = null;
        seleccionCampus = null;
        seleccionConceptoPago = null;
    }

    public void generarReporte() throws IOException {
        if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus != null && seleccionConceptoPago != null) {
            this.insertarParametro("id_gestionacademica", seleccionGestionAcademica.getId_gestionacademica());
            this.insertarParametro("id_carrera", seleccionCarrera.getId_carrera());
            this.insertarParametro("id_campus", seleccionCampus.getId_campus());
            this.insertarParametro("id_conceptopago", seleccionConceptoPago.getId_conceptopago());

            toIngresosConceptoPago();

            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.READ, "Generación reporte de ingresos por concepto de pago", loginController.getUsr().toString()));
        }
    }

    public void toReporteIngresosConceptoPago() throws IOException {
        reinit();

        this.redireccionarViewId("/reportes/ingresos/conceptoPago/reporteIngresosConceptoPago");
    }

    public void toIngresosConceptoPago() throws IOException {
        this.redireccionarViewId("/reportes/ingresos/conceptoPago/ingresosConceptoPago");
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
     * @return the seleccionConceptoPago
     */
    public ConceptoPago getSeleccionConceptoPago() {
        return seleccionConceptoPago;
    }

    /**
     * @param seleccionConceptoPago the seleccionConceptoPago to set
     */
    public void setSeleccionConceptoPago(ConceptoPago seleccionConceptoPago) {
        this.seleccionConceptoPago = seleccionConceptoPago;
    }
}

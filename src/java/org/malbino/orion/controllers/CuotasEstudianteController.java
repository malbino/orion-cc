/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.controllers;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.malbino.orion.entities.Cuota;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Log;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.facades.CuotaFacade;
import org.malbino.orion.facades.InscritoFacade;
import org.malbino.orion.util.Fecha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tincho
 */
@Named("CuotasEstudianteController")
@SessionScoped
public class CuotasEstudianteController extends AbstractController implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(CuotasEstudianteController.class);

    @Inject
    LoginController loginController;
    @EJB
    InscritoFacade inscritoFacade;
    @EJB
    CuotaFacade cuotaFacade;

    private Estudiante seleccionEstudiante;
    private Inscrito seleccionInscrito;

    @PostConstruct
    public void init() {
        seleccionEstudiante = null;
        seleccionInscrito = null;
    }

    public void reinit() {
        seleccionEstudiante = null;
        seleccionInscrito = null;
    }

    public List<Inscrito> listaInscritos() {
        List<Inscrito> l = new ArrayList();
        if (seleccionEstudiante != null) {
            l = inscritoFacade.listaInscritosPersona(seleccionEstudiante.getId_persona());
        }
        return l;
    }

    public void editarCuota(Cuota cuota) {
        BigDecimal adeudado = cuota.getMonto().subtract(cuota.getPagado());
        if(adeudado.compareTo(BigDecimal.ZERO) < 0) {
            return;
        }
        
        if (cuotaFacade.edit(cuota)) {
            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.UPDATE, EntidadLog.CUOTA, cuota.getId_cuota(), "Actualización cuota", loginController.getUsr().toString()));
        }
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
     * @return the seleccionInscrito
     */
    public Inscrito getSeleccionInscrito() {
        return seleccionInscrito;
    }

    /**
     * @param seleccionInscrito the seleccionInscrito to set
     */
    public void setSeleccionInscrito(Inscrito seleccionInscrito) {
        this.seleccionInscrito = seleccionInscrito;
    }

}

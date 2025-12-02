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
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Log;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.enums.Turno;
import org.malbino.orion.facades.GrupoFacade;
import org.malbino.orion.facades.InscritoFacade;
import org.malbino.orion.util.Fecha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tincho
 */
@Named("InscritosController")
@SessionScoped
public class InscritosController extends AbstractController implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(InscritosController.class);

    @EJB
    GrupoFacade grupoFacade;
    @EJB
    InscritoFacade inscritoFacade;
    @Inject
    LoginController loginController;

    private GestionAcademica seleccionGestionAcademica;
    private Carrera seleccionCarrera;
    private Campus seleccionCampus;
    private Turno seleccionTurno;
    private String seleccionParalelo;

    private List<Inscrito> inscritos;
    private Inscrito seleccionInscrito;

    private String keyword;

    @PostConstruct
    public void init() {
        seleccionGestionAcademica = null;
        seleccionCarrera = null;
        seleccionCampus = null;
        seleccionTurno = null;
        seleccionParalelo = null;

        inscritos = new ArrayList<>();
        seleccionInscrito = null;

        keyword = null;
    }

    public void reinit() {
        if (seleccionGestionAcademica != null && seleccionCarrera == null && seleccionCampus == null && seleccionTurno == null && seleccionParalelo == null) {

            inscritos = inscritoFacade.listaInscritos(seleccionGestionAcademica.getId_gestionacademica());

        } else if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus == null && seleccionTurno == null && seleccionParalelo == null) {

            inscritos = inscritoFacade.listaInscritos(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera());

        } else if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus != null && seleccionTurno == null && seleccionParalelo == null) {

            inscritos = inscritoFacade.listaInscritos(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionCampus.getId_campus());

        } else if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus != null && seleccionTurno != null && seleccionParalelo == null) {

            inscritos = inscritoFacade.listaInscritos(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionCampus.getId_campus(), seleccionTurno);
            log.info("inscritos=" + inscritos.size());

        } else if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus != null && seleccionTurno != null && seleccionParalelo != null) {

            inscritos = inscritoFacade.listaInscritos(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionCampus.getId_campus(), seleccionTurno, seleccionParalelo);

        } else {

            inscritos = new ArrayList<>();
        }
        seleccionInscrito = null;

        keyword = null;
    }

    @Override
    public List<Carrera> listaCarreras() {
        List<Carrera> l = new ArrayList();
        if (seleccionGestionAcademica != null) {
            l = carreraFacade.listaCarreras();
        }
        return l;
    }

    @Override
    public Turno[] listaTurnos() {
        Turno[] turnos = new Turno[0];
        if (seleccionGestionAcademica != null && seleccionCarrera != null) {
            turnos = Turno.values();
        }
        return turnos;
    }

    public List<String> listaParalelos() {
        List<String> paralelos = new ArrayList<>();
        if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus != null && seleccionTurno != null) {
            paralelos = grupoFacade.listaParalelos(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionCampus.getId_campus(), seleccionTurno);
        }
        return paralelos;
    }

    public void buscar() {
        if (seleccionGestionAcademica != null && seleccionCarrera == null && seleccionTurno == null && seleccionParalelo == null) {
            inscritos = inscritoFacade.buscar(seleccionGestionAcademica.getId_gestionacademica(), keyword);
        } else if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionTurno == null && seleccionParalelo == null) {
            inscritos = inscritoFacade.buscar(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), keyword);
        } else if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionTurno == null && seleccionParalelo == null) {
            inscritos = inscritoFacade.buscar(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), keyword);
        } else if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionTurno != null && seleccionParalelo == null) {
            inscritos = inscritoFacade.buscar(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionTurno, keyword);
        } else if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionTurno != null && seleccionParalelo != null) {
            inscritos = inscritoFacade.buscar(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionTurno, seleccionParalelo, keyword);
        }
    }

    public void eliminarInscrito() throws IOException {
        if (inscritoFacade.remove(seleccionInscrito)) {
            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.DELETE, EntidadLog.INSCRITO, seleccionInscrito.getId_inscrito(), "Borrado inscrito", loginController.getUsr().toString()));

            this.reinit();
        }
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
     * @return the inscritos
     */
    public List<Inscrito> getInscritos() {
        return inscritos;
    }

    /**
     * @param inscritos the inscritos to set
     */
    public void setInscritos(List<Inscrito> inscritos) {
        this.inscritos = inscritos;
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

    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

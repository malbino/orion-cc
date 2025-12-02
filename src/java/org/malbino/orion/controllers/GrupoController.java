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
import org.malbino.orion.entities.Grupo;
import org.malbino.orion.entities.Log;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.enums.Turno;
import org.malbino.orion.facades.GrupoFacade;
import org.malbino.orion.facades.ModuloFacade;
import org.malbino.orion.facades.negocio.ProgramacionGruposFacade;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Named("GrupoController")
@SessionScoped
public class GrupoController extends AbstractController implements Serializable {

    @EJB
    GrupoFacade grupoFacade;
    @EJB
    ModuloFacade moduloFacade;
    @EJB
    ProgramacionGruposFacade programacionGruposFacade;
    @Inject
    LoginController loginController;

    private List<Grupo> grupos;
    private Grupo seleccionGrupo;

    private GestionAcademica seleccionGestionAcademica;
    private Carrera seleccionCarrera;
    private Campus seleccionCampus;
    private Modulo seleccionModulo;
    private Turno seleccionTurno;
    private Integer capacidad;

    private String keyword;

    @PostConstruct
    public void init() {
        grupos = new ArrayList();
        seleccionGrupo = null;

        seleccionGestionAcademica = null;
        seleccionCarrera = null;
        seleccionCampus = null;
        seleccionModulo = null;
        seleccionTurno = null;
        capacidad = null;

        keyword = null;
    }

    public void reinit() {
        if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus != null) {
            grupos = grupoFacade.listaGrupos(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionCampus.getId_campus());
        }
        seleccionGrupo = null;

        seleccionModulo = null;
        seleccionTurno = null;
        capacidad = null;

        keyword = null;
    }

    public List<Modulo> listaModulos() {
        List<Modulo> l = new ArrayList();
        if (seleccionCarrera != null) {
            l = moduloFacade.listaModulos(seleccionCarrera);
        }
        return l;
    }

    public void buscar() {
        if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus != null) {
            grupos = grupoFacade.buscar(keyword, seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionCampus.getId_campus());
        }
    }

    public long cantidadNotasGrupo(Grupo grupo) {
        return grupoFacade.cantidadNotasGrupo(grupo.getId_grupo());
    }

    public void programarGrupos() throws IOException {
        List<Grupo> grupos = programacionGruposFacade.programarGrupos(seleccionGestionAcademica, seleccionCarrera, seleccionCampus, seleccionModulo, seleccionTurno, capacidad);
        if (!grupos.isEmpty()) {
            //log
            for (Grupo grupo : grupos) {
                logFacade.create(new Log(Fecha.getDate(), EventoLog.CREATE, EntidadLog.GRUPO, grupo.getId_grupo(), "Creación grupo por programación de grupos", loginController.getUsr().toString()));
            }

            toGrupos();
        }
    }

    public void editarGrupo() throws IOException {
        if (grupoFacade.edit(seleccionGrupo)) {
            this.toGrupos();

            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.UPDATE, EntidadLog.GRUPO, seleccionGrupo.getId_grupo(), "Actualización grupo", loginController.getUsr().toString()));
        }
    }

    public void eliminarGrupo() throws IOException {
        long cantidadNotasGrupo = grupoFacade.cantidadNotasGrupo(seleccionGrupo.getId_grupo());
        if (cantidadNotasGrupo == 0) {
            if (grupoFacade.remove(seleccionGrupo)) {
                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.DELETE, EntidadLog.GRUPO, seleccionGrupo.getId_grupo(), "Borrado grupo", loginController.getUsr().toString()));

                this.toGrupos();
            }
        } else {
            this.mensajeDeError("No se puede eliminar grupos con estudiantes inscritos.");
        }
    }

    public void toProgramarGrupos() throws IOException {
        this.redireccionarViewId("/gestionesAcademicas/grupo/programarGrupos");
    }

    public void toEditarGrupo() throws IOException {
        this.redireccionarViewId("/gestionesAcademicas/grupo/editarGrupo");
    }

    public void toGrupos() throws IOException {
        reinit();

        this.redireccionarViewId("/gestionesAcademicas/grupo/grupos");
    }

    /**
     * @return the grupos
     */
    public List<Grupo> getGrupos() {
        return grupos;
    }

    /**
     * @param grupos the grupos to set
     */
    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    /**
     * @return the seleccionGrupo
     */
    public Grupo getSeleccionGrupo() {
        return seleccionGrupo;
    }

    /**
     * @param seleccionGrupo the seleccionGrupo to set
     */
    public void setSeleccionGrupo(Grupo seleccionGrupo) {
        this.seleccionGrupo = seleccionGrupo;
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
     * @return the capacidad
     */
    public Integer getCapacidad() {
        return capacidad;
    }

    /**
     * @param capacidad the capacidad to set
     */
    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
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

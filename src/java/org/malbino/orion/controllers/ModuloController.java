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
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.Log;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.facades.ModuloFacade;
import org.malbino.orion.facades.negocio.PlanEstudioFacade;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Named("ModuloController")
@SessionScoped
public class ModuloController extends AbstractController implements Serializable {

    @EJB
    ModuloFacade moduloFacade;
    @EJB
    PlanEstudioFacade planEstudioFacade;
    @Inject
    LoginController loginController;

    private List<Modulo> modulos;
    private Modulo nuevaModulo;
    private Modulo seleccionModulo;
    private Carrera seleccionCarrera;

    private String keyword;

    @PostConstruct
    public void init() {
        modulos = new ArrayList();
        nuevaModulo = new Modulo();
        seleccionModulo = null;

        keyword = null;
    }

    public void reinit() {
        if (seleccionCarrera != null) {
            modulos = moduloFacade.listaModulos(seleccionCarrera);
        }
        nuevaModulo = new Modulo();
        seleccionModulo = null;

        keyword = null;
    }

    public void buscar() {
        if (seleccionCarrera != null) {
            modulos = moduloFacade.buscar(keyword, seleccionCarrera.getId_carrera());
        }
    }

    public List<Modulo> listaModulosCrear() {
        return moduloFacade.listaModulos(seleccionCarrera);
    }

    public List<Modulo> listaModulosEditar() {
        return moduloFacade.listaModulos(seleccionModulo.getCarrera(), seleccionModulo.getId_modulo());
    }

    public void crearModulo() throws IOException {
        nuevaModulo.setCarrera(seleccionCarrera);
        if (moduloFacade.buscarPorCodigo(nuevaModulo.getCodigo(), nuevaModulo.getCarrera()).isEmpty()) {
            if (moduloFacade.create(nuevaModulo)) {
                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.CREATE, EntidadLog.MODULO, nuevaModulo.getId_modulo(), "Creación modulo", loginController.getUsr().toString()));

                this.toModulos();
            }
        } else {
            this.mensajeDeError("Modulo repetida.");
        }
    }

    public void editarModulo() throws IOException {
        if (moduloFacade.buscarPorCodigo(seleccionModulo.getCodigo(), seleccionModulo.getId_modulo(), seleccionModulo.getCarrera()).isEmpty()) {
            if (moduloFacade.edit(seleccionModulo)) {
                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.UPDATE, EntidadLog.MODULO, seleccionModulo.getId_modulo(), "Actualización modulo", loginController.getUsr().toString()));

                this.toModulos();
            }
        } else {
            this.mensajeDeError("Modulo repetida.");
        }
    }

    public void eliminarModulo() throws IOException {
        if (planEstudioFacade.eliminarModulo(seleccionModulo)) {
            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.DELETE, EntidadLog.MODULO, seleccionModulo.getId_modulo(), "Borrado modulo", loginController.getUsr().toString()));

            this.toModulos();
        }
    }

    public void toNuevaModulo() throws IOException {
        this.redireccionarViewId("/planesEstudio/modulo/nuevoModulo");
    }

    public void toEditarModulo() throws IOException {
        this.redireccionarViewId("/planesEstudio/modulo/editarModulo");
    }

    public void toModulos() throws IOException {
        reinit();

        this.redireccionarViewId("/planesEstudio/modulo/modulos");
    }

    /**
     * @return the modulos
     */
    public List<Modulo> getModulos() {
        return modulos;
    }

    /**
     * @param modulos the modulos to set
     */
    public void setModulos(List<Modulo> modulos) {
        this.modulos = modulos;
    }

    /**
     * @return the nuevaModulo
     */
    public Modulo getNuevaModulo() {
        return nuevaModulo;
    }

    /**
     * @param nuevaModulo the nuevaModulo to set
     */
    public void setNuevaModulo(Modulo nuevaModulo) {
        this.nuevaModulo = nuevaModulo;
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
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.malbino.orion.entities.Descuento;
import org.malbino.orion.entities.Log;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.facades.DescuentoFacade;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Named("DescuentoController")
@SessionScoped
public class DescuentoController extends AbstractController implements Serializable {

    @EJB
    DescuentoFacade descuentoFacade;
    @Inject
    LoginController loginController;

    private List<Descuento> descuentos;
    private Descuento nuevoDescuento;
    private Descuento seleccionDescuento;

    private String keyword;

    @PostConstruct
    public void init() {
        descuentos = descuentoFacade.listaDescuentos();
        nuevoDescuento = new Descuento();
        seleccionDescuento = null;

        keyword = null;
    }

    public void reinit() {
        descuentos = descuentoFacade.listaDescuentos();
        nuevoDescuento = new Descuento();
        seleccionDescuento = null;

        keyword = null;
    }

    public void buscar() {
        descuentos = descuentoFacade.buscar(keyword);
    }

    public void crearDescuento() throws IOException {
        if (descuentoFacade.buscarPorCodigo(nuevoDescuento.getCodigo()) == null) {
            if (descuentoFacade.create(nuevoDescuento)) {
                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.CREATE, EntidadLog.DESCUENTO, nuevoDescuento.getId_descuento(), "Creación descuento", loginController.getUsr().toString()));

                this.toDescuentos();
            }
        } else {
            this.mensajeDeError("Descuento repetido.");
        }
    }

    public void editarDescuento() throws IOException {
        if (descuentoFacade.buscarPorCodigo(seleccionDescuento.getCodigo(), seleccionDescuento.getId_descuento()) == null) {
            if (descuentoFacade.edit(seleccionDescuento)) {
                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.UPDATE, EntidadLog.DESCUENTO, seleccionDescuento.getId_descuento(), "Actualización descuento", loginController.getUsr().toString()));

                this.toDescuentos();
            }
        } else {
            this.mensajeDeError("Descuento repetido.");
        }
    }

    public void toNuevoDescuento() throws IOException {
        this.redireccionarViewId("/cajas/descuento/nuevoDescuento");
    }

    public void toEditarDescuento() throws IOException {
        this.redireccionarViewId("/cajas/descuento/editarDescuento");
    }

    public void toDescuentos() throws IOException {
        reinit();

        this.redireccionarViewId("/cajas/descuento/descuentos");
    }

    /**
     * @return the descuentos
     */
    public List<Descuento> getDescuentos() {
        return descuentos;
    }

    /**
     * @param descuentos the descuentos to set
     */
    public void setDescuentos(List<Descuento> descuentos) {
        this.descuentos = descuentos;
    }

    /**
     * @return the nuevoDescuento
     */
    public Descuento getNuevoDescuento() {
        return nuevoDescuento;
    }

    /**
     * @param nuevoDescuento the nuevoDescuento to set
     */
    public void setNuevoDescuento(Descuento nuevoDescuento) {
        this.nuevoDescuento = nuevoDescuento;
    }

    /**
     * @return the seleccionDescuento
     */
    public Descuento getSeleccionDescuento() {
        return seleccionDescuento;
    }

    /**
     * @param seleccionDescuento the seleccionDescuento to set
     */
    public void setSeleccionDescuento(Descuento seleccionDescuento) {
        this.seleccionDescuento = seleccionDescuento;
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

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
import org.malbino.orion.entities.ConceptoPago;
import org.malbino.orion.entities.Log;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.facades.ConceptoPagoFacade;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Named("ConceptoPagoController")
@SessionScoped
public class ConceptoPagoController extends AbstractController implements Serializable {

    @EJB
    ConceptoPagoFacade conceptoPagoFacade;
    @Inject
    LoginController loginController;

    private List<ConceptoPago> conceptosPago;
    private ConceptoPago nuevoConceptoPago;
    private ConceptoPago seleccionConceptoPago;

    private String keyword;

    @PostConstruct
    public void init() {
        conceptosPago = conceptoPagoFacade.listaConceptoPago();
        nuevoConceptoPago = new ConceptoPago();
        seleccionConceptoPago = null;

        keyword = null;
    }

    public void reinit() {
        conceptosPago = conceptoPagoFacade.listaConceptoPago();
        nuevoConceptoPago = new ConceptoPago();
        seleccionConceptoPago = null;

        keyword = null;
    }

    public void buscar() {
        conceptosPago = conceptoPagoFacade.buscar(keyword);
    }

    public void crearConceptoPago() throws IOException {
        if (conceptoPagoFacade.buscarPorCodigo(nuevoConceptoPago.getCodigo()) == null) {
            if (conceptoPagoFacade.create(nuevoConceptoPago)) {
                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.CREATE, EntidadLog.CONCEPTO_PAGO, nuevoConceptoPago.getId_conceptopago(), "Creación concepto de pago", loginController.getUsr().toString()));

                this.toConceptosPago();
            }
        } else {
            this.mensajeDeError("Concepto de pago repetido.");
        }
    }

    public void editarConceptoPago() throws IOException {
        if (conceptoPagoFacade.buscarPorCodigo(seleccionConceptoPago.getCodigo(), seleccionConceptoPago.getId_conceptopago()) == null) {
            if (conceptoPagoFacade.edit(seleccionConceptoPago)) {
                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.UPDATE, EntidadLog.CONCEPTO_PAGO, seleccionConceptoPago.getId_conceptopago(), "Actualización concepto de pago", loginController.getUsr().toString()));

                this.toConceptosPago();
            }
        } else {
            this.mensajeDeError("Concepto de pago repetido.");
        }
    }

    public void toNuevoConceptoPago() throws IOException {
        this.redireccionarViewId("/cajas/conceptoPago/nuevoConceptoPago");
    }

    public void toEditarConceptoPago() throws IOException {
        this.redireccionarViewId("/cajas/conceptoPago/editarConceptoPago");
    }

    public void toConceptosPago() throws IOException {
        reinit();

        this.redireccionarViewId("/cajas/conceptoPago/conceptosPago");
    }

    /**
     * @return the conceptosPago
     */
    public List<ConceptoPago> getConceptosPago() {
        return conceptosPago;
    }

    /**
     * @param conceptosPago the conceptosPago to set
     */
    public void setConceptosPago(List<ConceptoPago> conceptosPago) {
        this.conceptosPago = conceptosPago;
    }

    /**
     * @return the nuevoConceptoPago
     */
    public ConceptoPago getNuevoConceptoPago() {
        return nuevoConceptoPago;
    }

    /**
     * @param nuevoConceptoPago the nuevoConceptoPago to set
     */
    public void setNuevoConceptoPago(ConceptoPago nuevoConceptoPago) {
        this.nuevoConceptoPago = nuevoConceptoPago;
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

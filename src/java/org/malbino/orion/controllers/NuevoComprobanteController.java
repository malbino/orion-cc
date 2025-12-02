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
import org.malbino.orion.entities.Comprobante;
import org.malbino.orion.entities.ConceptoPago;
import org.malbino.orion.entities.Descuento;
import org.malbino.orion.entities.Detalle;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Log;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.enums.UnidadMedida;
import org.malbino.orion.facades.ConceptoPagoFacade;
import org.malbino.orion.facades.DescuentoFacade;
import org.malbino.orion.facades.InscritoFacade;
import org.malbino.orion.facades.ModuloFacade;
import org.malbino.orion.facades.negocio.CajasFacade;
import org.malbino.orion.util.Fecha;
import org.malbino.orion.util.Redondeo;
import org.primefaces.event.RowEditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tincho
 */
@Named("NuevoComprobanteController")
@SessionScoped
public class NuevoComprobanteController extends AbstractController implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(NuevoComprobanteController.class);

    @EJB
    CajasFacade cajasFacade;
    @EJB
    InscritoFacade inscritoFacade;
    @EJB
    DescuentoFacade descuentoFacade;
    @EJB
    ConceptoPagoFacade conceptoPagoFacade;
    @EJB
    ModuloFacade moduloFacade;
    @Inject
    LoginController loginController;

    private Estudiante seleccionEstudiante;
    private Inscrito seleccionInscrito;
    private Descuento seleccionDescuento;
    private List<ConceptoPago> seleccionConceptosPago;
    private List<Modulo> seleccionModulos;

    private Comprobante nuevoComprobante;
    private List<Detalle> detalles;
    private Detalle seleccionDetalle;

    @PostConstruct
    public void init() {
        seleccionEstudiante = null;
        seleccionInscrito = null;
        seleccionDescuento = null;
        seleccionConceptosPago = new ArrayList<>();
        seleccionModulos = new ArrayList<>();

        nuevoComprobante = new Comprobante();
        detalles = new ArrayList<>();
        seleccionDetalle = null;
    }

    public void reinit() {
        seleccionEstudiante = null;
        seleccionInscrito = null;
        seleccionDescuento = null;
        seleccionConceptosPago = new ArrayList<>();
        seleccionModulos = new ArrayList<>();

        nuevoComprobante = new Comprobante();
        detalles = new ArrayList<>();
        seleccionDetalle = null;
    }

    public List<Inscrito> listaInscritos() {
        List<Inscrito> l = new ArrayList();
        if (seleccionEstudiante != null) {
            l = inscritoFacade.listaInscritosPersona(seleccionEstudiante.getId_persona());
        }
        return l;
    }

    public List<Descuento> listaDescuentos() {
        return descuentoFacade.listaDescuentos();
    }

    public List<ConceptoPago> listaConceptosPago() {
        return conceptoPagoFacade.listaConceptoPago();
    }

    public List<Modulo> listaModulos() {
        List<Modulo> l = new ArrayList<>();

        if (seleccionInscrito != null) {
            l = moduloFacade.listaModulos(seleccionInscrito);
        }

        return l;
    }

    public void añadirDetalleConcepto() throws IOException {
        for (ConceptoPago conceptoPago : seleccionConceptosPago) {
            if (seleccionDescuento != null) {
                Detalle detalle = new Detalle();
                detalle.setCodigo(conceptoPago.getCodigo());
                detalle.setCantidad(1);
                detalle.setUnidadMedida(conceptoPago.getUnidadMedida());
                detalle.setDescripcion(conceptoPago.getDescripcion());
                detalle.setPrecioUnitario(conceptoPago.getPrecioUnitario());
                detalle.setConceptoPago(conceptoPago);

                Integer subtotalSinDescuento = detalle.getCantidad() * detalle.getPrecioUnitario();
                Double descuentoSinRedondear = subtotalSinDescuento.doubleValue() * seleccionDescuento.proporcionDescuento();
                log.info("descuentoSinRedondear=" + descuentoSinRedondear);
                Integer descuentoRedondeado = Redondeo.redondear_DOWN(descuentoSinRedondear, 0).intValue();
                log.info("descuentoRedondeado=" + descuentoRedondeado);
                detalle.setDescuento(descuentoRedondeado);

                Integer subtotalConDescuento = (detalle.getCantidad() * detalle.getPrecioUnitario()) - detalle.getDescuento();
                detalle.setSubtotal(subtotalConDescuento);

                detalles.add(detalle);
            } else {
                Detalle detalle = new Detalle();
                detalle.setCodigo(conceptoPago.getCodigo());
                detalle.setCantidad(1);
                detalle.setUnidadMedida(conceptoPago.getUnidadMedida());
                detalle.setDescripcion(conceptoPago.getDescripcion());
                detalle.setPrecioUnitario(conceptoPago.getPrecioUnitario());
                detalle.setConceptoPago(conceptoPago);
                
                detalle.setDescuento(0);

                Integer subtotal = (detalle.getCantidad() * detalle.getPrecioUnitario()) - detalle.getDescuento();
                detalle.setSubtotal(subtotal);

                detalles.add(detalle);
            }
        }

        toNuevoComprobante();
    }

    public void añadirDetalleModulo() throws IOException {
        for (Modulo modulo : seleccionModulos) {
            if (seleccionDescuento != null) {
                Detalle detalle = new Detalle();
                detalle.setCodigo(modulo.getCodigo());
                detalle.setCantidad(1);
                detalle.setUnidadMedida(UnidadMedida.UNIDAD_SERVICIOS.getNombre());
                detalle.setDescripcion(modulo.getNombre());
                detalle.setModulo(modulo);

                Integer precioUnitario = modulo.getCreditajeModulo() * seleccionInscrito.getCampus().getInstituto().getPrecioCredito();
                detalle.setPrecioUnitario(precioUnitario);

                Integer subtotalSinDescuento = detalle.getCantidad() * detalle.getPrecioUnitario();
                Double descuentoSinRedondear = subtotalSinDescuento.doubleValue() * seleccionDescuento.proporcionDescuento();
                log.info("descuentoSinRedondear=" + descuentoSinRedondear);
                Integer descuentoRedondeado = Redondeo.redondear_DOWN(descuentoSinRedondear, 0).intValue();
                log.info("descuentoRedondeado=" + descuentoRedondeado);
                detalle.setDescuento(descuentoRedondeado);

                Integer subtotalConDescuento = (detalle.getCantidad() * detalle.getPrecioUnitario()) - detalle.getDescuento();
                detalle.setSubtotal(subtotalConDescuento);

                detalles.add(detalle);
            } else {
                Detalle detalle = new Detalle();
                detalle.setCodigo(modulo.getCodigo());
                detalle.setCantidad(1);
                detalle.setUnidadMedida(UnidadMedida.UNIDAD_SERVICIOS.getNombre());
                detalle.setDescripcion(modulo.getNombre());
                detalle.setModulo(modulo);

                Integer precioUnitario = modulo.getCreditajeModulo() * seleccionInscrito.getCampus().getInstituto().getPrecioCredito();
                detalle.setPrecioUnitario(precioUnitario);

                detalle.setDescuento(0);

                Integer subtotal = (detalle.getCantidad() * detalle.getPrecioUnitario()) - detalle.getDescuento();
                detalle.setSubtotal(subtotal);

                detalles.add(detalle);
            }
        }

        toNuevoComprobante();
    }

    public void eliminarDetalle() {
        if (seleccionDetalle != null) {
            detalles.remove(seleccionDetalle);
        }
    }

    public void limpiarDetalle() {
        detalles.clear();
    }

    public void onRowEdit(RowEditEvent event) {
        Detalle detalle = (Detalle) event.getObject();

        if (seleccionDescuento != null) {
            Integer subtotalSinDescuento = detalle.getCantidad() * detalle.getPrecioUnitario();
            Double descuentoSinRedondear = subtotalSinDescuento.doubleValue() * seleccionDescuento.proporcionDescuento();
            log.info("descuentoSinRedondear=" + descuentoSinRedondear);
            Integer descuentoRedondeado = Redondeo.redondear_DOWN(descuentoSinRedondear, 0).intValue();
            log.info("descuentoRedondeado=" + descuentoRedondeado);
            detalle.setDescuento(descuentoRedondeado);

            Integer subtotalConDescuento = (detalle.getCantidad() * detalle.getPrecioUnitario()) - detalle.getDescuento();
            detalle.setSubtotal(subtotalConDescuento);
        } else {
            detalle.setDescuento(0);

            Integer subtotalSinDescuento = detalle.getCantidad() * detalle.getPrecioUnitario();
            detalle.setSubtotal(subtotalSinDescuento);
        }
    }

    public String totalDescuentos() {
        String s;

        Integer totalDescuentos = 0;
        for (Detalle detalle : detalles) {
            totalDescuentos = totalDescuentos + detalle.getDescuento();
        }
        s = Redondeo.formatear_csm(totalDescuentos);

        return s;
    }

    public String totalComprobante() {
        String s;

        Integer totalComprobante = 0;
        for (Detalle detalle : detalles) {
            totalComprobante = totalComprobante + detalle.getSubtotal();
        }
        s = Redondeo.formatear_csm(totalComprobante);

        return s;
    }

    public void aplicarDescuento() {
        if (seleccionDescuento != null) {
            for (Detalle detalle : detalles) {
                Integer subtotalSinDescuento = detalle.getCantidad() * detalle.getPrecioUnitario();
                Double descuentoSinRedondear = subtotalSinDescuento.doubleValue() * seleccionDescuento.proporcionDescuento();
                log.info("descuentoSinRedondear=" + descuentoSinRedondear);
                Integer descuentoRedondeado = Redondeo.redondear_DOWN(descuentoSinRedondear, 0).intValue();
                log.info("descuentoRedondeado=" + descuentoRedondeado);
                detalle.setDescuento(descuentoRedondeado);

                Integer subtotalConDescuento = (detalle.getCantidad() * detalle.getPrecioUnitario()) - detalle.getDescuento();
                detalle.setSubtotal(subtotalConDescuento);
            }
        } else {
            for (Detalle detalle : detalles) {
                detalle.setDescuento(0);

                Integer subtotalSinDescuento = detalle.getCantidad() * detalle.getPrecioUnitario();
                detalle.setSubtotal(subtotalSinDescuento);
            }
        }
    }

    public void nuevoComprobante() throws IOException {
        nuevoComprobante.setFecha(Fecha.getDate());
        nuevoComprobante.setValido(true);
        nuevoComprobante.setInscrito(seleccionInscrito);
        nuevoComprobante.setUsuario(loginController.getUsr());
        if (!detalles.isEmpty()) {
            if (cajasFacade.nuevoComprobante(nuevoComprobante, detalles)) {
                //log
                logFacade.create(new Log(Fecha.getDate(), EventoLog.CREATE, EntidadLog.COMPROBANTE, nuevoComprobante.getId_comprobante(), "Creación comprobante", loginController.getUsr().toString()));

                this.insertarParametro("id_comprobante", nuevoComprobante.getId_comprobante());

                this.reinit();

                this.toComprobantePago();
            }
        } else {
            this.mensajeDeError("Ningun concepto seleccionado.");
        }
    }

    public void toConceptosPago() throws IOException {
        seleccionConceptosPago.clear();

        this.redireccionarViewId("/cajas/nuevoComprobante/conceptosPago");
    }

    public void toModulos() throws IOException {
        seleccionModulos.clear();

        this.redireccionarViewId("/cajas/nuevoComprobante/modulos");
    }

    public void toComprobantePago() throws IOException {
        this.redireccionarViewId("/cajas/nuevoComprobante/comprobantePago");
    }

    public void toNuevoComprobante() throws IOException {
        this.redireccionarViewId("/cajas/nuevoComprobante/nuevoComprobante");
    }

    /**
     * @return the nuevoComprobante
     */
    public Comprobante getNuevoComprobante() {
        return nuevoComprobante;
    }

    /**
     * @param nuevoComprobante the nuevoComprobante to set
     */
    public void setNuevoComprobante(Comprobante nuevoComprobante) {
        this.nuevoComprobante = nuevoComprobante;
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
     * @return the detalles
     */
    public List<Detalle> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
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
     * @return the seleccionConceptosPago
     */
    public List<ConceptoPago> getSeleccionConceptosPago() {
        return seleccionConceptosPago;
    }

    /**
     * @param seleccionConceptosPago the seleccionConceptosPago to set
     */
    public void setSeleccionConceptosPago(List<ConceptoPago> seleccionConceptosPago) {
        this.seleccionConceptosPago = seleccionConceptosPago;
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
     * @return the seleccionModulos
     */
    public List<Modulo> getSeleccionModulos() {
        return seleccionModulos;
    }

    /**
     * @param seleccionModulos the seleccionModulos to set
     */
    public void setSeleccionModulos(List<Modulo> seleccionModulos) {
        this.seleccionModulos = seleccionModulos;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.facades.negocio;

import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.malbino.orion.entities.Comprobante;
import org.malbino.orion.entities.Cuota;
import org.malbino.orion.entities.Detalle;
import org.malbino.orion.facades.ComprobanteFacade;
import org.malbino.orion.util.Generador;

/**
 *
 * @author Tincho
 */
@Stateless
@LocalBean
public class CajasFacade {

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    @EJB
    ComprobanteFacade comprobanteFacade;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean nuevoComprobante(Comprobante comprobante, List<Detalle> detalles) {
        Integer maximoCodigo = comprobanteFacade.maximoNumero(comprobante.getFecha());

        Integer numero;
        if (maximoCodigo == null) {
            numero = 1;
        } else {
            numero = maximoCodigo + 1;
        }
        comprobante.setNumero(numero);

        String codigo = Generador.generarCodigoComprobante();
        comprobante.setCodigo(codigo);

        em.persist(comprobante);

        for (Detalle detalle : detalles) {
            detalle.setComprobante(comprobante);
            em.persist(detalle);

            Cuota cuota = detalle.getCuota();
            if (cuota != null) {
                cuota.setPagado(detalle.getSubtotal());
                em.merge(cuota);
            }
        }

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean anularComprobante(Comprobante comprobante) {
        comprobante.setValido(false);
        em.merge(comprobante);

        List<Detalle> detalles = comprobante.getDetalles();
        for (Detalle detalle : detalles) {
            Cuota cuota = detalle.getCuota();
            if (cuota != null) {
                cuota.setPagado(BigDecimal.ZERO);
                em.merge(cuota);
            }
        }

        return true;
    }

}

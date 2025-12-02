/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.facades.negocio;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.malbino.orion.entities.Comprobante;
import org.malbino.orion.entities.Detalle;
import org.malbino.orion.facades.ComprobanteFacade;
import org.malbino.orion.facades.DetalleFacade;
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
    @EJB
    DetalleFacade detalleFacade;

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
        }

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean anularComprobante(Comprobante comprobante) {
        comprobante.setValido(false);
        em.merge(comprobante);

        return true;
    }

}

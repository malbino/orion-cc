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
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.facades.ModuloFacade;

/**
 *
 * @author Tincho
 */
@Stateless
@LocalBean
public class PlanEstudioFacade {

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    @EJB
    ModuloFacade moduloFacade;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean eliminarModulo(Modulo prerequisito) {
        List<Modulo> modulos = moduloFacade.listaModulos(prerequisito.getId_modulo());
        for (Modulo modulo : modulos) {
            modulo.getPrerequisitos().remove(prerequisito);

            moduloFacade.edit(modulo);
            moduloFacade.getEntityManager().flush();
        }

        moduloFacade.remove(prerequisito);

        return true;
    }
}

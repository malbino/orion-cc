/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.malbino.orion.entities.Cuota;
import org.malbino.orion.entities.Inscrito;

/**
 *
 * @author malbino
 */
@Stateless
@LocalBean
public class CuotaFacade extends AbstractFacade<Cuota> {

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    public CuotaFacade() {
        super(Cuota.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Cuota> listaCuotasAdeudadas(int id_inscrito) {
        List<Cuota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT c FROM Cuota c JOIN c.inscrito i WHERE i.id_inscrito=:id_inscrito AND (c.monto - c.pagado) > 0 ORDER BY c.codigo");
            q.setParameter("id_inscrito", id_inscrito);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }
}

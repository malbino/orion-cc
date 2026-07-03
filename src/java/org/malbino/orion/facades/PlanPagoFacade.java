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
import org.malbino.orion.entities.PlanPago;
import org.slf4j.LoggerFactory;

/**
 *
 * @author malbino
 */
@Stateless
@LocalBean
public class PlanPagoFacade extends AbstractFacade<PlanPago> {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PlanPagoFacade.class);

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    public PlanPagoFacade() {
        super(PlanPago.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<PlanPago> listaPlanesPago(int id_carrera) {
        List<PlanPago> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT pp FROM PlanPago pp JOIN pp.carrera c WHERE c.id_carrera=:id_carrera ORDER BY pp.nombre");
            q.setParameter("id_carrera", id_carrera);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }
}

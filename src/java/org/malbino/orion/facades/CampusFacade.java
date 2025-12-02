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
import org.malbino.orion.entities.Campus;

/**
 *
 * @author malbino
 */
@Stateless
@LocalBean
public class CampusFacade extends AbstractFacade<Campus> {

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    public CampusFacade() {
        super(Campus.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Campus buscarPorSucursal(String ciudad) {
        Campus c = null;

        try {
            Query q = em.createQuery("SELECT c FROM Campus c WHERE c.ciudad=:ciudad");
            q.setParameter("ciudad", ciudad);

            c = (Campus) q.getSingleResult();
        } catch (Exception e) {

        }

        return c;
    }

    public Campus buscarPorSucursal(String ciudad, int id_campus) {
        Campus c = null;

        try {
            Query q = em.createQuery("SELECT c FROM Campus c WHERE c.ciudad=:ciudad AND c.id_campus!=:id_campus");
            q.setParameter("ciudad", ciudad);
            q.setParameter("id_campus", id_campus);

            c = (Campus) q.getSingleResult();
        } catch (Exception e) {

        }

        return c;
    }

    public List<Campus> listaCampus() {
        List<Campus> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT c FROM Campus c ORDER BY c.ciudad");

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Campus> buscar(String keyword) {
        List<Campus> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT c FROM Campus c WHERE "
                    + "LOWER(c.ciudad) LIKE LOWER(:keyword) OR "
                    + "LOWER(c.direccion) LIKE LOWER(:keyword) "
                    + "ORDER BY c.ciudad");
            q.setParameter("keyword", "%" + keyword + "%");

            l = q.getResultList();
        } catch (Exception e) {
        }

        return l;
    }
}

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
import org.malbino.orion.entities.Descuento;

/**
 *
 * @author malbino
 */
@Stateless
@LocalBean
public class DescuentoFacade extends AbstractFacade<Descuento> {

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    public DescuentoFacade() {
        super(Descuento.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public Descuento buscarPorCodigo(String codigo) {
        Descuento d = null;

        try {
            Query q = em.createQuery("SELECT d FROM Descuento d WHERE d.codigo=:codigo");
            q.setParameter("codigo", codigo);

            d = (Descuento) q.getSingleResult();
        } catch (Exception e) {

        }

        return d;
    }

    public Descuento buscarPorCodigo(String codigo, int id_descuento) {
        Descuento c = null;

        try {
            Query q = em.createQuery("SELECT d FROM Descuento d WHERE d.codigo=:codigo AND d.id_descuento!=:id_descuento");
            q.setParameter("codigo", codigo);
            q.setParameter("id_descuento", id_descuento);

            c = (Descuento) q.getSingleResult();
        } catch (Exception e) {

        }

        return c;
    }

    public List<Descuento> listaDescuentos() {
        List<Descuento> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT d FROM Descuento d ORDER BY d.codigo");

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Descuento> buscar(String keyword) {
        List<Descuento> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT d FROM Descuento d WHERE "
                    + "LOWER(d.codigo) LIKE LOWER(:keyword) OR "
                    + "LOWER(d.descripcion) LIKE LOWER(:keyword) "
                    + "ORDER BY d.codigo");
            q.setParameter("keyword", "%" + keyword + "%");

            l = q.getResultList();
        } catch (Exception e) {
        }

        return l;
    }
}

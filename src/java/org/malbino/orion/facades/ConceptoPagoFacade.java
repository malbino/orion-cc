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
import org.malbino.orion.entities.ConceptoPago;

/**
 *
 * @author malbino
 */
@Stateless
@LocalBean
public class ConceptoPagoFacade extends AbstractFacade<ConceptoPago> {

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    public ConceptoPagoFacade() {
        super(ConceptoPago.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ConceptoPago buscarPorCodigo(String codigo) {
        ConceptoPago c = null;

        try {
            Query q = em.createQuery("SELECT c FROM ConceptoPago c WHERE c.codigo=:codigo");
            q.setParameter("codigo", codigo);

            c = (ConceptoPago) q.getSingleResult();
        } catch (Exception e) {

        }

        return c;
    }

    public ConceptoPago buscarPorCodigo(String codigo, int id_conceptopago) {
        ConceptoPago c = null;

        try {
            Query q = em.createQuery("SELECT c FROM ConceptoPago c WHERE c.codigo=:codigo AND c.id_conceptopago!=:id_conceptopago");
            q.setParameter("codigo", codigo);
            q.setParameter("id_conceptopago", id_conceptopago);

            c = (ConceptoPago) q.getSingleResult();
        } catch (Exception e) {

        }

        return c;
    }

    public List<ConceptoPago> listaConceptoPago() {
        List<ConceptoPago> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT c FROM ConceptoPago c ORDER BY c.codigo");

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<ConceptoPago> buscar(String keyword) {
        List<ConceptoPago> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT c FROM ConceptoPago c WHERE "
                    + "LOWER(c.codigo) LIKE LOWER(:keyword) OR "
                    + "LOWER(c.descripcion) LIKE LOWER(:keyword) "
                    + "ORDER BY c.codigo");
            q.setParameter("keyword", "%" + keyword + "%");

            l = q.getResultList();
        } catch (Exception e) {
        }

        return l;
    }
}

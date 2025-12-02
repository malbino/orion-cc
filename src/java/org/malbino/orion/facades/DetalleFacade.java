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
import org.malbino.orion.entities.Detalle;
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Modulo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author malbino
 */
@Stateless
@LocalBean
public class DetalleFacade extends AbstractFacade<Detalle> {

    private static final Logger log = LoggerFactory.getLogger(DetalleFacade.class);

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    public DetalleFacade() {
        super(Detalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Detalle> listaDetalles(int id_comprobante) {
        List<Detalle> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT d FROM Detalle d JOIN d.comprobante c WHERE c.id_comprobante=:id_comprobante ORDER BY d.codigo");
            q.setParameter("id_comprobante", id_comprobante);

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    public List<Detalle> kardexEconomico(int id_persona, int id_carrera) {
        List<Detalle> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT d FROM Detalle d JOIN d.comprobante c JOIN c.inscrito i JOIN i.estudiante e JOIN i.carrera a JOIN i.gestionAcademica ga WHERE e.id_persona=:id_persona AND a.id_carrera=:id_carrera AND c.valido=:valido ORDER BY ga.gestion, ga.periodo, c.fecha, d.codigo");
            q.setParameter("id_persona", id_persona);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("valido", Boolean.TRUE);

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    public List<Detalle> ingresosConceptoPago(int id_gestionacademica, int id_carrera, int id_campus, int id_conceptopago) {
        List<Detalle> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT d FROM Detalle d JOIN d.comprobante c JOIN c.inscrito i JOIN i.gestionAcademica ga JOIN i.carrera a JOIN i.campus m JOIN d.conceptoPago cp WHERE ga.id_gestionacademica=:id_gestionacademica AND a.id_carrera=:id_carrera AND m.id_campus=:id_campus AND cp.id_conceptopago=:id_conceptopago AND c.valido=:valido ORDER BY ga.gestion, ga.periodo, c.fecha, d.codigo");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);
            q.setParameter("id_conceptopago", id_conceptopago);
            q.setParameter("valido", Boolean.TRUE);

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    public List<Detalle> ingresosModulo(int id_gestionacademica, int id_carrera, int id_campus, int id_modulo) {
        List<Detalle> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT d FROM Detalle d JOIN d.comprobante c JOIN c.inscrito i JOIN i.gestionAcademica ga JOIN i.carrera a JOIN i.campus m JOIN d.modulo o WHERE ga.id_gestionacademica=:id_gestionacademica AND a.id_carrera=:id_carrera AND m.id_campus=:id_campus AND o.id_modulo=:id_modulo AND c.valido=:valido ORDER BY ga.gestion, ga.periodo, c.fecha, d.codigo");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);
            q.setParameter("id_modulo", id_modulo);
            q.setParameter("valido", Boolean.TRUE);

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    public Boolean modulosPagados(Inscrito inscrito, List<Modulo> modulos) {
        Boolean b = Boolean.FALSE;

        try {
            Query q = em.createQuery("SELECT m FROM Detalle d JOIN d.comprobante c JOIN c.inscrito i JOIN d.modulo m WHERE i.id_inscrito=:id_inscrito AND c.valido=:valido");
            q.setParameter("id_inscrito", inscrito.getId_inscrito());
            q.setParameter("valido", Boolean.TRUE);
            List<Modulo> modulosPagados = q.getResultList();

            if (modulosPagados.containsAll(modulos)) {
                b = Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error(e.toString());
        }

        return b;
    }
}

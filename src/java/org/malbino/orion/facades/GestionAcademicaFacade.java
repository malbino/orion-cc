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
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.enums.Condicion;
import org.malbino.orion.enums.ModalidadEvaluacion;
import org.malbino.orion.enums.Periodo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author malbino
 */
@Stateless
@LocalBean
public class GestionAcademicaFacade extends AbstractFacade<GestionAcademica> {

    private static final Logger log = LoggerFactory.getLogger(GestionAcademicaFacade.class);

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    public GestionAcademicaFacade() {
        super(GestionAcademica.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public GestionAcademica buscarPorCodigoRegimen(Integer gestion, Periodo periodo) {
        GestionAcademica ga = null;

        try {
            Query q = em.createQuery("SELECT ga FROM GestionAcademica ga WHERE ga.gestion=:gestion AND ga.periodo=:periodo");
            q.setParameter("gestion", gestion);
            q.setParameter("periodo", periodo);

            ga = (GestionAcademica) q.getSingleResult();
        } catch (Exception e) {

        }

        return ga;
    }

    public GestionAcademica buscarPorCodigoRegimen(Integer gestion, Periodo periodo, int id_gestionacademica) {
        GestionAcademica ga = null;

        try {
            Query q = em.createQuery("SELECT ga FROM GestionAcademica ga WHERE ga.gestion=:gestion AND ga.periodo=:periodo AND ga.id_gestionacademica!=:id_gestionacademica");
            q.setParameter("gestion", gestion);
            q.setParameter("periodo", periodo);
            q.setParameter("id_gestionacademica", id_gestionacademica);

            ga = (GestionAcademica) q.getSingleResult();
        } catch (Exception e) {

        }

        return ga;
    }

    public List<GestionAcademica> listaGestionAcademica(boolean vigente) {
        List<GestionAcademica> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT ga FROM GestionAcademica ga WHERE ga.vigente=:vigente ORDER BY ga.gestion, ga.periodo");
            q.setParameter("vigente", vigente);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<GestionAcademica> listaGestionAcademica() {
        List<GestionAcademica> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT ga FROM GestionAcademica ga ORDER BY ga.gestion, ga.periodo");

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    public List<GestionAcademica> buscar(String keyword) {
        List<GestionAcademica> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT ga FROM GestionAcademica ga WHERE "
                    + "LOWER(CAST(ga.gestion AS CHAR)) LIKE LOWER(:keyword) "
                    + "ORDER BY ga.gestion, ga.periodo");
            q.setParameter("keyword", "%" + keyword + "%");

            l = q.getResultList();
        } catch (Exception e) {
        }

        return l;
    }

    public List<GestionAcademica> listaGestionAcademicaDashboard() {
        List<GestionAcademica> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT ga FROM GestionAcademica ga ORDER BY ga.gestion, ga.periodo");

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Integer> listaGestiones() {
        List<Integer> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT DISTINCT ga.gestion FROM GestionAcademica ga ORDER BY ga.gestion, ga.periodo");

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<GestionAcademica> listaGestionAcademica(ModalidadEvaluacion modalidadEvaluacion1, boolean vigente) {
        List<GestionAcademica> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT ga FROM GestionAcademica ga WHERE ga.modalidadEvaluacion=:modalidadEvaluacion1 AND ga.vigente=:vigente ORDER BY ga.gestion, ga.periodo");
            q.setParameter("modalidadEvaluacion1", modalidadEvaluacion1);
            q.setParameter("vigente", vigente);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<GestionAcademica> listaGestionAcademica(ModalidadEvaluacion modalidadEvaluacion1, ModalidadEvaluacion modalidadEvaluacion2, boolean vigente) {
        List<GestionAcademica> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT ga FROM GestionAcademica ga WHERE (ga.modalidadEvaluacion=:modalidadEvaluacion1 OR ga.modalidadEvaluacion=:modalidadEvaluacion2) AND ga.vigente=:vigente ORDER BY ga.gestion, ga.periodo");
            q.setParameter("modalidadEvaluacion1", modalidadEvaluacion1);
            q.setParameter("modalidadEvaluacion2", modalidadEvaluacion2);
            q.setParameter("vigente", vigente);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<GestionAcademica> listaGestionAcademica(ModalidadEvaluacion modalidadEvaluacion1, ModalidadEvaluacion modalidadEvaluacion2, ModalidadEvaluacion modalidadEvaluacion3, boolean vigente) {
        List<GestionAcademica> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT ga FROM GestionAcademica ga WHERE (ga.modalidadEvaluacion=:modalidadEvaluacion1 OR ga.modalidadEvaluacion=:modalidadEvaluacion2 OR ga.modalidadEvaluacion=:modalidadEvaluacion3) AND ga.vigente=:vigente ORDER BY ga.gestion, ga.periodo");
            q.setParameter("modalidadEvaluacion1", modalidadEvaluacion1);
            q.setParameter("modalidadEvaluacion2", modalidadEvaluacion2);
            q.setParameter("modalidadEvaluacion3", modalidadEvaluacion3);
            q.setParameter("vigente", vigente);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<GestionAcademica> listaGestionesAcademicas(Estudiante estudiante, Carrera carrera) {
        List<GestionAcademica> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT DISTINCT ga FROM Nota n JOIN n.modulo m JOIN n.gestionAcademica ga WHERE n.estudiante=:estudiante AND m.carrera=:carrera AND n.condicion=:condicion ORDER BY m.numero");
            q.setParameter("estudiante", estudiante);
            q.setParameter("carrera", carrera);
            q.setParameter("condicion", Condicion.APROBADO);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }
}

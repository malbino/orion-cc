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
import org.malbino.orion.entities.Grupo;
import org.malbino.orion.enums.Turno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author malbino
 */
@Stateless
@LocalBean
public class GrupoFacade extends AbstractFacade<Grupo> {

    private static final Logger log = LoggerFactory.getLogger(GrupoFacade.class);

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    public GrupoFacade() {
        super(Grupo.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Grupo> listaGrupos(int id_gestionacademica, int id_carrera, int id_campus) {
        List<Grupo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT g FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN m.carrera c JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus ORDER BY m.numero, g.turno, g.codigo");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public Long cantidadGrupos(int id_gestionacademica, int id_modulo, int id_campus, Turno turno) {
        Long l = 0l;

        try {
            Query q = em.createQuery("SELECT COUNT(g) FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND m.id_modulo=:id_modulo AND a.id_campus=:id_campus AND g.turno=:turno");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_modulo", id_modulo);
            q.setParameter("id_campus", id_campus);
            q.setParameter("turno", turno);

            l = (Long) q.getSingleResult();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    public List<Grupo> buscar(String keyword, int id_gestionacademica, int id_carrera, int id_campus) {
        List<Grupo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT g FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN m.carrera c JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus AND "
                    + "(LOWER(g.codigo) LIKE LOWER(:keyword) OR "
                    + "LOWER(m.nombre) LIKE LOWER(:keyword)) "
                    + "ORDER BY m.numero, g.turno, g.codigo");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);
            q.setParameter("keyword", "%" + keyword + "%");

            l = q.getResultList();
        } catch (Exception e) {
        }

        return l;
    }

    public List<Grupo> listaGruposAbiertos(int id_gestionacademica, int id_carrera, int id_campus, int id_modulo) {
        List<Grupo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT g FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN m.carrera c JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus AND m.id_modulo=:id_modulo AND g.abierto=TRUE ORDER BY g.turno, g.codigo");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);
            q.setParameter("id_modulo", id_modulo);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public long cantidadNotasGrupo(int id_grupo) {
        long l = 0;

        try {
            Query q = em.createQuery("SELECT COUNT(n) FROM Nota n JOIN n.grupo g WHERE g.id_grupo=:id_grupo");
            q.setParameter("id_grupo", id_grupo);

            l = (long) q.getSingleResult();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Grupo> listaGruposEmpleado(int id_gestionacademica, int id_carrera, int id_campus, int id_persona) {
        List<Grupo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT g FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN m.carrera c JOIN g.empleado e JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus AND e.id_persona=:id_persona ORDER BY m.nombre, g.turno");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);
            q.setParameter("id_persona", id_persona);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Grupo> listaGruposAbiertos(int id_gestionacademica, int id_campus, int id_modulo, String codigo) {
        List<Grupo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT g FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND a.id_campus=:id_campus AND m.id_modulo=:id_modulo AND g.codigo=:codigo AND g.abierto=TRUE ORDER BY g.codigo");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_campus", id_campus);
            q.setParameter("id_modulo", id_modulo);
            q.setParameter("codigo", codigo);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public Long cantidadGrupos(int id_carrera) {
        Long l = 0l;

        try {
            Query q = em.createQuery("SELECT COUNT(DISTINCT(g.codigo)) FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN m.carrera c WHERE ga.vigente=TRUE AND c.id_carrera=:id_carrera");
            q.setParameter("id_carrera", id_carrera);

            l = (Long) q.getSingleResult();
        } catch (Exception e) {

        }

        return l;
    }

    public List<String> listaParalelos(int id_gestionacademica, int id_carrera) {
        List<String> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT DISTINCT g.codigo FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN m.carrera c WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera ORDER BY g.codigo");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<String> listaParalelos(int id_gestionacademica, int id_carrera, int id_campus, Turno turno) {
        List<String> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT DISTINCT g.codigo FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN m.carrera c JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus AND g.turno=:turno ORDER BY g.codigo");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);
            q.setParameter("turno", turno);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Grupo> listaGrupos(int id_gestionacademica, int id_carrera, int id_campus, Turno turno) {
        List<Grupo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT g FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN m.carrera c JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus AND g.turno=:turno ORDER BY m.nombre, g.codigo");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);
            q.setParameter("turno", turno);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Grupo> listaGrupos(int id_gestionacademica, int id_carrera, int id_campus, Turno turno, String paralelo) {
        List<Grupo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT g FROM Grupo g JOIN g.gestionAcademica ga JOIN g.modulo m JOIN m.carrera c JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus AND g.turno=:turno AND g.codigo=:paralelo ORDER BY m.nombre, g.codigo");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);
            q.setParameter("turno", turno);
            q.setParameter("paralelo", paralelo);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }
}

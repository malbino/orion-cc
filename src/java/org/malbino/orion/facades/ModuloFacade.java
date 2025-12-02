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
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.enums.Condicion;

/**
 *
 * @author malbino
 */
@Stateless
@LocalBean
public class ModuloFacade extends AbstractFacade<Modulo> {

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    public ModuloFacade() {
        super(Modulo.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Modulo> buscarPorCodigo(String codigo, Carrera carrera) {
        List<Modulo> l = new ArrayList<>();

        try {
            Query q = em.createQuery("SELECT m FROM Modulo m WHERE m.codigo=:codigo AND m.carrera=:carrera");
            q.setParameter("codigo", codigo);
            q.setParameter("carrera", carrera);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Modulo> buscarPorCodigo(String codigo, int id_modulo, Carrera carrera) {
        List<Modulo> l = new ArrayList<>();

        try {
            Query q = em.createQuery("SELECT m FROM Modulo m WHERE m.codigo=:codigo AND m.id_modulo!=:id_modulo AND m.carrera=:carrera");
            q.setParameter("codigo", codigo);
            q.setParameter("id_modulo", id_modulo);
            q.setParameter("carrera", carrera);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Modulo> listaModulos(Carrera carrera) {
        List<Modulo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT m FROM Modulo m WHERE m.carrera=:carrera ORDER BY m.numero");
            q.setParameter("carrera", carrera);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Modulo> listaModulos(Carrera carrera, int id_modulo) {
        List<Modulo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT m FROM Modulo m WHERE m.carrera=:carrera AND m.id_modulo!=:id_modulo ORDER BY m.numero");
            q.setParameter("carrera", carrera);
            q.setParameter("id_modulo", id_modulo);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Modulo> buscar(String keyword, int id_carrera) {
        List<Modulo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT m FROM Modulo m JOIN m.carrera c WHERE c.id_carrera=:id_carrera AND "
                    + "(LOWER(m.codigo) LIKE LOWER(:keyword) OR "
                    + "LOWER(m.nombre) LIKE LOWER(:keyword)) "
                    + "ORDER BY m.numero");
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("keyword", "%" + keyword + "%");

            l = q.getResultList();
        } catch (Exception e) {
        }

        return l;
    }

    public List<Modulo> listaModuloAprobadas(int id_persona, int id_carrera) {
        List<Modulo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT m FROM Nota n JOIN n.estudiante e JOIN n.modulo m JOIN m.carrera c WHERE e.id_persona=:id_persona AND c.id_carrera=:id_carrera AND n.condicion=:condicion ORDER BY m.numero");
            q.setParameter("id_persona", id_persona);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("condicion", Condicion.APROBADO);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Modulo> listaModuloAprobadas(Estudiante estudiante, Carrera carrera) {
        List<Modulo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT m FROM Nota n JOIN n.modulo m WHERE n.estudiante=:estudiante AND m.carrera=:carrera AND n.condicion=:condicion ORDER BY m.numero");
            q.setParameter("estudiante", estudiante);
            q.setParameter("carrera", carrera);
            q.setParameter("condicion", Condicion.APROBADO);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public Long cantidadMaximaModulosNivel(Carrera carrera) {
        Long l = 0l;

        try {
            Query q = em.createQuery("SELECT COUNT(m) AS cantidad FROM Modulo m WHERE m.carrera=:carrera ORDER BY cantidad DESC");
            q.setParameter("carrera", carrera);
            q.setMaxResults(1);

            l = (Long) q.getSingleResult();
        } catch (Exception e) {

        }

        return l;
    }

    public Long cantidadModulos(Carrera carrera) {
        Long l = 0l;

        try {
            Query q = em.createQuery("SELECT COUNT(m) FROM Modulo m WHERE m.carrera=:carrera");
            q.setParameter("carrera", carrera);

            l = (Long) q.getSingleResult();
        } catch (Exception e) {

        }

        return l;
    }

    public Long cantidadHoras(Carrera carrera) {
        Long l = 0l;

        try {
            Query q = em.createQuery("SELECT SUM(m.horas) FROM Modulo m WHERE m.carrera=:carrera");
            q.setParameter("carrera", carrera);

            l = (Long) q.getSingleResult();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Modulo> listaModulos(int id_prerequisito) {
        List<Modulo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT m FROM Modulo m JOIN m.prerequisitos p WHERE p.id_modulo=:id_prerequisito");
            q.setParameter("id_prerequisito", id_prerequisito);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Modulo> listaModulos(Inscrito inscrito) {
        List<Modulo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT m FROM Modulo m WHERE m.carrera=:carrera AND m.id_modulo NOT IN (SELECT m.id_modulo FROM Detalle d JOIN d.comprobante c JOIN d.modulo m WHERE c.inscrito=:inscrito AND c.valido=:valido) ORDER BY m.numero");
            q.setParameter("carrera", inscrito.getCarrera());
            q.setParameter("inscrito", inscrito);
            q.setParameter("valido", Boolean.TRUE);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }
}

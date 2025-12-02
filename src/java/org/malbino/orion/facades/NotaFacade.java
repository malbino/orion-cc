/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.facades;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Nota;
import org.malbino.orion.enums.Condicion;
import org.malbino.orion.enums.Modalidad;
import org.malbino.orion.enums.TipoNota;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author malbino
 */
@Stateless
@LocalBean
public class NotaFacade extends AbstractFacade<Nota> {

    private static final Logger log = LoggerFactory.getLogger(NotaFacade.class);

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    public NotaFacade() {
        super(Nota.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<Nota> listaNotas(int id_inscrito) {
        List<Nota> l = new ArrayList();
        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.inscrito i JOIN n.modulo m WHERE i.id_inscrito=:id_inscrito ORDER BY m.numero");
            q.setParameter("id_inscrito", id_inscrito);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Nota> historialAcademico(Estudiante estudiante, Carrera carrera) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m WHERE n.estudiante=:estudiante AND m.carrera=:carrera ORDER BY ga.gestion, ga.periodo, m.numero");
            q.setParameter("estudiante", estudiante);
            q.setParameter("carrera", carrera);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Nota> listaNotasGrupo(int id_grupo) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.grupo g JOIN n.estudiante e WHERE g.id_grupo=:id_grupo ORDER BY e.primerApellido, e.segundoApellido, e.nombre");
            q.setParameter("id_grupo", id_grupo);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Nota> buscar(String keyword, int id_grupo) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.grupo g JOIN n.estudiante e WHERE g.id_grupo=:id_grupo AND "
                    + "(LOWER(e.primerApellido) LIKE LOWER(:keyword) OR "
                    + "LOWER(e.segundoApellido) LIKE LOWER(:keyword) OR "
                    + "LOWER(e.nombre) LIKE LOWER(:keyword)) "
                    + "ORDER BY e.primerApellido, e.segundoApellido, e.nombre");
            q.setParameter("id_grupo", id_grupo);
            q.setParameter("keyword", "%" + keyword + "%");

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Nota> listaNotasPruebaRecuperacion(Inscrito inscrito, int id_persona) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.inscrito i JOIN n.grupo g JOIN g.empleado e JOIN n.modulo m WHERE i.id_inscrito=:id_inscrito AND e.id_persona=:id_persona AND n.notaFinal>=:notaMinimaPruebaRecuperacion AND n.notaFinal<:notaMinimaAprobacion ORDER BY m.numero");
            q.setParameter("id_inscrito", inscrito.getId_inscrito());
            q.setParameter("id_persona", id_persona);
            q.setParameter("notaMinimaPruebaRecuperacion", inscrito.getGestionAcademica().getModalidadEvaluacion().getNotaMinimmaPruebaRecuperacion());
            q.setParameter("notaMinimaAprobacion", inscrito.getGestionAcademica().getModalidadEvaluacion().getNotaMinimaAprobacion());

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Nota> listaNotasPruebaRecuperacion(Inscrito inscrito) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.inscrito i JOIN n.modulo m WHERE i.id_inscrito=:id_inscrito AND n.notaFinal>=:notaMinimaPruebaRecuperacion AND n.notaFinal<:notaMinimaAprobacion ORDER BY m.numero");
            q.setParameter("id_inscrito", inscrito.getId_inscrito());
            q.setParameter("notaMinimaPruebaRecuperacion", inscrito.getGestionAcademica().getModalidadEvaluacion().getNotaMinimmaPruebaRecuperacion());
            q.setParameter("notaMinimaAprobacion", inscrito.getGestionAcademica().getModalidadEvaluacion().getNotaMinimaAprobacion());

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Nota> listaNotasReprobadas(GestionAcademica gestionAcademica, Carrera carrera, Estudiante estudiante) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.modulo m WHERE n.gestionAcademica=:gestionAcademica AND m.carrera=:carrera AND n.estudiante=:estudiante AND n.condicion=:condicion ORDER BY m.numero");
            q.setParameter("gestionAcademica", gestionAcademica);
            q.setParameter("carrera", carrera);
            q.setParameter("estudiante", estudiante);
            q.setParameter("condicion", Condicion.REPROBADO);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Nota> listaNotasModulo(int id_gestionacademica, int id_persona, int id_modulo) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m JOIN n.estudiante e WHERE ga.id_gestionacademica=:id_gestionacademica AND e.id_persona=:id_persona AND m.id_modulo=:id_modulo ORDER BY m.numero");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_persona", id_persona);
            q.setParameter("id_modulo", id_modulo);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Nota> reporteHistorialAcademico(Estudiante estudiante, Carrera carrera) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m WHERE n.estudiante=:estudiante AND m.carrera=:carrera AND n.condicion=:condicion ORDER BY ga.gestion, ga.periodo, m.numero");
            q.setParameter("estudiante", estudiante);
            q.setParameter("carrera", carrera);
            q.setParameter("condicion", Condicion.APROBADO);

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    public List<Nota> reporteHistorialAcademicoRecuperatorio(Estudiante estudiante, Carrera carrera) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.modulo m WHERE n.estudiante=:estudiante AND m.carrera=:carrera AND n.condicion=:condicion AND n.recuperatorio IS NOT NULL ORDER BY m.numero");
            q.setParameter("estudiante", estudiante);
            q.setParameter("carrera", carrera);
            q.setParameter("condicion", Condicion.APROBADO);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public GestionAcademica inicioFormacion(Carrera carrera, Estudiante estudiante) {
        GestionAcademica ga = null;

        try {
            Query q = em.createQuery("SELECT DISTINCT ga FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m WHERE m.carrera=:carrera AND n.estudiante=:estudiante AND n.condicion=:condicion ORDER BY ga.gestion, ga.periodo");
            q.setParameter("carrera", carrera);
            q.setParameter("estudiante", estudiante);
            q.setParameter("condicion", Condicion.APROBADO);
            q.setMaxResults(1);

            ga = (GestionAcademica) q.getSingleResult();
        } catch (Exception e) {

        }

        return ga;
    }

    public GestionAcademica finFormacion(Carrera carrera, Estudiante estudiante) {
        GestionAcademica ga = null;

        try {
            Query q = em.createQuery("SELECT DISTINCT ga FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m WHERE m.carrera=:carrera AND n.estudiante=:estudiante AND n.condicion=:condicion ORDER BY ga.gestion DESC, ga.periodo DESC");
            q.setParameter("carrera", carrera);
            q.setParameter("estudiante", estudiante);
            q.setParameter("condicion", Condicion.APROBADO);
            q.setMaxResults(1);

            ga = (GestionAcademica) q.getSingleResult();
        } catch (Exception e) {

        }

        return ga;
    }

    public Long cantidadNotasAprobadas(Carrera carrera, Estudiante estudiante) {
        Long l = 0l;

        try {
            Query q = em.createQuery("SELECT COUNT(n) FROM Nota n JOIN n.modulo m WHERE m.carrera=:carrera AND n.estudiante=:estudiante AND n.condicion=:condicion");
            q.setParameter("carrera", carrera);
            q.setParameter("estudiante", estudiante);
            q.setParameter("condicion", Condicion.APROBADO);

            l = (Long) q.getSingleResult();
        } catch (Exception e) {

        }

        return l;
    }

    public double promedioReporteHistorialAcademico(Estudiante estudiante, Carrera carrera) {
        double d = 0.0;

        try {
            Query q = em.createQuery("SELECT AVG(COALESCE(n.recuperatorio, n.notaFinal)) FROM Nota n JOIN n.modulo m WHERE n.estudiante=:estudiante AND m.carrera=:carrera AND n.condicion=:condicion");
            q.setParameter("estudiante", estudiante);
            q.setParameter("carrera", carrera);
            q.setParameter("condicion", Condicion.APROBADO);

            d = (double) q.getSingleResult();
        } catch (Exception e) {

        }

        return d;
    }

    public double promedioBoletinNotas(Estudiante estudiante, Carrera carrera, GestionAcademica gestionAcademica) {
        double d = 0.0;

        try {
            Query q = em.createQuery("SELECT AVG(COALESCE(n.recuperatorio, n.notaFinal)) FROM Nota n JOIN n.modulo m WHERE n.estudiante=:estudiante AND m.carrera=:carrera AND n.gestionAcademica=:gestionAcademica AND n.condicion=:condicion");
            q.setParameter("estudiante", estudiante);
            q.setParameter("carrera", carrera);
            q.setParameter("gestionAcademica", gestionAcademica);
            q.setParameter("condicion", Condicion.APROBADO);

            d = (double) q.getSingleResult();
        } catch (Exception e) {

        }

        return d;
    }

    public Date fechaInicio(int id_persona) {
        Date d = null;

        try {
            Query q = em.createQuery("SELECT MIN(ga.inicio) FROM Nota n JOIN n.gestionAcademica ga JOIN n.estudiante e WHERE e.id_persona=:id_persona");
            q.setParameter("id_persona", id_persona);

            d = (Date) q.getSingleResult();
        } catch (Exception e) {

        }

        return d;
    }

    public Long cantidadInscritos(int id_grupo) {
        Long l = 0l;

        try {
            Query q = em.createQuery("SELECT COUNT(n) FROM Nota n JOIN n.grupo g WHERE g.id_grupo=:id_grupo");
            q.setParameter("id_grupo", id_grupo);

            l = (Long) q.getSingleResult();
        } catch (Exception e) {

        }

        return l;
    }

    public Long cantidadCondicion(int id_grupo, Condicion condicion) {
        Long l = 0l;

        try {
            Query q = em.createQuery("SELECT COUNT(n) FROM Nota n JOIN n.grupo g WHERE g.id_grupo=:id_grupo AND n.condicion=:condicion");
            q.setParameter("id_grupo", id_grupo);
            q.setParameter("condicion", condicion);

            l = (Long) q.getSingleResult();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Nota> listaNotasFaltantesSemestral(GestionAcademica gestionAcademica, int id_carrera, int id_campus) {
        List<Nota> l = new ArrayList();
        try {
            String qlString = null;
            if (gestionAcademica.getModalidadEvaluacion().getCantidadParciales() == 2) {
                qlString = "SELECT n FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m JOIN m.carrera c JOIN n.estudiante e JOIN n.grupo g JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus AND n.modalidad=:modalidad AND (n.nota1 IS NULL OR n.nota2 IS NULL) ORDER BY e.primerApellido, e.segundoApellido, e.nombre";

            } else if (gestionAcademica.getModalidadEvaluacion().getCantidadParciales() == 3) {
                qlString = "SELECT n FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m JOIN m.carrera c JOIN n.estudiante e JOIN n.grupo g JOIN g.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus AND n.modalidad=:modalidad AND (n.nota1 IS NULL OR n.nota2 IS NULL OR n.nota3 IS NULL) ORDER BY e.primerApellido, e.segundoApellido, e.nombre";
            }

            Query q = em.createQuery(qlString);
            q.setParameter("id_gestionacademica", gestionAcademica.getId_gestionacademica());
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);
            q.setParameter("modalidad", Modalidad.REGULAR);

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    public List<Nota> listaNotasFaltantesAnual(int id_gestionacademica, int id_carrera) {
        List<Nota> l = new ArrayList();
        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m JOIN m.carrera c JOIN n.estudiante e WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND n.modalidad=:modalidad AND (n.nota1 IS NULL OR n.nota2 IS NULL OR n.nota3 IS NULL OR n.nota4 IS NULL) ORDER BY e.primerApellido, e.segundoApellido, e.nombre");
            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("curricular", Boolean.TRUE);
            q.setParameter("modalidad", Modalidad.REGULAR);

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }

    public List<Nota> listaRegistroNotasModular(int id_gestionacademica, int id_carrera, int id_campus, TipoNota tipoNota) {
        List<Nota> l = new ArrayList();
        try {
            Query q = null;

            if (tipoNota.equals(TipoNota.PRIMER_PARCIAL_MODULAR_2P)) {
                q = em.createQuery("SELECT n FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m JOIN m.carrera c JOIN n.estudiante e JOIN n.inscrito i JOIN i.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus AND n.modalidad=:modalidad AND n.nota1 IS NULL ORDER BY e.primerApellido, e.segundoApellido, e.nombre");
            } else if (tipoNota.equals(TipoNota.SEGUNDO_PARCIAL_MODULAR_2P)) {
                q = em.createQuery("SELECT n FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m JOIN m.carrera c JOIN n.estudiante e JOIN n.inscrito i JOIN i.campus a WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND a.id_campus=:id_campus AND n.modalidad=:modalidad AND n.nota2 IS NULL ORDER BY e.primerApellido, e.segundoApellido, e.nombre");
            }

            q.setParameter("id_gestionacademica", id_gestionacademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("id_campus", id_campus);
            q.setParameter("modalidad", Modalidad.REGULAR);

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    public List<Nota> listaRegistroNotasRecuperatorio(Inscrito inscrito) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.inscrito i JOIN n.modulo m WHERE i.id_inscrito=:id_inscrito AND n.notaFinal>=:notaMinimaPruebaRecuperacion AND n.notaFinal<:notaMinimaAprobacion AND n.recuperatorio IS NULL ORDER BY m.numero");
            q.setParameter("id_inscrito", inscrito.getId_inscrito());
            q.setParameter("notaMinimaPruebaRecuperacion", inscrito.getGestionAcademica().getModalidadEvaluacion().getNotaMinimmaPruebaRecuperacion());
            q.setParameter("notaMinimaAprobacion", inscrito.getGestionAcademica().getModalidadEvaluacion().getNotaMinimaAprobacion());

            l = q.getResultList();
        } catch (Exception e) {

        }

        return l;
    }
}

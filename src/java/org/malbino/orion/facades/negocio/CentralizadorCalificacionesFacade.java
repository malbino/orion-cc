/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.facades.negocio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.malbino.orion.entities.Campus;
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.entities.Nota;
import org.malbino.orion.enums.Turno;
import org.malbino.orion.pojos.centralizador.EstudianteCentralizador;
import org.malbino.orion.pojos.centralizador.NotaCentralizador;
import org.malbino.orion.pojos.centralizador.Centralizador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tincho
 */
@Stateless
@LocalBean
public class CentralizadorCalificacionesFacade {

    private static final Logger log = LoggerFactory.getLogger(CentralizadorCalificacionesFacade.class);

    private static final int CAN_MIN_MOD = 10;

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Estudiante> listaEstudiantes(int id_gestioncademica, int id_carrera, Turno turno, String paralelo) {
        List<Estudiante> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT DISTINCT e FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m JOIN m.carrera c JOIN n.grupo g JOIN n.estudiante e WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND g.turno=:turno AND g.codigo=:paralelo ORDER BY e.primerApellido, e.segundoApellido, e.nombre");
            q.setParameter("id_gestionacademica", id_gestioncademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("turno", turno);
            q.setParameter("paralelo", paralelo);

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Modulo> listaModulos(int id_carrera) {
        List<Modulo> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT m FROM Modulo m JOIN m.carrera c WHERE c.id_carrera=:id_carrera ORDER BY m.numero");
            q.setParameter("id_carrera", id_carrera);

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Nota> listaNotas(int id_gestioncademica, int id_carrera, Turno turno, String paralelo) {
        List<Nota> l = new ArrayList();

        try {
            Query q = em.createQuery("SELECT n FROM Nota n JOIN n.gestionAcademica ga JOIN n.modulo m JOIN m.carrera c JOIN n.grupo g JOIN n.estudiante e WHERE ga.id_gestionacademica=:id_gestionacademica AND c.id_carrera=:id_carrera AND g.turno=:turno AND g.codigo=:paralelo ORDER BY e.primerApellido, e.segundoApellido, e.nombre, m.numero");
            q.setParameter("id_gestionacademica", id_gestioncademica);
            q.setParameter("id_carrera", id_carrera);
            q.setParameter("turno", turno);
            q.setParameter("paralelo", paralelo);

            l = q.getResultList();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return l;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Centralizador centralizadorCalificaciones(GestionAcademica seleccionGestionAcademica, Carrera seleccionCarrera, Campus seleccionCampus, Turno seleccionTurno, String seleccionParalelo) {
        Centralizador seguimiento = new Centralizador();
        seguimiento.setInstituto(seleccionCampus.getInstituto().toString());
        seguimiento.setGestion(seleccionGestionAcademica.toString());
        seguimiento.setCarrera(seleccionCarrera.toString());
        seguimiento.setCampus(seleccionCampus.getCiudad());
        seguimiento.setTurno(seleccionTurno.toString());
        seguimiento.setParalelo(seleccionParalelo.toString());

        List<Modulo> listaModulos = listaModulos(seleccionCarrera.getId_carrera());

        int cantidadModulos = listaModulos.size();
        int cantidadMaximaModulos = CAN_MIN_MOD;
        while (cantidadModulos > cantidadMaximaModulos) {
            cantidadMaximaModulos += CAN_MIN_MOD;
        }

        String[] modulosSeguimiento = new String[cantidadMaximaModulos];
        for (int i = 0; i < modulosSeguimiento.length; i++) {
            if (i < listaModulos.size()) {
                Modulo modulo = listaModulos.get(i);
                modulosSeguimiento[i] = modulo.getNombre();
            } else {
                modulosSeguimiento[i] = "";
            }
        }
        seguimiento.setModulosCentralizador(modulosSeguimiento);

        List<Estudiante> listaEstudiantes = listaEstudiantes(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionTurno, seleccionParalelo);
        List<Nota> listaNotas = listaNotas(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionTurno, seleccionParalelo);
        EstudianteCentralizador[] estudiantesSeguimiento = new EstudianteCentralizador[listaEstudiantes.size()];
        for (int i = 0; i < listaEstudiantes.size(); i++) {
            Estudiante estudiante = listaEstudiantes.get(i);

            EstudianteCentralizador estudianteSeguimiento = new EstudianteCentralizador();
            estudianteSeguimiento.setNumero(i + 1);
            estudianteSeguimiento.setEstudiante(estudiante.toString());
            estudianteSeguimiento.setObservaciones("");

            NotaCentralizador[] notasSeguimiento = new NotaCentralizador[cantidadMaximaModulos];
            for (int j = 0; j < notasSeguimiento.length; j++) {
                if (j < listaModulos.size()) {
                    Modulo modulo = listaModulos.get(j);

                    List<Nota> collect = listaNotas.stream().filter(n -> n.getEstudiante().equals(estudiante) && n.getModulo().equals(modulo)).collect(Collectors.toList());
                    Iterator<Nota> iteratorCollect = collect.iterator();
                    if (iteratorCollect.hasNext()) {
                        Nota nota = iteratorCollect.next();

                        NotaCentralizador notaSeguimiento = new NotaCentralizador();
                        if (nota.getNotaFinal() != null) {
                            notaSeguimiento.setNotaFinal(nota.getNotaFinal());
                        }
                        if (nota.getRecuperatorio() != null) {
                            notaSeguimiento.setRecuperatorio(nota.getRecuperatorio());
                        }

                        notasSeguimiento[j] = notaSeguimiento;
                    } else {
                        NotaCentralizador notaSeguimiento = new NotaCentralizador();

                        notasSeguimiento[j] = notaSeguimiento;
                    }
                } else {
                    NotaCentralizador notaSeguimiento = new NotaCentralizador();

                    notasSeguimiento[j] = notaSeguimiento;
                }
            }
            estudianteSeguimiento.setNotasCentralizador(notasSeguimiento);

            estudiantesSeguimiento[i] = estudianteSeguimiento;
        }
        seguimiento.setEstudiantesCentralizador(estudiantesSeguimiento);

        return seguimiento;
    }

}

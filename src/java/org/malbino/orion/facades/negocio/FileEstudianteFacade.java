/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.facades.negocio;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.CarreraEstudiante;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.entities.Nota;
import org.malbino.orion.entities.Rol;
import org.malbino.orion.enums.Condicion;
import org.malbino.orion.facades.CarreraEstudianteFacade;
import org.malbino.orion.facades.EstudianteFacade;
import org.malbino.orion.facades.ModuloFacade;
import org.malbino.orion.facades.RolFacade;
import org.malbino.orion.util.Constantes;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author Tincho
 */
@Stateless
@LocalBean
public class FileEstudianteFacade {

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    @EJB
    EstudianteFacade estudianteFacade;
    @EJB
    RolFacade rolFacade;
    @EJB
    ModuloFacade moduloFacade;
    @EJB
    CarreraEstudianteFacade carreraEstudianteFacade;


    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean registrarEstudiante(Estudiante estudiante, List<CarreraEstudiante> seleccionCarrerasEstudiante) {
        Integer maximaMatricula = estudianteFacade.maximaMatricula(estudiante.getFecha());
        Integer matricula;
        if (maximaMatricula == null) {
            matricula = (Fecha.extrarAño(estudiante.getFecha()) * 10000) + 1;
        } else {
            matricula = maximaMatricula + 1;
        }
        estudiante.setMatricula(matricula);
        estudiante.setUsuario(String.valueOf(matricula));
        List<Rol> roles = new ArrayList();
        roles.add(rolFacade.find(Constantes.ID_ROL_ESTUDIANTE));
        estudiante.setRoles(roles);
        em.persist(estudiante);
        em.flush();

        for (CarreraEstudiante nuevaCarreraEstudiante : seleccionCarrerasEstudiante) {
            nuevaCarreraEstudiante.getCarreraEstudianteId().setId_persona(estudiante.getId_persona());

            CarreraEstudiante carreraEstudiante = carreraEstudianteFacade.find(nuevaCarreraEstudiante.getCarreraEstudianteId());
            if (carreraEstudiante == null) {
                carreraEstudianteFacade.create(nuevaCarreraEstudiante);
            }
        }

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean editarEstudiante(Estudiante estudiante, List<CarreraEstudiante> seleccionCarrerasEstudiante) {
        estudianteFacade.edit(estudiante);

        List<CarreraEstudiante> carrerasEstudiante = carreraEstudianteFacade.listaCarrerasEstudiante(estudiante.getId_persona());
        for (CarreraEstudiante carreraEstudiante : carrerasEstudiante) {
            carreraEstudianteFacade.remove(carreraEstudiante);
            carreraEstudianteFacade.getEntityManager().flush();
        }

        for (CarreraEstudiante nuevaCarreraEstudiante : seleccionCarrerasEstudiante) {
            CarreraEstudiante carreraEstudiante = carreraEstudianteFacade.find(nuevaCarreraEstudiante.getCarreraEstudianteId());
            if (carreraEstudiante == null) {
                carreraEstudianteFacade.create(nuevaCarreraEstudiante);
            }
        }

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Modulo> oferta(Carrera carrera, Estudiante estudiante) {
        List<Modulo> oferta = new ArrayList();

        CarreraEstudiante.CarreraEstudianteId carreraEstudianteId = new CarreraEstudiante.CarreraEstudianteId();
        carreraEstudianteId.setId_carrera(carrera.getId_carrera());
        carreraEstudianteId.setId_persona(estudiante.getId_persona());
        CarreraEstudiante carreraEstudiante = carreraEstudianteFacade.find(carreraEstudianteId);
        if (carreraEstudiante != null) {
            // modulos carrera
            List<Modulo> listaModulosCarrera = moduloFacade.listaModulos(carrera);

            // quitando modulos aprobadas
            List<Modulo> listaModuloAprobadas = moduloFacade.listaModuloAprobadas(estudiante.getId_persona(), carrera.getId_carrera());
            listaModulosCarrera.removeAll(listaModuloAprobadas);

            // control de prerequisitos
            for (Modulo modulo : listaModulosCarrera) {
                List<Modulo> prerequisitos = modulo.getPrerequisitos();
                if (listaModuloAprobadas.containsAll(prerequisitos)) {
                    oferta.add(modulo);
                }
            }
        }

        return oferta;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean crearNota(Nota nota) {
        if (nota.getRecuperatorio() != null) {
            if (nota.getRecuperatorio() >= nota.getGestionAcademica().getModalidadEvaluacion().getNotaMinimaAprobacion()) {
                nota.setCondicion(Condicion.APROBADO);
            } else {
                nota.setCondicion(Condicion.REPROBADO);
            }
        } else if (nota.getNotaFinal() != null) {
            if (nota.getNotaFinal() >= nota.getGestionAcademica().getModalidadEvaluacion().getNotaMinimaAprobacion()) {
                nota.setCondicion(Condicion.APROBADO);
            } else if (nota.getNotaFinal() == 0) {
                nota.setCondicion(Condicion.ABANDONO);
            } else {
                nota.setCondicion(Condicion.REPROBADO);
            }
        }

        em.persist(nota);

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean editarNota(Nota nota) {
        if (nota.getRecuperatorio() != null) {
            if (nota.getRecuperatorio() >= nota.getGestionAcademica().getModalidadEvaluacion().getNotaMinimaAprobacion()) {
                nota.setCondicion(Condicion.APROBADO);
            } else {
                nota.setCondicion(Condicion.REPROBADO);
            }
        } else if (nota.getNotaFinal() != null) {
            if (nota.getNotaFinal() >= nota.getGestionAcademica().getModalidadEvaluacion().getNotaMinimaAprobacion()) {
                nota.setCondicion(Condicion.APROBADO);
            } else if (nota.getNotaFinal() == 0) {
                nota.setCondicion(Condicion.ABANDONO);
            } else {
                nota.setCondicion(Condicion.REPROBADO);
            }
        }

        em.merge(nota);

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean eliminarEstudiante(Estudiante estudiante) {
        List<CarreraEstudiante> carrerasEstudiante = carreraEstudianteFacade.listaCarrerasEstudiante(estudiante.getId_persona());
        for (CarreraEstudiante carreraEstudiante : carrerasEstudiante) {
            carreraEstudianteFacade.remove(carreraEstudiante);
            carreraEstudianteFacade.getEntityManager().flush();
        }

        estudianteFacade.remove(estudiante);

        return true;
    }
}

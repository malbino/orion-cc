/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.facades.negocio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.malbino.orion.entities.Campus;
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.CarreraEstudiante;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.entities.Grupo;
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.entities.Nota;
import org.malbino.orion.entities.Rol;

import org.malbino.orion.enums.Tipo;
import org.malbino.orion.facades.CarreraEstudianteFacade;
import org.malbino.orion.facades.EstudianteFacade;
import org.malbino.orion.facades.GrupoFacade;
import org.malbino.orion.facades.InscritoFacade;
import org.malbino.orion.facades.ModuloFacade;
import org.malbino.orion.facades.NotaFacade;
import org.malbino.orion.facades.RolFacade;
import org.malbino.orion.util.Constantes;
import org.malbino.orion.util.Fecha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tincho
 */
@Stateless
@LocalBean
public class InscripcionesFacade {

    private static final Logger log = LoggerFactory.getLogger(InscripcionesFacade.class);

    @PersistenceContext(unitName = "orionPU")
    private EntityManager em;

    @EJB
    InscritoFacade inscritoFacade;
    @EJB
    ModuloFacade moduloFacade;
    @EJB
    RolFacade rolFacade;
    @EJB
    GrupoFacade grupoFacade;
    @EJB
    NotaFacade notaFacade;
    @EJB
    EstudianteFacade estudianteFacade;
    @EJB
    CarreraEstudianteFacade carreraEstudianteFacade;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean registrarEstudianteNuevo(Estudiante estudiante, CarreraEstudiante carreraEstudiante, GestionAcademica gestionAcademica, Campus campus) {
        // estudiante
        log.info("estudiante=" + estudiante);
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

        // carreraestudiante
        log.info("carreraEstudiante=" + carreraEstudiante);
        carreraEstudiante.getCarreraEstudianteId().setId_persona(estudiante.getId_persona());
        carreraEstudianteFacade.create(carreraEstudiante);

        // inscrito
        log.info("gestionAcademica=" + gestionAcademica);
        Date fecha = estudiante.getFecha();
        Integer maximoNumero = inscritoFacade.maximoNumero(gestionAcademica.getId_gestionacademica(), carreraEstudiante.getCarrera().getId_carrera());
        Long maximoCodigo = inscritoFacade.maximoCodigo(gestionAcademica.getId_gestionacademica(), carreraEstudiante.getCarrera().getId_carrera());
        Long codigo;
        Integer numero;
        if (maximoNumero == null && maximoCodigo == null) {
            codigo = (Long.parseLong(gestionAcademica.getGestion().toString() + gestionAcademica.getPeriodo().getPeriodoEntero().toString() + carreraEstudiante.getCarrera().getId_carrera().toString()) * 10000) + 1;
            numero = 1;
        } else {
            codigo = maximoCodigo + 1;
            numero = maximoNumero + 1;
        }
        Inscrito inscrito = new Inscrito(fecha, Tipo.NUEVO, codigo, numero, estudiante, carreraEstudiante.getCarrera(), gestionAcademica, campus);
        em.persist(inscrito);

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean registrarEstudianteRegular(Estudiante estudiante, Carrera carrera, GestionAcademica gestionAcademica, Campus campus) {
        // estudiante
        if (estudiante.getMatricula() == null && estudiante.getUsuario() == null) {
            Date fecha = notaFacade.fechaInicio(estudiante.getId_persona());
            if (fecha == null) {
                estudiante.setFecha(estudiante.getFechaInscripcion()); //fecha de inscripcion
            } else {
                estudiante.setFecha(fecha);
            }

            Integer maximaMatricula = estudianteFacade.maximaMatricula(estudiante.getFecha());
            Integer matricula;
            if (maximaMatricula == null) {
                matricula = (Fecha.extrarAño(estudiante.getFecha()) * 10000) + 1;
            } else {
                matricula = maximaMatricula + 1;
            }
            estudiante.setMatricula(matricula);
            estudiante.setUsuario(String.valueOf(matricula));
        }
        em.merge(estudiante);

        // inscrito
        Date fecha = estudiante.getFechaInscripcion(); //fecha de inscripcion
        Integer maximoNumero = inscritoFacade.maximoNumero(gestionAcademica.getId_gestionacademica(), carrera.getId_carrera());
        Long maximoCodigo = inscritoFacade.maximoCodigo(gestionAcademica.getId_gestionacademica(), carrera.getId_carrera());
        Long codigo;
        Integer numero;
        if (maximoNumero == null && maximoCodigo == null) {
            codigo = (Long.parseLong(gestionAcademica.getGestion().toString() + gestionAcademica.getPeriodo().getPeriodoEntero().toString() + carrera.getId_carrera().toString()) * 10000) + 1;
            numero = 1;
        } else {
            codigo = maximoCodigo + 1;
            numero = maximoNumero + 1;
        }
        Inscrito inscrito = new Inscrito(fecha, Tipo.REGULAR, codigo, numero, estudiante, carrera, gestionAcademica, campus);
        em.persist(inscrito);

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean cambioCarrera(Estudiante estudiante, CarreraEstudiante carreraEstudiante, GestionAcademica gestionAcademica, Campus campus) {
        // estudiante
        if (estudiante.getMatricula() == null && estudiante.getUsuario() == null) {
            Date fecha = notaFacade.fechaInicio(estudiante.getId_persona());
            if (fecha == null) {
                estudiante.setFecha(estudiante.getFechaInscripcion()); // fecha de inscripcion
            } else {
                estudiante.setFecha(fecha);
            }

            Integer maximaMatricula = estudianteFacade.maximaMatricula(estudiante.getFecha());
            Integer matricula;
            if (maximaMatricula == null) {
                matricula = (Fecha.extrarAño(estudiante.getFecha()) * 10000) + 1;
            } else {
                matricula = maximaMatricula + 1;
            }
            estudiante.setMatricula(matricula);
            estudiante.setUsuario(String.valueOf(matricula));
        }
        em.merge(estudiante);

        // carreraestudiante
        carreraEstudiante.getCarreraEstudianteId().setId_persona(estudiante.getId_persona());
        carreraEstudianteFacade.create(carreraEstudiante);

        // inscrito
        Date fecha = estudiante.getFechaInscripcion(); // fecha de inscripcion
        Integer maximoNumero = inscritoFacade.maximoNumero(gestionAcademica.getId_gestionacademica(), carreraEstudiante.getCarrera().getId_carrera());
        Long maximoCodigo = inscritoFacade.maximoCodigo(gestionAcademica.getId_gestionacademica(), carreraEstudiante.getCarrera().getId_carrera());
        Long codigo;
        Integer numero;
        if (maximoNumero == null && maximoCodigo == null) {
            codigo = (Long.parseLong(gestionAcademica.getGestion().toString() + gestionAcademica.getPeriodo().getPeriodoEntero().toString() + carreraEstudiante.getCarrera().getId_carrera().toString()) * 10000) + 1;
            numero = 1;
        } else {
            codigo = maximoCodigo + 1;
            numero = maximoNumero + 1;
        }
        Inscrito inscrito = new Inscrito(fecha, Tipo.NUEVO, codigo, numero, estudiante, carreraEstudiante.getCarrera(), gestionAcademica, campus);
        em.persist(inscrito);

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Long creditajeOferta(Inscrito inscrito) {
        Long l = 0L;

        List<Modulo> oferta = oferta(inscrito);
        for (Modulo modulo : oferta) {
            l += modulo.getCreditajeModulo();
        }

        return l;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Modulo> oferta(Inscrito inscrito) {
        List<Modulo> oferta = new ArrayList();

        List<Modulo> listaModuloAprobadas = moduloFacade.listaModuloAprobadas(inscrito.getEstudiante().getId_persona(), inscrito.getCarrera().getId_carrera());

        List<Modulo> listaModulos = moduloFacade.listaModulos(inscrito.getCarrera());
        listaModulos.removeAll(listaModuloAprobadas);

        for (Modulo modulo : listaModulos) {
            List<Modulo> prerequisitos = modulo.getPrerequisitos();
            if (listaModuloAprobadas.containsAll(prerequisitos)) {
                oferta.add(modulo);
            }
        }

        return oferta;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean tomarModulos(List<Nota> notas) {
        for (Nota nota : notas) {
            Grupo grupo = nota.getGrupo();
            long cantidadNotasGrupo = grupoFacade.cantidadNotasGrupo(grupo.getId_grupo());
            if (cantidadNotasGrupo + 1 < grupo.getCapacidad()) {
                em.persist(nota);
            } else if (cantidadNotasGrupo + 1 == grupo.getCapacidad()) {
                em.persist(nota);

                grupo.setAbierto(Boolean.FALSE);
                em.merge(grupo);
            } else {
                throw new EJBException("Grupo(s) lleno(s).");
            }
        }

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean retirarModulo(Nota nota) {
        Grupo grupo = nota.getGrupo();
        long cantidadNotasGrupo = grupoFacade.cantidadNotasGrupo(grupo.getId_grupo());

        if (cantidadNotasGrupo < grupo.getCapacidad()) {
            em.remove(em.merge(nota));
        } else if (cantidadNotasGrupo == grupo.getCapacidad()) {
            em.remove(em.merge(nota));

            grupo.setAbierto(Boolean.TRUE);
            em.merge(grupo);
        }

        return true;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Modulo> ofertaTomaModulos(Inscrito inscrito) {
        List<Modulo> ofertaTomaModulos = oferta(inscrito);

        List<Nota> estadoInscripcion = notaFacade.listaNotas(inscrito.getId_inscrito());
        for (Nota nota : estadoInscripcion) {
            ofertaTomaModulos.remove(nota.getModulo());
        }

        return ofertaTomaModulos;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Modulo> ofertaBoletinNotas(Inscrito inscrito) {
        List<Modulo> ofertaTomaModulos = oferta(inscrito);

        return ofertaTomaModulos;
    }
}

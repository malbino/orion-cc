/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.Cuota;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.Grupo;
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Log;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.entities.Nota;
import org.malbino.orion.entities.PlanPago;
import org.malbino.orion.entities.Usuario;
import org.malbino.orion.enums.Condicion;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.enums.Modalidad;
import org.malbino.orion.facades.GrupoFacade;
import org.malbino.orion.facades.ModuloFacade;
import org.malbino.orion.facades.PlanPagoFacade;
import org.malbino.orion.facades.negocio.InscripcionesFacade;
import org.malbino.orion.util.Encriptador;
import org.malbino.orion.util.Fecha;
import org.malbino.orion.util.Generador;
import org.malbino.orion.util.Propiedades;
import org.malbino.pfsense.webservices.CopiarUsuario;
import org.primefaces.event.FlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tincho
 */
@Named("EstudianteNuevoController")
@SessionScoped
public class EstudianteNuevoController extends AbstractController implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(InscripcionesFacade.class);

    @EJB
    InscripcionesFacade inscripcionesFacade;
    @EJB
    PlanPagoFacade planPagoFacade;
    @EJB
    ModuloFacade moduloFacade;
    @EJB
    GrupoFacade grupoFacade;
    @Inject
    LoginController loginController;

    private Inscrito inscrito;

    @PostConstruct
    public void init() {
        Estudiante estudiante = new Estudiante();
        inscrito = new Inscrito(estudiante);
    }

    public void reinit() {
        Estudiante estudiante = new Estudiante();
        inscrito = new Inscrito(estudiante);
    }

    @Override
    public List<Carrera> listaCarreras() {
        List<Carrera> l = new ArrayList<>();

        if (inscrito.getEstudiante() != null) {
            l = carreraFacade.listaCarreras();
        }

        return l;
    }

    public List<PlanPago> listaPlanesPago() {
        List<PlanPago> l = new ArrayList<>();

        if (inscrito.getCarrera() != null) {
            l = planPagoFacade.listaPlanesPago(inscrito.getCarrera().getId_carrera());
        }

        return l;
    }

    public void notasCuotasInscrito() {
        if (inscrito.getCarrera() != null) {
            inscrito.getNotas().clear();
            List<Modulo> modulos = moduloFacade.listaModulos(inscrito.getCarrera());
            for (Modulo modulo : modulos) {
                Nota nota = new Nota(0, Modalidad.REGULAR, Condicion.ABANDONO, inscrito.getGestionAcademica(), modulo, inscrito.getEstudiante(), inscrito, modulo.getGrupo());
                inscrito.getNotas().add(nota);
            }
        }

        if (inscrito.getPlanPago() != null) {
            inscrito.getCuotas().clear();
            for (int i = 1; i <= inscrito.getPlanPago().getNumeroCuotas(); i++) {
                Cuota cuota = new Cuota(i, "C" + i, "UNIDAD", "CUOTA " + i, inscrito.getPlanPago().getMontoCuota(), BigDecimal.ZERO, inscrito);
                inscrito.getCuotas().add(cuota);
            }
        }
    }

    public List<Grupo> listaGruposAbiertos(Modulo modulo) {
        List<Grupo> l = new ArrayList();

        log.info("gestionAcademica=" + inscrito.getGestionAcademica());
        log.info("carrera=" + inscrito.getCarrera());
        log.info("campus=" + inscrito.getCampus());

        if (inscrito.getGestionAcademica() != null && inscrito.getCarrera() != null && inscrito.getCampus() != null) {
            l = grupoFacade.listaGruposAbiertos(inscrito.getGestionAcademica().getId_gestionacademica(), inscrito.getCarrera().getId_carrera(), inscrito.getCampus().getId_campus(), modulo.getId_modulo());
        }
        return l;
    }

    public String onFlowProcess(FlowEvent event) {
        String newStep;

        if (event.getOldStep().compareTo("inscripcion") == 0) {
            notasCuotasInscrito();

            return event.getNewStep();
        }

        if (event.getOldStep().compareTo("modulos") == 0) {
            Boolean b = Boolean.TRUE;
            List<Nota> notas = inscrito.getNotas();
            for (Nota nota : notas) {
                if (nota.getGrupo() == null) {
                    b = Boolean.FALSE;
                    break;
                }
            }

            if (b) {
                return event.getNewStep();
            } else {
                this.mensajeDeError("Modulo(s) sin grupo.");

                return event.getOldStep();
            }
        }

        return event.getNewStep();
    }

    public Boolean revisarCuotas() {
        Boolean b = Boolean.TRUE;
        List<Cuota> cuotas = inscrito.getCuotas();
        for (Cuota cuota : cuotas) {
            if (cuota.getFechaVencimiento() == null) {
                b = Boolean.FALSE;
                break;
            }
        }
        return b;
    }

    public void copiarUsuario(Usuario usuario) {
        String[] properties = Propiedades.pfsenseProperties();

        String webservice = properties[0];
        String user = properties[1];
        String password = properties[2];

        if (!webservice.isEmpty() && !user.isEmpty() && !password.isEmpty()) {
            CopiarUsuario copiarUsuario = new CopiarUsuario(webservice, user, password, usuario);
            new Thread(copiarUsuario).start();

            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.READ, EntidadLog.USUARIO, usuario.getId_persona(), "Copia de usuario a pfSense", loginController.getUsr().toString()));
        }
    }

    public void registrarEstudiante() throws IOException {
        if (revisarCuotas()) {
            if (estudianteFacade.buscarPorDni(inscrito.getEstudiante().getDni()) == null) {
                String contrasena = Generador.generarContrasena();
                inscrito.getEstudiante().setContrasena(Encriptador.encriptar(contrasena));
                inscrito.getEstudiante().setContrasenaSinEncriptar(contrasena);

                if (inscripcionesFacade.registrarEstudianteNuevo(inscrito)) {
                    copiarUsuario(inscrito.getEstudiante());

                    //logF
                    logFacade.create(new Log(Fecha.getDate(), EventoLog.CREATE, EntidadLog.ESTUDIANTE, inscrito.getEstudiante().getId_persona(), "Inscripción estudiante nuevo", loginController.getUsr().toString()));

                    this.insertarParametro("inscrito", inscrito);

                    reinit();

                    this.toFichaInscripcion();
                } else {
                    this.mensajeDeError("No se pudo registrar al estudiante.");
                }
            } else {
                this.mensajeDeError("Estudiante repetido.");
            }
        } else {
            this.mensajeDeError("Cuota(s) sin fecha de vencimiento.");
        }
    }

    public void cancelar() throws IOException {
        reinit();

        toEstudianteNuevo();
    }

    public void toEstudianteNuevo() throws IOException {
        this.redireccionarViewId("/inscripciones/estudianteNuevo/estudianteNuevo");
    }

    public void toFichaInscripcion() throws IOException {
        this.redireccionarViewId("/inscripciones/estudianteNuevo/fichaInscripcion");
    }

    /**
     * @return the inscrito
     */
    public Inscrito getInscrito() {
        return inscrito;
    }

    /**
     * @param inscrito the inscrito to set
     */
    public void setInscrito(Inscrito inscrito) {
        this.inscrito = inscrito;
    }
}

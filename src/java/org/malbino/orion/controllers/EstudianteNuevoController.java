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
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Log;
import org.malbino.orion.entities.PlanPago;
import org.malbino.orion.entities.Usuario;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.facades.PlanPagoFacade;
import org.malbino.orion.facades.negocio.InscripcionesFacade;
import org.malbino.orion.util.Encriptador;
import org.malbino.orion.util.Fecha;
import org.malbino.orion.util.Generador;
import org.malbino.orion.util.Propiedades;
import org.malbino.pfsense.webservices.CopiarUsuario;
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

    public void planPagoInscrito() {
        inscrito.setPlanPago(null);
        inscrito.getCuotas().clear();
    }

    public void cuotasInscrito() {
        inscrito.getCuotas().clear();

        if (inscrito.getPlanPago() != null) {
            for (int i = 1; i <= inscrito.getPlanPago().getNumeroCuotas(); i++) {
                Cuota cuota = new Cuota(i, "C" + i, "UNIDAD", "CUOTA " + i, inscrito.getPlanPago().getMontoCuota(), BigDecimal.ZERO, inscrito);
                inscrito.getCuotas().add(cuota);
            }
        } else {
            inscrito.getCuotas().clear();
        }
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

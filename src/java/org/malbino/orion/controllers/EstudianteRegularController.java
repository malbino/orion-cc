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
import org.malbino.orion.entities.CarreraEstudiante;
import org.malbino.orion.entities.Cuota;
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
import org.malbino.orion.facades.CarreraEstudianteFacade;
import org.malbino.orion.facades.GrupoFacade;
import org.malbino.orion.facades.InscritoFacade;
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
@Named("EstudianteRegularController")
@SessionScoped
public class EstudianteRegularController extends AbstractController implements Serializable {
    
    private static final Logger log = LoggerFactory.getLogger(EstudianteRegularController.class);
    
    @EJB
    InscritoFacade inscritoFacade;
    @EJB
    InscripcionesFacade inscripcionesFacade;
    @EJB
    CarreraEstudianteFacade carreraEstudianteFacade;
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
        inscrito = new Inscrito();
    }
    
    public void reinit() {
        inscrito = new Inscrito();
    }
    
    @Override
    public List<Carrera> listaCarreras() {
        List<Carrera> l = new ArrayList<>();
        
        if (inscrito.getEstudiante() != null) {
            List<CarreraEstudiante> carrerasEstudiante = carreraEstudianteFacade.listaCarrerasEstudiante(inscrito.getEstudiante().getId_persona());
            for (CarreraEstudiante carreraEstudiante : carrerasEstudiante) {
                l.add(carreraEstudiante.getCarrera());
            }
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
            
            List<Modulo> oferta = inscripcionesFacade.oferta(inscrito.getEstudiante(), inscrito.getCarrera());
            for (Modulo modulo : oferta) {
                Nota nota = new Nota(0, Modalidad.REGULAR, Condicion.ABANDONO, inscrito.getGestionAcademica(), modulo, inscrito.getEstudiante(), inscrito, modulo.getGrupo());
                inscrito.getNotas().add(nota);
            }
        }

        // cuotas
        BigDecimal precioHora = inscrito.getCarrera().precioHora();
        log.info("precioHora=" + precioHora);
        BigDecimal montoCuota = BigDecimal.ZERO;
        List<Nota> notas = inscrito.getNotas();
        for (Nota nota : notas) {
            BigDecimal precioModulo = BigDecimal.valueOf(nota.getModulo().getHoras()).multiply(precioHora);
            log.info("precioModulo=" + precioModulo);
            
            montoCuota = montoCuota.add(precioModulo);
        }
        log.info("montoCuota=" + montoCuota);
        
        inscrito.getCuotas().clear();
        Cuota cuota = new Cuota(1, "C1", "UNIDAD", "CUOTA 1", montoCuota, BigDecimal.ZERO, inscrito);
        inscrito.getCuotas().add(cuota);
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
            if (inscritoFacade.buscarInscrito(inscrito.getEstudiante().getId_persona(), inscrito.getCarrera().getId_carrera(), inscrito.getGestionAcademica().getId_gestionacademica()) == null) {
                if (inscrito.getEstudiante().getDiplomaBachiller()) {
                    String contrasena = Generador.generarContrasena();
                    inscrito.getEstudiante().setContrasena(Encriptador.encriptar(contrasena));
                    inscrito.getEstudiante().setContrasenaSinEncriptar(contrasena);
                    
                    if (inscripcionesFacade.registrarEstudianteRegular(inscrito)) {
                        copiarUsuario(inscrito.getEstudiante());

                        //log
                        logFacade.create(new Log(Fecha.getDate(), EventoLog.UPDATE, EntidadLog.ESTUDIANTE, inscrito.getEstudiante().getId_persona(), "Inscripción estudiante regular", loginController.getUsr().toString()));
                        
                        this.insertarParametro("inscrito", inscrito);
                        
                        reinit();
                        
                        this.toFichaInscripcion();
                    } else {
                        this.mensajeDeError("No se pudo registrar al estudiante.");
                    }
                } else {
                    this.mensajeDeError("Estudiante sin titulo de bachiller.");
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
        
        toEstudianteRegular();
    }
    
    public void toEstudianteRegular() throws IOException {
        this.redireccionarViewId("/inscripciones/estudianteRegular/estudianteRegular");
    }
    
    public void toFichaInscripcion() throws IOException {
        this.redireccionarViewId("/inscripciones/estudianteRegular/fichaInscripcion");
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

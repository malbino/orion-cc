/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.malbino.moodle.webservices.CopiarInscrito;
import org.malbino.orion.entities.Estudiante;
import org.malbino.orion.entities.Grupo;
import org.malbino.orion.entities.Inscrito;
import org.malbino.orion.entities.Log;
import org.malbino.orion.entities.Modulo;
import org.malbino.orion.entities.Nota;
import org.malbino.orion.enums.Condicion;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.enums.Funcionalidad;
import org.malbino.orion.enums.Modalidad;
import org.malbino.orion.facades.ActividadFacade;
import org.malbino.orion.facades.DetalleFacade;
import org.malbino.orion.facades.GrupoFacade;
import org.malbino.orion.facades.InscritoFacade;
import org.malbino.orion.facades.ModuloFacade;
import org.malbino.orion.facades.NotaFacade;
import org.malbino.orion.facades.negocio.InscripcionesFacade;
import org.malbino.orion.util.Constantes;
import org.malbino.orion.util.Fecha;
import org.malbino.orion.util.Propiedades;

/**
 *
 * @author Tincho
 */
@Named("InscripcionManualController")
@SessionScoped
public class InscripcionManualController extends AbstractController implements Serializable {

    @Inject
    LoginController loginController;
    @EJB
    InscritoFacade inscritoFacade;
    @EJB
    InscripcionesFacade inscripcionesFacade;
    @EJB
    GrupoFacade grupoFacade;
    @EJB
    NotaFacade notaFacade;
    @EJB
    ActividadFacade actividadFacade;
    @EJB
    ModuloFacade moduloFacade;
    @EJB
    DetalleFacade detalleFacade;

    private Estudiante seleccionEstudiante;
    private Inscrito seleccionInscrito;

    private List<Modulo> ofertaModulos;
    private List<Modulo> modulos;
    private List<Nota> estadoInscripcion;
    private Nota seleccionNota;

    private String[] grupos = Arrays.copyOfRange(Constantes.ABECEDARIO, 0, 6);
    private String grupo;

    @PostConstruct
    public void init() {
        seleccionInscrito = null;
        ofertaModulos = new ArrayList();
        modulos = new ArrayList();
        estadoInscripcion = new ArrayList();

        grupo = grupos[0];
    }

    public void reinit() {
        seleccionInscrito = null;
        ofertaModulos = new ArrayList();
        modulos = new ArrayList();
        estadoInscripcion = new ArrayList();

        grupo = grupos[0];
    }

    public List<Inscrito> listaInscritos() {
        List<Inscrito> l = new ArrayList();
        if (seleccionEstudiante != null) {
            l = inscritoFacade.listaInscritosPersona(seleccionEstudiante.getId_persona());
        }
        return l;
    }

    public List<Grupo> listaGruposAbiertos(Modulo modulo) {
        List<Grupo> l = new ArrayList();
        if (seleccionInscrito != null && modulo != null) {
            l = grupoFacade.listaGruposAbiertos(seleccionInscrito.getGestionAcademica().getId_gestionacademica(), seleccionInscrito.getCarrera().getId_carrera(), seleccionInscrito.getCampus().getId_campus(), modulo.getId_modulo());
        }
        return l;
    }

    public void actualizarOferta() {
        if (seleccionInscrito != null) {
            ofertaModulos = inscripcionesFacade.ofertaTomaModulos(seleccionInscrito);

            for (Modulo modulo : ofertaModulos) {
                List<Grupo> listaGruposAbiertos = grupoFacade.listaGruposAbiertos(seleccionInscrito.getGestionAcademica().getId_gestionacademica(), seleccionInscrito.getCampus().getId_campus(), modulo.getId_modulo(), grupo);
                Iterator<Grupo> iterator = listaGruposAbiertos.iterator();
                if (iterator.hasNext()) {
                    modulo.setGrupo(iterator.next());
                } else {
                    modulo.setGrupo(null);
                }
            }
        }
    }

    public void actualizarModulos() {
        if (seleccionInscrito != null) {
            modulos = moduloFacade.listaModulos(seleccionInscrito.getCarrera());
        }
    }

    public void actualizarEstadoInscripcion() {
        if (seleccionInscrito != null) {
            estadoInscripcion = notaFacade.listaNotas(seleccionInscrito.getId_inscrito());
        }
    }

    public boolean verificarGrupos() {
        boolean b = true;
        for (Modulo m : ofertaModulos) {
            if (m.getGrupo() == null) {
                b = false;
                break;
            }
        }
        return b;
    }

    public boolean moduloRepetida(Modulo modulo) {
        boolean b = false;
        for (Nota n : estadoInscripcion) {
            if (n.getModulo().equals(modulo)) {
                b = true;
                break;
            }
        }
        return b;
    }

    public void copiarInscrito(Estudiante estudiante, List<Nota> notas) {
        String[] properties = Propiedades.moodleProperties();

        String webservice = properties[0];
        String login = properties[1];
        String username = properties[2];
        String password = properties[3];
        String serviceName = properties[4];

        if (!webservice.isEmpty() && !login.isEmpty() && !username.isEmpty() && !password.isEmpty() && !serviceName.isEmpty()) {
            CopiarInscrito copiarInscrito = new CopiarInscrito(login, webservice, username, password, serviceName, estudiante, notas);
            new Thread(copiarInscrito).start();

            //log
            logFacade.create(new Log(Fecha.getDate(), EventoLog.READ, EntidadLog.ESTUDIANTE, estudiante.getId_persona(), "Copia de inscrito a Moodle", loginController.getUsr().toString()));
        }
    }

    public void tomarModulos() throws IOException {
        if (!actividadFacade.listaActividades(Fecha.getDate(), Funcionalidad.INSCRIPCION, seleccionInscrito.getGestionAcademica().getId_gestionacademica()).isEmpty()) {
            if (!ofertaModulos.isEmpty()) {
                if (verificarGrupos()) {
                    if (detalleFacade.modulosPagados(seleccionInscrito, ofertaModulos)) {
                        List<Nota> aux = new ArrayList();
                        for (Modulo modulo : ofertaModulos) {
                            Nota nota = new Nota(0, Modalidad.REGULAR, Condicion.ABANDONO, seleccionInscrito.getGestionAcademica(), modulo, seleccionInscrito.getEstudiante(), seleccionInscrito, modulo.getGrupo());
                            aux.add(nota);
                        }

                        try {
                            if (inscripcionesFacade.tomarModulos(aux)) {
                                copiarInscrito(seleccionInscrito.getEstudiante(), aux);

                                //log
                                for (Nota nota : aux) {
                                    //log
                                    logFacade.create(new Log(Fecha.getDate(), EventoLog.CREATE, EntidadLog.NOTA, nota.getId_nota(), "Creación de nota por inscripción manual", loginController.getUsr().toString()));
                                }

                                toEstadoInscripcion();
                            }
                        } catch (EJBException e) {
                            this.mensajeDeError(e.getMessage());
                        }
                    } else {
                        this.mensajeDeError("Existen modulos pendientes de pago.");
                    }
                } else {
                    this.mensajeDeError("Existen modulos sin grupos.");
                }
            } else {
                this.mensajeDeError("No existen modulos.");
            }
        } else {
            this.mensajeDeError("Fuera de fecha.");
        }
    }

    public void inscripcionManual() throws IOException {
        if (!actividadFacade.listaActividades(Fecha.getDate(), Funcionalidad.INSCRIPCION, seleccionInscrito.getGestionAcademica().getId_gestionacademica()).isEmpty()) {

            if (!modulos.isEmpty()) {
                List<Nota> aux = new ArrayList();
                for (Modulo modulo : modulos) {
                    if (modulo.getGrupo() != null && !moduloRepetida(modulo)) {
                        Nota nota = new Nota(0, Modalidad.REGULAR, Condicion.ABANDONO, seleccionInscrito.getGestionAcademica(), modulo, seleccionInscrito.getEstudiante(), seleccionInscrito, modulo.getGrupo());
                        aux.add(nota);
                    }
                }

                try {
                    if (inscripcionesFacade.tomarModulos(aux)) {
                        copiarInscrito(seleccionInscrito.getEstudiante(), aux);

                        toEstadoInscripcion();
                    }
                } catch (EJBException e) {
                    this.mensajeDeError(e.getMessage());
                }
            } else {
                this.mensajeDeError("No existen modulos.");
            }

        } else {
            this.mensajeDeError("Fuera de fecha.");
        }
    }

    public void retirarModulo() throws IOException {
        if (inscripcionesFacade.retirarModulo(seleccionNota)) {
            toEstadoInscripcion();
        }
    }

    public void toEstadoInscripcion() throws IOException {
        this.actualizarEstadoInscripcion();

        this.redireccionarViewId("/inscripciones/inscripcionManual/estadoInscripcion");
    }

    public void toOfertaModulos() throws IOException {
        actualizarOferta();

        this.redireccionarViewId("/inscripciones/inscripcionManual/ofertaModulos");
    }

    public void toModulos() throws IOException {
        actualizarModulos();

        this.redireccionarViewId("/inscripciones/inscripcionManual/modulos");
    }

    /**
     * @return the seleccionInscrito
     */
    public Inscrito getSeleccionInscrito() {
        return seleccionInscrito;
    }

    /**
     * @param seleccionInscrito the seleccionInscrito to set
     */
    public void setSeleccionInscrito(Inscrito seleccionInscrito) {
        this.seleccionInscrito = seleccionInscrito;
    }

    /**
     * @return the ofertaModulos
     */
    public List<Modulo> getOfertaModulos() {
        return ofertaModulos;
    }

    /**
     * @param ofertaModulos the ofertaModulos to set
     */
    public void setOfertaModulos(List<Modulo> ofertaModulos) {
        this.ofertaModulos = ofertaModulos;
    }

    /**
     * @return the estadoInscripcion
     */
    public List<Nota> getEstadoInscripcion() {
        return estadoInscripcion;
    }

    /**
     * @param estadoInscripcion the estadoInscripcion to set
     */
    public void setEstadoInscripcion(List<Nota> estadoInscripcion) {
        this.estadoInscripcion = estadoInscripcion;
    }

    /**
     * @return the seleccionEstudiante
     */
    public Estudiante getSeleccionEstudiante() {
        return seleccionEstudiante;
    }

    /**
     * @param seleccionEstudiante the seleccionEstudiante to set
     */
    public void setSeleccionEstudiante(Estudiante seleccionEstudiante) {
        this.seleccionEstudiante = seleccionEstudiante;
    }

    /**
     * @return the seleccionNota
     */
    public Nota getSeleccionNota() {
        return seleccionNota;
    }

    /**
     * @param seleccionNota the seleccionNota to set
     */
    public void setSeleccionNota(Nota seleccionNota) {
        this.seleccionNota = seleccionNota;
    }

    /**
     * @return the modulos
     */
    public List<Modulo> getModulos() {
        return modulos;
    }

    /**
     * @param modulos the modulos to set
     */
    public void setModulos(List<Modulo> modulos) {
        this.modulos = modulos;
    }

    /**
     * @return the grupos
     */
    public String[] getGrupos() {
        return grupos;
    }

    /**
     * @param grupos the grupos to set
     */
    public void setGrupos(String[] grupos) {
        this.grupos = grupos;
    }

    /**
     * @return the grupo
     */
    public String getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}

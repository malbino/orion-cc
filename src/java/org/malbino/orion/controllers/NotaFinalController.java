/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.malbino.orion.entities.Campus;
import org.malbino.orion.entities.Carrera;
import org.malbino.orion.entities.GestionAcademica;
import org.malbino.orion.entities.Grupo;
import org.malbino.orion.entities.Log;
import org.malbino.orion.entities.Nota;
import org.malbino.orion.enums.EntidadLog;
import org.malbino.orion.enums.EventoLog;
import org.malbino.orion.enums.ModalidadEvaluacion;
import org.malbino.orion.facades.GrupoFacade;
import org.malbino.orion.facades.NotaFacade;
import org.malbino.orion.facades.negocio.RegistroDocenteFacade;
import org.malbino.orion.util.Fecha;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author Tincho
 */
@Named("NotaFinalController")
@SessionScoped
public class NotaFinalController extends AbstractController implements Serializable {

    @Inject
    LoginController loginController;
    @EJB
    GrupoFacade grupoFacade;
    @EJB
    NotaFacade notaFacade;
    @EJB
    RegistroDocenteFacade registroDocenteFacade;

    private GestionAcademica seleccionGestionAcademica;
    private Carrera seleccionCarrera;
    private Campus seleccionCampus;
    private Grupo seleccionGrupo;
    private List<Nota> notas;

    private StreamedContent download;
    private UploadedFile upload;

    @PostConstruct
    public void init() {
        seleccionGestionAcademica = null;
        seleccionCarrera = null;
        seleccionCampus = null;
        seleccionGrupo = null;
        notas = new ArrayList();
    }

    public void reinit() {
        seleccionGestionAcademica = null;
        seleccionCarrera = null;
        seleccionCampus = null;
        seleccionGrupo = null;
        notas = new ArrayList();
    }

    @Override
    public List<GestionAcademica> listaGestionesAcademicas() {
        return gestionAcademicaFacade.listaGestionAcademica(ModalidadEvaluacion.MODULAR, true);
    }

    public List<Grupo> listaGrupos() {
        List<Grupo> l = new ArrayList();
        if (seleccionGestionAcademica != null && seleccionCarrera != null && seleccionCampus != null && loginController.getUsr() != null) {
            l = grupoFacade.listaGruposEmpleado(seleccionGestionAcademica.getId_gestionacademica(), seleccionCarrera.getId_carrera(), seleccionCampus.getId_campus(), loginController.getUsr().getId_persona());
        }
        return l;
    }

    public void actualizarNotas() {
        if (seleccionGrupo != null) {
            notas = notaFacade.listaNotasGrupo(seleccionGrupo.getId_grupo());
        }
    }

    public void guardar() {
        if (registroDocenteFacade.editarNotas(notas)) {
            actualizarNotas();

            //log
            for (Nota nota : notas) {
                logFacade.create(new Log(Fecha.getDate(), EventoLog.UPDATE, EntidadLog.NOTA, nota.getId_nota(), "Actualización nota final", loginController.getUsr().toString()));
            }

            this.mensajeDeInformacion("Guardado.");
        }
    }

    public void generarPDF() throws IOException {
        this.insertarParametro("id_grupo", seleccionGrupo.getId_grupo());

        //log
        logFacade.create(new Log(Fecha.getDate(), EventoLog.READ, EntidadLog.GRUPO, seleccionGrupo.getId_grupo(), "Descarga Planilla de Seguimiento", loginController.getUsr().toString()));
        
        this.toPlanillaSeguimiento();
    }

    public void toNotaFinal() throws IOException {
        this.redireccionarViewId("/registroDocente/notaFinal");
    }

    public void toPlanillaSeguimiento() throws IOException {
        this.redireccionarViewId("/registroDocente/planillaSeguimiento");
    }

    /**
     * @return the seleccionGestionAcademica
     */
    public GestionAcademica getSeleccionGestionAcademica() {
        return seleccionGestionAcademica;
    }

    /**
     * @param seleccionGestionAcademica the seleccionGestionAcademica to set
     */
    public void setSeleccionGestionAcademica(GestionAcademica seleccionGestionAcademica) {
        this.seleccionGestionAcademica = seleccionGestionAcademica;
    }

    /**
     * @return the seleccionCarrera
     */
    public Carrera getSeleccionCarrera() {
        return seleccionCarrera;
    }

    /**
     * @param seleccionCarrera the seleccionCarrera to set
     */
    public void setSeleccionCarrera(Carrera seleccionCarrera) {
        this.seleccionCarrera = seleccionCarrera;
    }

    /**
     * @return the seleccionGrupo
     */
    public Grupo getSeleccionGrupo() {
        return seleccionGrupo;
    }

    /**
     * @param seleccionGrupo the seleccionGrupo to set
     */
    public void setSeleccionGrupo(Grupo seleccionGrupo) {
        this.seleccionGrupo = seleccionGrupo;
    }

    /**
     * @return the notas
     */
    public List<Nota> getNotas() {
        return notas;
    }

    /**
     * @param notas the notas to set
     */
    public void setNotas(List<Nota> notas) {
        this.notas = notas;
    }

    /**
     * @return the download
     */
    public StreamedContent getDownload() {
        return download;
    }

    /**
     * @param download the download to set
     */
    public void setDownload(StreamedContent download) {
        this.download = download;
    }

    /**
     * @return the upload
     */
    public UploadedFile getUpload() {
        return upload;
    }

    /**
     * @param upload the upload to set
     */
    public void setUpload(UploadedFile upload) {
        this.upload = upload;
    }

    /**
     * @return the seleccionCampus
     */
    public Campus getSeleccionCampus() {
        return seleccionCampus;
    }

    /**
     * @param seleccionCampus the seleccionCampus to set
     */
    public void setSeleccionCampus(Campus seleccionCampus) {
        this.seleccionCampus = seleccionCampus;
    }
}

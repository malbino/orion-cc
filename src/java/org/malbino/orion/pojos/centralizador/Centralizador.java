/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.pojos.centralizador;

/**
 *
 * @author Tincho
 */
public class Centralizador {
    
    private String instituto;
    private String gestion;
    private String carrera;
    private String campus;
    private String turno;
    private String paralelo;

    private String[] modulosCentralizador;
    private EstudianteCentralizador[] estudiantesCentralizador;

    public Centralizador() {
    } 

    /**
     * @return the instituto
     */
    public String getInstituto() {
        return instituto;
    }

    /**
     * @param instituto the instituto to set
     */
    public void setInstituto(String instituto) {
        this.instituto = instituto;
    }

    /**
     * @return the gestion
     */
    public String getGestion() {
        return gestion;
    }

    /**
     * @param gestion the gestion to set
     */
    public void setGestion(String gestion) {
        this.gestion = gestion;
    }

    /**
     * @return the carrera
     */
    public String getCarrera() {
        return carrera;
    }

    /**
     * @param carrera the carrera to set
     */
    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    /**
     * @return the campus
     */
    public String getCampus() {
        return campus;
    }

    /**
     * @param campus the campus to set
     */
    public void setCampus(String campus) {
        this.campus = campus;
    }

    /**
     * @return the turno
     */
    public String getTurno() {
        return turno;
    }

    /**
     * @param turno the turno to set
     */
    public void setTurno(String turno) {
        this.turno = turno;
    }

    /**
     * @return the paralelo
     */
    public String getParalelo() {
        return paralelo;
    }

    /**
     * @param paralelo the paralelo to set
     */
    public void setParalelo(String paralelo) {
        this.paralelo = paralelo;
    }

    /**
     * @return the modulosCentralizador
     */
    public String[] getModulosCentralizador() {
        return modulosCentralizador;
    }

    /**
     * @param modulosCentralizador the modulosCentralizador to set
     */
    public void setModulosCentralizador(String[] modulosCentralizador) {
        this.modulosCentralizador = modulosCentralizador;
    }

    /**
     * @return the estudiantesCentralizador
     */
    public EstudianteCentralizador[] getEstudiantesCentralizador() {
        return estudiantesCentralizador;
    }

    /**
     * @param estudiantesCentralizador the estudiantesCentralizador to set
     */
    public void setEstudiantesCentralizador(EstudianteCentralizador[] estudiantesCentralizador) {
        this.estudiantesCentralizador = estudiantesCentralizador;
    }

}

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
public class EstudianteCentralizador {

    private Integer numero;
    private String estudiante;
    private NotaCentralizador[] notasCentralizador;
    private String observaciones;

    public EstudianteCentralizador() {
    }

    /**
     * @return the numero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    /**
     * @return the estudiante
     */
    public String getEstudiante() {
        return estudiante;
    }

    /**
     * @param estudiante the estudiante to set
     */
    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    /**
     * @return the notasCentralizador
     */
    public NotaCentralizador[] getNotasCentralizador() {
        return notasCentralizador;
    }

    /**
     * @param notasCentralizador the notasCentralizador to set
     */
    public void setNotasCentralizador(NotaCentralizador[] notasCentralizador) {
        this.notasCentralizador = notasCentralizador;
    }

    /**
     * @return the observaciones
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * @param observaciones the observaciones to set
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    
}

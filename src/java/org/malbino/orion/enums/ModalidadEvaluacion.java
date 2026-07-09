/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.enums;

/**
 *
 * @author Martin
 */
public enum ModalidadEvaluacion {
    MODULAR("MODULAR", 3, 80, 60);

    private String nombre;
    private Integer cantidadMaximaReprobaciones;
    private Integer notaMinimaAprobacion;
    private Integer notaMinimmaPruebaRecuperacion;

    private ModalidadEvaluacion(String nombre, Integer cantidadMaximaReprobaciones, Integer notaMinimaAprobacion, Integer notaMinimmaPruebaRecuperacion) {
        this.nombre = nombre;
        this.cantidadMaximaReprobaciones = cantidadMaximaReprobaciones;
        this.notaMinimaAprobacion = notaMinimaAprobacion;
        this.notaMinimmaPruebaRecuperacion = notaMinimmaPruebaRecuperacion;
    }

    @Override
    public String toString() {
        return nombre;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the cantidadMaximaReprobaciones
     */
    public Integer getCantidadMaximaReprobaciones() {
        return cantidadMaximaReprobaciones;
    }

    /**
     * @param cantidadMaximaReprobaciones the cantidadMaximaReprobaciones to set
     */
    public void setCantidadMaximaReprobaciones(Integer cantidadMaximaReprobaciones) {
        this.cantidadMaximaReprobaciones = cantidadMaximaReprobaciones;
    }

    /**
     * @return the notaMinimaAprobacion
     */
    public Integer getNotaMinimaAprobacion() {
        return notaMinimaAprobacion;
    }

    /**
     * @param notaMinimaAprobacion the notaMinimaAprobacion to set
     */
    public void setNotaMinimaAprobacion(Integer notaMinimaAprobacion) {
        this.notaMinimaAprobacion = notaMinimaAprobacion;
    }

    /**
     * @return the notaMinimmaPruebaRecuperacion
     */
    public Integer getNotaMinimmaPruebaRecuperacion() {
        return notaMinimmaPruebaRecuperacion;
    }

    /**
     * @param notaMinimmaPruebaRecuperacion the notaMinimmaPruebaRecuperacion to
     * set
     */
    public void setNotaMinimmaPruebaRecuperacion(Integer notaMinimmaPruebaRecuperacion) {
        this.notaMinimmaPruebaRecuperacion = notaMinimmaPruebaRecuperacion;
    }
}

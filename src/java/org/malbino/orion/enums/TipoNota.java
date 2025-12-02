/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.enums;

import java.util.Arrays;

/**
 *
 * @author Martin
 */
public enum TipoNota {
    PRIMER_PARCIAL_MODULAR_2P("PRIMER PARCIAL", ModalidadEvaluacion.MODULAR_2P),
    SEGUNDO_PARCIAL_MODULAR_2P("SEGUNDO PARCIAL", ModalidadEvaluacion.MODULAR_2P),
    RECUPERATORIO_MODULAR_2P("RECUPERATORIO", ModalidadEvaluacion.MODULAR_2P);

    private String nombre;
    private ModalidadEvaluacion modalidadEvaluacion;

    private TipoNota(String nombre, ModalidadEvaluacion modalidadEvaluacion) {
        this.nombre = nombre;
        this.modalidadEvaluacion = modalidadEvaluacion;
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
     * @return the modalidadEvaluacion
     */
    public ModalidadEvaluacion getModalidadEvaluacion() {
        return modalidadEvaluacion;
    }

    /**
     * @param modalidadEvaluacion the modalidadEvaluacion to set
     */
    public void setModalidadEvaluacion(ModalidadEvaluacion modalidadEvaluacion) {
        this.modalidadEvaluacion = modalidadEvaluacion;
    }

    public static TipoNota[] values(ModalidadEvaluacion modalidadEvaluacion) {
        return Arrays.stream(TipoNota.values()).filter(tipoNota -> tipoNota.getModalidadEvaluacion() != null && tipoNota.getModalidadEvaluacion().equals(modalidadEvaluacion)).toArray(TipoNota[]::new);
    }
}

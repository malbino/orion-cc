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
public enum EntidadLog {

    ACTIVIDAD("Actividad"),
    AULA("Aula"),
    CAMPUS("Campus"),
    CARRERA("Carrera"),
    CARRERA_ESTUDIANTE("Carrera Estudiante"),
    CLASE("Clase"),
    COMPROBANTE("Comprobante"),
    CONCEPTO_PAGO("Concepto de Pago"),
    DESCUENTO("Descuento"),
    DETALLE("Detalle"),
    EMPLEADO("Empleado"),
    ESTUDIANTE("Estudiante"),
    GESTION_ACADEMICA("Gestión Académica"),
    GRUPO("Grupo"),
    INSCRITO("Inscrito"),
    INSTITUTO("Instituto"),
    MODULO("Modulo"),
    NOTA("Nota"),
    PERIODO("Periodo"),
    RECURSO("Recurso"),
    ROL("Rol"),
    USUARIO("Usuario");

    private String nombre;

    private EntidadLog(String nombre) {
        this.nombre = nombre;
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

    @Override
    public String toString() {
        return nombre;
    }
}

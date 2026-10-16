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
public class NotaCentralizador {

    private Integer notaFinal;
    private Integer recuperatorio;

    public NotaCentralizador() {
    }

    public String notaFinal() {
        String s = "";
        if (notaFinal != null) {
            s = notaFinal.toString();
        }
        return s;
    }
    
    public String recuperatorio() {
        String s = "";
        if (recuperatorio != null) {
            s = recuperatorio.toString();
        }
        return s;
    }

    /**
     * @return the notaFinal
     */
    public Integer getNotaFinal() {
        return notaFinal;
    }

    /**
     * @param notaFinal the notaFinal to set
     */
    public void setNotaFinal(Integer notaFinal) {
        this.notaFinal = notaFinal;
    }

    /**
     * @return the recuperatorio
     */
    public Integer getRecuperatorio() {
        return recuperatorio;
    }

    /**
     * @param recuperatorio the recuperatorio to set
     */
    public void setRecuperatorio(Integer recuperatorio) {
        this.recuperatorio = recuperatorio;
    }

}

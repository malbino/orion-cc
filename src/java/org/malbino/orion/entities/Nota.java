/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.malbino.orion.enums.Condicion;
import org.malbino.orion.enums.Modalidad;

/**
 *
 * @author malbino
 */
@Entity
@Table(name = "nota", uniqueConstraints = @UniqueConstraint(columnNames = {"id_gestionacademica", "id_modulo", "id_estudiante"}))
public class Nota implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_nota;

    private Integer notaFinal;
    private Integer recuperatorio;
    private Modalidad modalidad;
    private Condicion condicion;

    private Integer numeroLibro;
    private Integer numeroFolio;

    @JoinColumn(name = "id_gestionacademica")
    @ManyToOne
    private GestionAcademica gestionAcademica;

    @JoinColumn(name = "id_modulo")
    @ManyToOne
    private Modulo modulo;

    @JoinColumn(name = "id_estudiante")
    @ManyToOne
    private Estudiante estudiante;

    @JoinColumn(name = "id_inscrito")
    @ManyToOne
    private Inscrito inscrito;

    @JoinColumn(name = "id_grupo")
    @ManyToOne
    private Grupo grupo;

    public Nota() {
    }

    public Nota(Integer notaFinal, Modalidad modalidad, Condicion condicion, GestionAcademica gestionAcademica, Modulo modulo, Estudiante estudiante, Inscrito inscrito, Grupo grupo) {
        this.notaFinal = notaFinal;
        this.modalidad = modalidad;
        this.condicion = condicion;
        this.gestionAcademica = gestionAcademica;
        this.modulo = modulo;
        this.estudiante = estudiante;
        this.inscrito = inscrito;
        this.grupo = grupo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id_nota);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Nota other = (Nota) obj;
        return Objects.equals(this.id_nota, other.id_nota);
    }

    /**
     * @return the id_nota
     */
    public Integer getId_nota() {
        return id_nota;
    }

    /**
     * @param id_nota the id_nota to set
     */
    public void setId_nota(Integer id_nota) {
        this.id_nota = id_nota;
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

    /**
     * @return the modalidad
     */
    public Modalidad getModalidad() {
        return modalidad;
    }

    /**
     * @param modalidad the modalidad to set
     */
    public void setModalidad(Modalidad modalidad) {
        this.modalidad = modalidad;
    }

    /**
     * @return the condicion
     */
    public Condicion getCondicion() {
        return condicion;
    }

    /**
     * @param condicion the condicion to set
     */
    public void setCondicion(Condicion condicion) {
        this.condicion = condicion;
    }

    /**
     * @return the numeroLibro
     */
    public Integer getNumeroLibro() {
        return numeroLibro;
    }

    /**
     * @param numeroLibro the numeroLibro to set
     */
    public void setNumeroLibro(Integer numeroLibro) {
        this.numeroLibro = numeroLibro;
    }

    /**
     * @return the numeroFolio
     */
    public Integer getNumeroFolio() {
        return numeroFolio;
    }

    /**
     * @param numeroFolio the numeroFolio to set
     */
    public void setNumeroFolio(Integer numeroFolio) {
        this.numeroFolio = numeroFolio;
    }

    /**
     * @return the gestionAcademica
     */
    public GestionAcademica getGestionAcademica() {
        return gestionAcademica;
    }

    /**
     * @param gestionAcademica the gestionAcademica to set
     */
    public void setGestionAcademica(GestionAcademica gestionAcademica) {
        this.gestionAcademica = gestionAcademica;
    }

    /**
     * @return the modulo
     */
    public Modulo getModulo() {
        return modulo;
    }

    /**
     * @param modulo the modulo to set
     */
    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    /**
     * @return the estudiante
     */
    public Estudiante getEstudiante() {
        return estudiante;
    }

    /**
     * @param estudiante the estudiante to set
     */
    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
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

    /**
     * @return the grupo
     */
    public Grupo getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

}

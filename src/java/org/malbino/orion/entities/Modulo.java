/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author malbino
 */
@Entity
@Table(name = "modulo", uniqueConstraints = @UniqueConstraint(columnNames = {"codigo", "id_carrera"}))
public class Modulo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_modulo;

    private Integer numero;
    private String codigo;
    private String nombre;
    private Integer horas;
    private Integer creditajeModulo;

    @JoinTable(name = "prerequisito", joinColumns = {
        @JoinColumn(name = "id_modulo", referencedColumnName = "id_modulo")}, inverseJoinColumns = {
        @JoinColumn(name = "id_prerequisito", referencedColumnName = "id_modulo")})
    @ManyToMany
    private List<Modulo> prerequisitos;

    @JoinColumn(name = "id_carrera")
    @ManyToOne
    private Carrera carrera;

    @OneToMany(mappedBy = "modulo", orphanRemoval = true)
    private List<Grupo> grupos;

    @OneToMany(mappedBy = "modulo", orphanRemoval = true)
    private List<Nota> notas;

    @Transient
    private Grupo grupo;

    public Modulo() {
    }

    /**
     * @return the id_modulo
     */
    public Integer getId_modulo() {
        return id_modulo;
    }

    /**
     * @param id_modulo the id_modulo to set
     */
    public void setId_modulo(Integer id_modulo) {
        this.id_modulo = id_modulo;
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
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
     * @return the horas
     */
    public Integer getHoras() {
        return horas;
    }

    /**
     * @param horas the horas to set
     */
    public void setHoras(Integer horas) {
        this.horas = horas;
    }

    /**
     * @return the creditajeModulo
     */
    public Integer getCreditajeModulo() {
        return creditajeModulo;
    }

    /**
     * @param creditajeModulo the creditajeModulo to set
     */
    public void setCreditajeModulo(Integer creditajeModulo) {
        this.creditajeModulo = creditajeModulo;
    }

    /**
     * @return the prerequisitos
     */
    public List<Modulo> getPrerequisitos() {
        return prerequisitos;
    }

    /**
     * @param prerequisitos the prerequisitos to set
     */
    public void setPrerequisitos(List<Modulo> prerequisitos) {
        this.prerequisitos = prerequisitos;
    }

    /**
     * @return the carrera
     */
    public Carrera getCarrera() {
        return carrera;
    }

    /**
     * @param carrera the carrera to set
     */
    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
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

    public String prerequisitosToString() {
        String s = " ";
        for (Modulo m : prerequisitos) {
            if (s.compareTo(" ") == 0) {
                s = m.getCodigo();
            } else {
                s += ", " + m.getCodigo();
            }
        }
        return s;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id_modulo);
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
        final Modulo other = (Modulo) obj;
        if (!Objects.equals(this.id_modulo, other.id_modulo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombre + " [" + codigo + "]";
    }

    /**
     * @return the grupos
     */
    public List<Grupo> getGrupos() {
        return grupos;
    }

    /**
     * @param grupos the grupos to set
     */
    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
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
}

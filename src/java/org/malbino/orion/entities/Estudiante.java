/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.malbino.orion.util.Fecha;

/**
 *
 * @author malbino
 */
@Entity
@Table(name = "estudiante")

@PrimaryKeyJoinColumn(name = "id_persona")
@DiscriminatorValue("Estudiante")
public class Estudiante extends Usuario implements Serializable {

    @Column(unique = true)
    private Integer matricula;
    @Temporal(TemporalType.DATE)
    private Date fecha;

    private Boolean diplomaBachiller;
    private Boolean certificadoNacimiento;
    private Boolean cedulaIdentidad;
    private Boolean fotografias3X3;
    private Boolean certificadoFELCC;
    private Boolean certificadoFELCN;

    @OneToMany(mappedBy = "estudiante", orphanRemoval = true)
    private List<Nota> notas;

    @OneToMany(mappedBy = "estudiante", orphanRemoval = true)
    private List<Inscrito> inscritos;

    @Transient
    private Date fechaInscripcion;

    public Estudiante() {
    }

    public String fecha_ddMMyyyy() {
        return Fecha.formatearFecha_ddMMyyyy(fecha);
    }

    public String diplomaBachiller_siNo() {
        String s = "";
        if (diplomaBachiller) {
            s = "Sí";
        } else {
            s = "No";
        }
        return s;
    }

    public String certificadoNacimiento_siNo() {
        String s = "";
        if (certificadoNacimiento) {
            s = "Sí";
        } else {
            s = "No";
        }
        return s;
    }

    public String cedulaIdentidad_siNo() {
        String s = "";
        if (cedulaIdentidad) {
            s = "Sí";
        } else {
            s = "No";
        }
        return s;
    }

    public String fotografias3X3_siNo() {
        String s = "";
        if (fotografias3X3) {
            s = "Sí";
        } else {
            s = "No";
        }
        return s;
    }

    public String certificadoFELCC_siNo() {
        String s = "";
        if (certificadoFELCC) {
            s = "Sí";
        } else {
            s = "No";
        }
        return s;
    }

    public String certificadoFELCN_siNo() {
        String s = "";
        if (certificadoFELCN) {
            s = "Sí";
        } else {
            s = "No";
        }
        return s;
    }

    /**
     * @return the matricula
     */
    public Integer getMatricula() {
        return matricula;
    }

    /**
     * @param matricula the matricula to set
     */
    public void setMatricula(Integer matricula) {
        this.matricula = matricula;
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the diplomaBachiller
     */
    public Boolean getDiplomaBachiller() {
        return diplomaBachiller;
    }

    /**
     * @param diplomaBachiller the diplomaBachiller to set
     */
    public void setDiplomaBachiller(Boolean diplomaBachiller) {
        this.diplomaBachiller = diplomaBachiller;
    }

    /**
     * @return the certificadoNacimiento
     */
    public Boolean getCertificadoNacimiento() {
        return certificadoNacimiento;
    }

    /**
     * @param certificadoNacimiento the certificadoNacimiento to set
     */
    public void setCertificadoNacimiento(Boolean certificadoNacimiento) {
        this.certificadoNacimiento = certificadoNacimiento;
    }

    /**
     * @return the cedulaIdentidad
     */
    public Boolean getCedulaIdentidad() {
        return cedulaIdentidad;
    }

    /**
     * @param cedulaIdentidad the cedulaIdentidad to set
     */
    public void setCedulaIdentidad(Boolean cedulaIdentidad) {
        this.cedulaIdentidad = cedulaIdentidad;
    }

    /**
     * @return the fotografias3X3
     */
    public Boolean getFotografias3X3() {
        return fotografias3X3;
    }

    /**
     * @param fotografias3X3 the fotografias3X3 to set
     */
    public void setFotografias3X3(Boolean fotografias3X3) {
        this.fotografias3X3 = fotografias3X3;
    }

    /**
     * @return the certificadoFELCC
     */
    public Boolean getCertificadoFELCC() {
        return certificadoFELCC;
    }

    /**
     * @param certificadoFELCC the certificadoFELCC to set
     */
    public void setCertificadoFELCC(Boolean certificadoFELCC) {
        this.certificadoFELCC = certificadoFELCC;
    }

    /**
     * @return the certificadoFELCN
     */
    public Boolean getCertificadoFELCN() {
        return certificadoFELCN;
    }

    /**
     * @param certificadoFELCN the certificadoFELCN to set
     */
    public void setCertificadoFELCN(Boolean certificadoFELCN) {
        this.certificadoFELCN = certificadoFELCN;
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
     * @return the inscritos
     */
    public List<Inscrito> getInscritos() {
        return inscritos;
    }

    /**
     * @param inscritos the inscritos to set
     */
    public void setInscritos(List<Inscrito> inscritos) {
        this.inscritos = inscritos;
    }

    /**
     * @return the fechaInscripcion
     */
    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    /**
     * @param fechaInscripcion the fechaInscripcion to set
     */
    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

}

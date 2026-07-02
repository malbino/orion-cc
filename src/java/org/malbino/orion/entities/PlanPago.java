/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.malbino.orion.util.Redondeo;

/**
 *
 * @author malbino
 */
@Entity
@Table(name = "planpago")
public class PlanPago implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_planpago;

    private String nombre;

    private Integer numeroCuotas;
    @Column(precision = 34, scale = 9)
    private BigDecimal montoCuota;

    @JoinColumn(name = "id_carrera")
    @ManyToOne
    private Carrera carrera;

    public PlanPago() {
    }

    public PlanPago(String nombre, Integer numeroCuotas, BigDecimal montoCuota, Carrera carrera) {
        this.nombre = nombre;
        this.numeroCuotas = numeroCuotas;
        this.montoCuota = montoCuota;
        this.carrera = carrera;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id_planpago);
        hash = 79 * hash + Objects.hashCode(this.nombre);
        hash = 79 * hash + Objects.hashCode(this.numeroCuotas);
        hash = 79 * hash + Objects.hashCode(this.montoCuota);
        hash = 79 * hash + Objects.hashCode(this.carrera);
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
        final PlanPago other = (PlanPago) obj;
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        if (!Objects.equals(this.id_planpago, other.id_planpago)) {
            return false;
        }
        if (!Objects.equals(this.numeroCuotas, other.numeroCuotas)) {
            return false;
        }
        if (!Objects.equals(this.montoCuota, other.montoCuota)) {
            return false;
        }
        return Objects.equals(this.carrera, other.carrera);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PlanPago{");
        sb.append("id_planpago=").append(id_planpago);
        sb.append(", nombre=").append(nombre);
        sb.append(", numeroCuotas=").append(numeroCuotas);
        sb.append(", montoCuota=").append(montoCuota);
        sb.append(", carrera=").append(carrera);
        sb.append('}');
        return sb.toString();
    }

    public String montoCuota() {
        return Redondeo.formatear_csm(montoCuota);
    }
    
    public String total() {
        BigDecimal total = BigDecimal.valueOf(numeroCuotas).multiply(montoCuota);
        return Redondeo.formatear_csm(total);
    }

    /**
     * @return the id_planpago
     */
    public Integer getId_planpago() {
        return id_planpago;
    }

    /**
     * @param id_planpago the id_planpago to set
     */
    public void setId_planpago(Integer id_planpago) {
        this.id_planpago = id_planpago;
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
     * @return the numeroCuotas
     */
    public Integer getNumeroCuotas() {
        return numeroCuotas;
    }

    /**
     * @param numeroCuotas the numeroCuotas to set
     */
    public void setNumeroCuotas(Integer numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }

    /**
     * @return the montoCuota
     */
    public BigDecimal getMontoCuota() {
        return montoCuota;
    }

    /**
     * @param montoCuota the montoCuota to set
     */
    public void setMontoCuota(BigDecimal montoCuota) {
        this.montoCuota = montoCuota;
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

}

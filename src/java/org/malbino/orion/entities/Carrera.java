/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.malbino.orion.enums.NivelAcademico;
import org.malbino.orion.util.Redondeo;

/**
 *
 * @author malbino
 */
@Entity
@Table(name = "carrera")
public class Carrera implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_carrera;
    
    @Column(unique = true)
    private String codigo;
    private String nombre;
    private NivelAcademico nivelAcademico;
    private Integer version;
    @Column(precision = 34, scale = 9)
    private BigDecimal precio;
    
    @JoinColumn(name = "id_instituto")
    @ManyToOne
    private Instituto instituto;
    
    @JoinColumn(name = "id_jefecarrera")
    @ManyToOne
    private Empleado jefeCarrera;
    
    @OneToMany(mappedBy = "carrera", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy(value = "nombre")
    private List<PlanPago> planesPago;
    
    @OneToMany(mappedBy = "carrera")
    private List<Modulo> modulos;
    
    public Carrera() {
        planesPago = new ArrayList<>();
    }
    
    public String idnumberMoodle() {
        return "c" + this.id_carrera;
    }
    
    public String nameMoodle() {
        return nombre + " v" + version;
    }
    
    public String fullNameMoodle() {
        return nameMoodle();
    }
    
    public String shortNameMoodle() {
        return codigo + " v" + version;
    }
    
    public String precio() {
        return Redondeo.formatear_csm(precio);
    }
    
    public BigDecimal precioHora() {
        Integer horasModulos = 0;
        for (Modulo modulo : modulos) {
            horasModulos = horasModulos + modulo.getHoras();
        }
        
        BigDecimal precioHoraSinRedondear = precio.divide(BigDecimal.valueOf(horasModulos), Redondeo.FULL_SCALE, RoundingMode.HALF_UP);
        
        return Redondeo.redondear_HALFUP(precioHoraSinRedondear, 0);
    }
    
    /**
     * @return the id_carrera
     */
    public Integer getId_carrera() {
        return id_carrera;
    }

    /**
     * @param id_carrera the id_carrera to set
     */
    public void setId_carrera(Integer id_carrera) {
        this.id_carrera = id_carrera;
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
     * @return the nivelAcademico
     */
    public NivelAcademico getNivelAcademico() {
        return nivelAcademico;
    }

    /**
     * @param nivelAcademico the nivelAcademico to set
     */
    public void setNivelAcademico(NivelAcademico nivelAcademico) {
        this.nivelAcademico = nivelAcademico;
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return the instituto
     */
    public Instituto getInstituto() {
        return instituto;
    }

    /**
     * @param instituto the instituto to set
     */
    public void setInstituto(Instituto instituto) {
        this.instituto = instituto;
    }

    /**
     * @return the jefeCarrera
     */
    public Empleado getJefeCarrera() {
        return jefeCarrera;
    }

    /**
     * @param jefeCarrera the jefeCarrera to set
     */
    public void setJefeCarrera(Empleado jefeCarrera) {
        this.jefeCarrera = jefeCarrera;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id_carrera);
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
        final Carrera other = (Carrera) obj;
        if (!Objects.equals(this.id_carrera, other.id_carrera)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * @return the precio
     */
    public BigDecimal getPrecio() {
        return precio;
    }

    /**
     * @param precio the precio to set
     */
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    /**
     * @return the planesPago
     */
    public List<PlanPago> getPlanesPago() {
        return planesPago;
    }

    /**
     * @param planesPago the planesPago to set
     */
    public void setPlanesPago(List<PlanPago> planesPago) {
        this.planesPago = planesPago;
    }

    /**
     * @return the modulos
     */
    public List<Modulo> getModulos() {
        return modulos;
    }

    /**
     * @param modulos the modulos to set
     */
    public void setModulos(List<Modulo> modulos) {
        this.modulos = modulos;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.malbino.orion.util.Fecha;
import org.malbino.orion.util.NumberToLetterConverter;
import org.malbino.orion.util.Redondeo;

/**
 *
 * @author malbino
 */
@Entity
@Table(name = "comprobante")
public class Comprobante implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_comprobante;

    private Integer numero;
    @Column(unique = true)
    private String codigo;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    private Boolean valido;

    @JoinColumn(name = "id_inscrito")
    @ManyToOne
    private Inscrito inscrito;

    @JoinColumn(name = "id_usuario")
    @ManyToOne
    private Usuario usuario;

    @OneToMany(mappedBy = "comprobante")
    private List<Detalle> detalles;

    public Comprobante() {
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
        final Comprobante other = (Comprobante) obj;
        if (!Objects.equals(this.id_comprobante, other.id_comprobante)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " [" + fecha_ddMMyyyy() + "]";
    }

    public String fecha_ddMMyyyy() {
        return Fecha.formatearFecha_ddMMyyyy(fecha);
    }

    public String validoToString() {
        return valido ? "Sí" : "No";
    }

    public String subtotal() {
        Integer subtotal = 0;
        for (Detalle detalle : detalles) {
            subtotal += detalle.getCantidad() * detalle.getPrecioUnitario();
        }
        return Redondeo.formatear_csm(subtotal);
    }

    public String descuento() {
        Integer descuento = 0;
        for (Detalle detalle : detalles) {
            descuento += detalle.getDescuento();
        }
        return Redondeo.formatear_csm(descuento);
    }

    public String montoPagar() {
        Integer subtotal = 0;
        for (Detalle detalle : detalles) {
            subtotal += detalle.getSubtotal();
        }
        return Redondeo.formatear_csm(subtotal);
    }

    public String montoPagarLiteral() {
        Integer subtotal = 0;
        for (Detalle detalle : detalles) {
            subtotal += detalle.getSubtotal();
        }
        return NumberToLetterConverter.convertNumberToLetterComprobante(subtotal);
    }

    public String usuario() {
        String s = "";
        if (usuario != null) {
            s = usuario.toString();
        }
        return s;
    }

    /**
     * @return the id_comprobante
     */
    public Integer getId_comprobante() {
        return id_comprobante;
    }

    /**
     * @param id_comprobante the id_comprobante to set
     */
    public void setId_comprobante(Integer id_comprobante) {
        this.id_comprobante = id_comprobante;
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
     * @return the valido
     */
    public Boolean getValido() {
        return valido;
    }

    /**
     * @param valido the valido to set
     */
    public void setValido(Boolean valido) {
        this.valido = valido;
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
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the detalles
     */
    public List<Detalle> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles the detalles to set
     */
    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.id_comprobante);
        return hash;
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
}

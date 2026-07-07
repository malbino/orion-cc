/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.UniqueConstraint;
import org.malbino.orion.enums.CondicionCuota;
import org.malbino.orion.util.Fecha;
import org.malbino.orion.util.Redondeo;

/**
 *
 * @author tincho
 */
@Entity
@Table(name = "cuota", uniqueConstraints = @UniqueConstraint(columnNames = {"codigo", "id_inscrito"}))
public class Cuota implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_cuota;

    private Integer numero;
    private String codigo;
    private String unidadMedida;
    private String descripcion;

    @Column(precision = 34, scale = 9)
    private BigDecimal monto;
    @Column(precision = 34, scale = 9)
    private BigDecimal pagado;

    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @JoinColumn(name = "id_inscrito")
    @ManyToOne
    private Inscrito inscrito;

    @OneToMany(mappedBy = "cuota")
    private List<Detalle> detalles;

    public Cuota() {
    }

    public Cuota(Integer numero, String codigo, String unidadMedida, String descripcion, BigDecimal monto, BigDecimal pagado, Inscrito inscrito) {
        this.numero = numero;
        this.codigo = codigo;
        this.unidadMedida = unidadMedida;
        this.descripcion = descripcion;
        this.monto = monto;
        this.pagado = pagado;
        this.inscrito = inscrito;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.id_cuota);
        hash = 41 * hash + Objects.hashCode(this.numero);
        hash = 41 * hash + Objects.hashCode(this.codigo);
        hash = 41 * hash + Objects.hashCode(this.unidadMedida);
        hash = 41 * hash + Objects.hashCode(this.descripcion);
        hash = 41 * hash + Objects.hashCode(this.monto);
        hash = 41 * hash + Objects.hashCode(this.pagado);
        hash = 41 * hash + Objects.hashCode(this.fechaVencimiento);
        hash = 41 * hash + Objects.hashCode(this.inscrito);
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
        final Cuota other = (Cuota) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        if (!Objects.equals(this.unidadMedida, other.unidadMedida)) {
            return false;
        }
        if (!Objects.equals(this.descripcion, other.descripcion)) {
            return false;
        }
        if (!Objects.equals(this.id_cuota, other.id_cuota)) {
            return false;
        }
        if (!Objects.equals(this.numero, other.numero)) {
            return false;
        }
        if (!Objects.equals(this.monto, other.monto)) {
            return false;
        }
        if (!Objects.equals(this.pagado, other.pagado)) {
            return false;
        }
        if (!Objects.equals(this.fechaVencimiento, other.fechaVencimiento)) {
            return false;
        }
        return Objects.equals(this.inscrito, other.inscrito);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cuota{");
        sb.append("id_cuota=").append(id_cuota);
        sb.append(", numero=").append(numero);
        sb.append(", codigo=").append(codigo);
        sb.append(", unidadMedida=").append(unidadMedida);
        sb.append(", descripcion=").append(descripcion);
        sb.append(", monto=").append(monto);
        sb.append(", pagado=").append(pagado);
        sb.append(", fechaVencimiento=").append(fechaVencimiento);
        sb.append(", inscrito=").append(inscrito);
        sb.append('}');
        return sb.toString();
    }

    public String monto() {
        return Redondeo.formatear_csm(monto);
    }

    public String pagado() {
        return Redondeo.formatear_csm(pagado);
    }

    public String adeudado() {
        BigDecimal adeudado = monto.subtract(pagado);
        return Redondeo.formatear_csm(adeudado);
    }

    public String fechaVencimiento() {
        String s = "";
        if (fechaVencimiento != null) {
            s = Fecha.formatearFecha_ddMMyyyy(fechaVencimiento);
        }
        return s;
    }

    public CondicionCuota condicion() {
        BigDecimal adeudado = monto.subtract(pagado);
        if (adeudado.compareTo(BigDecimal.ZERO) == 0) {
            return CondicionCuota.PAGADA;
        }
        return CondicionCuota.ADEUDADA;
    }

    public String comprobantes() {
        StringBuilder sb = new StringBuilder();
        for (Detalle detalle : detalles) {
            if (sb.length() == 0) {
                sb.append(detalle.getComprobante().getNumero());
                sb.append(" - ");
                sb.append(detalle.getComprobante().fecha_ddMMyyyy());
            } else {
                sb.append("\n" + detalle.getComprobante().getNumero());
                sb.append(" - ");
                sb.append(detalle.getComprobante().fecha_ddMMyyyy());
            }
        }
        return sb.toString();
    }

    /**
     * @return the id_cuota
     */
    public Integer getId_cuota() {
        return id_cuota;
    }

    /**
     * @param id_cuota the id_cuota to set
     */
    public void setId_cuota(Integer id_cuota) {
        this.id_cuota = id_cuota;
    }

    /**
     * @return the nunmero
     */
    public Integer getNumero() {
        return numero;
    }

    /**
     * @param numero the nunmero to set
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
     * @return the unidadMedida
     */
    public String getUnidadMedida() {
        return unidadMedida;
    }

    /**
     * @param unidadMedida the unidadMedida to set
     */
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return the monto
     */
    public BigDecimal getMonto() {
        return monto;
    }

    /**
     * @param monto the monto to set
     */
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    /**
     * @return the pagado
     */
    public BigDecimal getPagado() {
        return pagado;
    }

    /**
     * @param pagado the pagado to set
     */
    public void setPagado(BigDecimal pagado) {
        this.pagado = pagado;
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
     * @return the fechaVencimiento
     */
    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    /**
     * @param fechaVencimiento the fechaVencimiento to set
     */
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
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

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.malbino.orion.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author tincho
 */
@Entity
@Table(name = "cuota")
public class Cuota implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_cuota;

    private String codigo;
    private String concepto;
    private Integer numero;
    
    @Column(precision = 34, scale = 9)
    private BigDecimal monto;
    @Column(precision = 34, scale = 9)
    private BigDecimal pagado;
    @Column(precision = 34, scale = 9)
    private BigDecimal adeudado;
    
    private String condicion;

    @Transient
    private Integer pago;
    @Transient
    private Integer monto_sindescuento;

    @JoinColumn(name = "id_inscrito")
    @ManyToOne
    private Inscrito inscrito;

    public Cuota() {
    }

}

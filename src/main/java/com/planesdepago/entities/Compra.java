package com.planesdepago.entities;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Compra implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer idTransaccion;

  private LocalDate fecha;

  @Column(columnDefinition = "DECIMAL(12,2)")
  private BigDecimal montoCompra;

  @Column(columnDefinition = "DECIMAL(12,2)")
  private BigDecimal montoAFinanciar;

  @Column(columnDefinition = "DECIMAL(12,2)")
  private BigDecimal saldoRestante;

  @OneToMany(mappedBy = "compraID", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Pago> pagos = new ArrayList<>();

  @OneToMany(mappedBy = "compraID", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Cuota> cuotas = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  private Cliente IDCliente;
  private int cantCuotas;
  private BigDecimal interes;
  private String descripcion;


  public BigDecimal getSaldoRestante() {
    return saldoRestante;
  }

  public void setSaldoRestante(BigDecimal saldoRestante) {
    this.saldoRestante = saldoRestante;
  }

  public void addPago(Pago pago) {
    pagos.add(pago);
    pago.setCompraID(this);
  }

  public void removeCuota(Cuota cuota) {
    cuotas.remove(cuota);
    cuota.setCompraID(null);
  }


  public void addCuota(Cuota cuota) {
    cuotas.add(cuota);
    cuota.setCompraID(this);
  }

  public void removePago(Pago pago) {
    pagos.remove(pago);
    pago.setCompraID(null);
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  public BigDecimal getMontoCompra() {
    return montoCompra;
  }

  public void setMontoCompra(BigDecimal montoCompra) {
    this.montoCompra = montoCompra;
  }

  public BigDecimal getMontoAFinanciar() {
    return montoAFinanciar;
  }

  public void setMontoAFinanciar(BigDecimal montoAFinanciar) {
    this.montoAFinanciar = montoAFinanciar;
  }

  public int getCantCuotas() {
    return cantCuotas;
  }

  public void setCantCuotas(int cantCuotas) {
    this.cantCuotas = cantCuotas;
  }

  public BigDecimal getInteres() {
    return interes;
  }

  public void setInteres(BigDecimal interes) {
    this.interes = interes;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public List<Pago> getPagos() {
    return pagos;
  }

  public void setPagos(List<Pago> pagos) {

    for (Pago pago : pagos) {
      addPago(pago);
    }

  }

  public Cliente getIDCliente() {
    return IDCliente;
  }

  public void setIDCliente(Cliente IDCliente) {
    this.IDCliente = IDCliente;
  }

  public List<Cuota> getCuotas() {
    return cuotas;
  }

  public void setCuotas(List<Cuota> cuotas) {
    for (Cuota cuota : cuotas) {
      addCuota(cuota);
    }
  }


  //Recupero todos los pagos realizados para esta compra y los sumo.
  public BigDecimal calcularTotalPagosRealizados() {
    BigDecimal total = new BigDecimal(0);
    for (Pago pago: pagos) {
      total = total.add(pago.getMontoPagado());

    }

    return total;
  }

}

package com.planesdepago.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Pago implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long idPago;

  @ManyToOne(fetch = FetchType.LAZY)
  private Compra compraID;

  @Column(columnDefinition = "DECIMAL(12,2)")
  private BigDecimal montoPagado;
  private LocalDate fechaPago;
  private String descripcionPago;


  @Override
  public int hashCode() {
    return Objects.hashCode(this.idPago);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Pago )) return false;
    return idPago != null && idPago.equals(((Pago) o).idPago);
  }
  public Long getIdPago() {
    return idPago;
  }

  public void setIdPago(Long idPago) {
    this.idPago = idPago;
  }

  public Compra getCompraID() {
    return compraID;
  }

  public void setCompraID(Compra compraID) {
    this.compraID = compraID;
  }

  public BigDecimal getMontoPagado() {
    return montoPagado;
  }

  public void setMontoPagado(BigDecimal montoPagado) {
    this.montoPagado = montoPagado;
  }

  public LocalDate getFechaPago() {
    return fechaPago;
  }

  public void setFechaPago(LocalDate fechaPago) {
    this.fechaPago = fechaPago;
  }

  public String getDescripcionPago() {
    return descripcionPago;
  }

  public void setDescripcionPago(String descripcionPago) {
    this.descripcionPago = descripcionPago;
  }
}

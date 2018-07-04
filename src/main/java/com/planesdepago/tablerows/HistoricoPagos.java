package com.planesdepago.tablerows;

import com.planesdepago.entities.Cheque;
import com.planesdepago.entities.Compra;
import com.planesdepago.uiutils.ListaTarjetas;
import com.planesdepago.uiutils.ListaTiposDePago;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

public class HistoricoPagos {

  private Compra compraID;
  private Long idPago;
  private BigDecimal montoPagado;
  private LocalDate fechaPago;
  private String descripcionPago;
  private String metodoPago;

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

  public String getMetodoPago() {
    return metodoPago;
  }

  public void setMetodoPago(String metodoPago) {
    this.metodoPago = metodoPago;
  }
}

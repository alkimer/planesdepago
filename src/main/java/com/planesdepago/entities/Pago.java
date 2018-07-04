package com.planesdepago.entities;

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

  private ListaTiposDePago tipoPago;
  @OneToOne(mappedBy = "idPago", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)

  private Cheque cheque;
  private String tarjeta;

  private Integer numeroRetencion;


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

  public ListaTiposDePago getTipoPago() {
    return tipoPago;
  }

  public void setTipoPago(ListaTiposDePago tipoPago) {
    this.tipoPago = tipoPago;
  }

  public Cheque getCheque() {
    return cheque;
  }

  public void setCheque(Cheque cheque) {
    this.cheque = cheque;
  }

  public String getTarjeta() {
    return tarjeta;
  }

  public void setTarjeta(String tarjeta) {
    this.tarjeta = tarjeta;
  }

  public void addCheque(Cheque cheque) {
    this.cheque = cheque;
    cheque.setIdPago(this);

  }

  public Integer getNumeroRetencion() {
    return numeroRetencion;
  }

  public void setNumeroRetencion(Integer numeroRetencion) {
    this.numeroRetencion = numeroRetencion;
  }
}

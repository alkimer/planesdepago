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
public class Cuota implements Serializable{

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long idCuota;

  @ManyToOne(fetch = FetchType.LAZY)
  private Compra compraID;

  private int nroCuota;

  @Column(columnDefinition = "DECIMAL(12,2)")
  private BigDecimal montoCuota;

  private String descripcion;
  private LocalDate fechaVencimiento;

  public int getNroCuota() {
    return nroCuota;
  }

  public void setNroCuota(int nroCuota) {
    this.nroCuota = nroCuota;
  }

  public BigDecimal getMontoCuota() {
    return montoCuota;
  }

  public void setMontoCuota(BigDecimal montoCuota) {
    this.montoCuota = montoCuota;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public LocalDate getFechaVencimiento() {
    return fechaVencimiento;
  }

  public void setFechaVencimiento(LocalDate fechaVencimiento) {
    this.fechaVencimiento = fechaVencimiento;
  }

  public Compra getCompraID() {
    return compraID;
  }

  public void setCompraID(Compra compraID) {
    this.compraID = compraID;
  }

  public Long getIdCuota() {
    return idCuota;
  }

  public void setIdCuota(Long idCuota) {
    this.idCuota = idCuota;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.idCuota);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Pago )) return false;
    return idCuota != null && idCuota.equals(((Cuota) o).idCuota);
  }
}

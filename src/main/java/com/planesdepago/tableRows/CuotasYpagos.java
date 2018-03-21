package com.planesdepago.tableRows;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CuotasYpagos {

  private int nroCuota;
  private BigDecimal montoCuota;
  private String descripcion;
  private LocalDate fechaVencimiento;
  private String cuotaPaga;

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

  public String getCuotaPaga() {
    return cuotaPaga;
  }

  public void setCuotaPaga(String cuotaPaga) {
    this.cuotaPaga = cuotaPaga;
  }
}

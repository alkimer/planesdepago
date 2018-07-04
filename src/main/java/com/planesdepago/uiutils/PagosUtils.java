package com.planesdepago.uiutils;


import static com.planesdepago.uiutils.DateUtils.formatLocalDate2StringPattern;

import com.planesdepago.entities.Cheque;
import com.planesdepago.entities.Pago;

public class PagosUtils {

  public synchronized static String obtenerDescripcion(Pago pago) {
    String texto = "";
    if (pago.getDescripcionPago() != null) {
      texto = pago.getDescripcionPago();
    }
    if (pago.getTipoPago() != null) {
      switch (pago.getTipoPago()) {
        case CHEQUE:
          Cheque cheque = pago.getCheque();
          texto += " Nro.cheque: " + cheque.getNroCheque() + " Vencimiento: " + formatLocalDate2StringPattern(cheque
              .getFechaVencimiento()) + ""
              + " Banco: " + cheque.getBanchoEmisor();
          break;
        case TARJETA:
          texto += " Tarjeta: " + pago.getTarjeta();
          break;

        case RETENCIONES:
          texto += "Número Retención: " + pago.getNumeroRetencion();
          break;
      }

    }
    return texto;
  }
}

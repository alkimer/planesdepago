package com.planesdepago.util;


import javafx.util.StringConverter;

import java.math.BigDecimal;

public class BigDecimalStringConverterCustom extends StringConverter<BigDecimal> {

  /** {@inheritDoc} */
  @Override public BigDecimal fromString(String value) {
    // If the specified value is null or zero-length, return null
    if (value == null) {
      return null;
    }

    value = value.trim();

    value = value.replaceAll(",",".");
    if (value.length() < 1) {
      return null;
    }
    BigDecimal resultado = BigDecimal.ZERO;
    try {
      resultado = new BigDecimal(value);
    } catch (Exception e) {

    }

    return resultado;
  }

  /** {@inheritDoc} */
  @Override public String toString(BigDecimal value) {
    // If the specified value is null, return a zero-length String
    if (value == null) {
      return "";
    }

    return ((BigDecimal)value).toString();
  }
}

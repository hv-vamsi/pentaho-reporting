/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula.util;

import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.typing.Type;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Cedric Pronzato
 */
public class DateUtil {
  private static final Date ISO8001_TIME = new GregorianCalendar().getTime();

  private DateUtil() {
  }

  /**
   * Converts a <code>Date</code> value according to the requested <code>Type</code> to the proper <code>Date</code>
   * subclasses (<code>java.sql.Time</code>, <code>java.sql.Date</code>) if needed. If the requested type is unknown, no
   * conversion takes place and the input date is returned.
   *
   * @param fromDate The date to convert.
   * @param toType   The requested type of date.
   * @return The converted date.
   */
  public static Date normalizeDate( final Date fromDate, final Type toType ) {
    return normalizeDate( fromDate, toType, true );
  }

  public static Date normalizeDate( Date fromDate, final Type toType, final boolean convertSerial ) {
    if ( fromDate == null ) {
      throw new IllegalArgumentException();
    }
    if ( toType == null ) {
      throw new IllegalArgumentException();
    }

    if ( convertSerial ) {
      BigDecimal serial = HSSFDateUtil.getExcelDate( fromDate );
      serial = normalizeDate( serial, toType );
      fromDate = HSSFDateUtil.getJavaDate( serial );
    }
    // final GregorianCalendar gc = new GregorianCalendar();
    // gc.setTime(fromDate);
    // gc.set(GregorianCalendar.MILLISECOND, 0);
    // fromDate = gc.getTime();
    if ( toType.isFlagSet( Type.TIME_TYPE ) ) {
      return new Time( fromDate.getTime() );
    } else if ( toType.isFlagSet( Type.DATE_TYPE ) ) {
      return new java.sql.Date( fromDate.getTime() );
    } else if ( toType.isFlagSet( Type.DATETIME_TYPE ) ) {
      return new Date( fromDate.getTime() );
    }

    return fromDate;
  }

  public static BigDecimal normalizeDate( final BigDecimal fromSerialDate, final Type toType ) {
    if ( fromSerialDate == null ) {
      throw new IllegalArgumentException();
    }
    if ( toType == null ) {
      throw new IllegalArgumentException();
    }


    if ( toType.isFlagSet( Type.TIME_TYPE ) ) {
      final BigDecimal o = fromSerialDate.setScale( LibFormulaBoot.GLOBAL_SCALE, BigDecimal.ROUND_UP );
      return o.subtract( new BigDecimal( o.intValue() ) );
      // only return the decimal part
      // final Double d = new Double(fromSerialDate.doubleValue()
      // - fromSerialDate.intValue());
      // return d;
    } else if ( toType.isFlagSet( Type.DATE_TYPE ) ) {
      return NumberUtil.performIntRounding( fromSerialDate );
    } else {
      // else it must be date-time, so return it unchanged ..
      return fromSerialDate;
    }
  }

  public static Date now( final FormulaContext context ) {
    final LocalizationContext localizationContext = context.getLocalizationContext();
    final GregorianCalendar gc =
      new GregorianCalendar( localizationContext.getTimeZone(), localizationContext.getLocale() );
    gc.setTime( context.getCurrentDate() );
    gc.set( Calendar.MILLISECOND, 0 );
    return gc.getTime();
  }

  public static Date createDateTime( final int year, final int month, final int day, final int hour,
                                     final int minute, final int second, final LocalizationContext context ) {
    final GregorianCalendar gc = new GregorianCalendar( context.getTimeZone(),
      context.getLocale() );
    gc.set( Calendar.DAY_OF_MONTH, day );
    gc.set( Calendar.MONTH, month );
    gc.set( Calendar.YEAR, year );
    gc.set( Calendar.MILLISECOND, 0 );
    gc.set( Calendar.HOUR_OF_DAY, hour );
    gc.set( Calendar.MINUTE, minute );
    gc.set( Calendar.SECOND, second );
    return gc.getTime();
  }

  public static Time createTime( final int hour, final int minute, final int second,
                                 final LocalizationContext context ) {
    final GregorianCalendar gc = new GregorianCalendar( context.getTimeZone(),
      context.getLocale() );
    gc.setTime( ISO8001_TIME );
    gc.set( Calendar.MILLISECOND, 0 );
    gc.set( Calendar.HOUR_OF_DAY, hour );
    gc.set( Calendar.MINUTE, minute );
    gc.set( Calendar.SECOND, second );
    return new Time( gc.getTime().getTime() );
  }

  public static java.sql.Date createDate( final int year, final int month, final int day,
                                          final LocalizationContext context ) {
    final GregorianCalendar gc = new GregorianCalendar( context.getTimeZone(),
      context.getLocale() );
    gc.set( Calendar.DAY_OF_MONTH, day );
    gc.set( Calendar.MONTH, month - 1 );
    gc.set( Calendar.YEAR, year );
    gc.set( Calendar.MILLISECOND, 0 );
    gc.set( Calendar.HOUR_OF_DAY, 0 );
    gc.set( Calendar.MINUTE, 0 );
    gc.set( Calendar.SECOND, 0 );
    return new java.sql.Date( gc.getTime().getTime() );
  }

  public static Calendar createCalendar( final Date date, final LocalizationContext context ) {
    final GregorianCalendar gc = new GregorianCalendar( context.getTimeZone(),
      context.getLocale() );
    gc.setTime( date );
    return gc;
  }
}

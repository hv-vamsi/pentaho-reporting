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


package org.pentaho.reporting.libraries.css.keys.line;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 24.11.2005, 16:43:12
 *
 * @author Thomas Morgner
 */
public class LineStackingShift {
  public static final CSSConstant CONSIDER_SHIFTS =
    new CSSConstant( "consider-shifts" );
  public static final CSSConstant DISREGARD_SHIFTS =
    new CSSConstant( "disregard-shifts" );

  private LineStackingShift() {
  }
}

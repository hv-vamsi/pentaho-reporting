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


package org.pentaho.reporting.libraries.css.values;

/**
 * Creation-Date: 23.11.2005, 12:04:06
 *
 * @author Thomas Morgner
 */
public class CSSRectangleType extends CSSType {
  public static final CSSRectangleType RECT = new CSSRectangleType( "rect" );
  public static final CSSRectangleType INSET_RECT = new CSSRectangleType( "inset-rect" );

  private CSSRectangleType( String name ) {
    super( name );
  }

  public boolean equals( Object obj ) {
    return ( obj instanceof CSSRectangleType && super.equals( obj ) );
  }
}

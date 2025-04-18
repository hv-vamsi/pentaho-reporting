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

import java.awt.*;

/**
 * Creation-Date: 23.11.2005, 12:01:04
 *
 * @author Thomas Morgner
 */
public class CSSColorValue extends Color implements CSSValue {
  public CSSColorValue( int r, int g, int b, int a ) {
    super( r, g, b, a );
  }

  public CSSColorValue( int rgba, boolean hasalpha ) {
    super( rgba, hasalpha );
  }

  public CSSColorValue( float r, float g, float b, float a ) {
    super( r, g, b, a );
  }

  public CSSColorValue( float r, float g, float b ) {
    super( r, g, b );
  }

  public CSSColorValue( int r, int g, int b ) {
    super( r, g, b );
  }

  public CSSColorValue( Color color ) {
    super( color.getRGB() );
  }

  public String getCSSText() {
    if ( getAlpha() == 0 ) {
      return "transparent";
    } else if ( getAlpha() == 255 ) {
      return "rgb(" + getRed() + ',' + getGreen() + ',' + getBlue() + ')';
    } else {
      return "rgba(" + getRed() + ',' + getGreen() + ',' + getBlue() + ',' + getAlpha() + ')';
    }
  }

  /**
   * Determines if this instance of the object is equals to another Object
   *
   * @return <code>true</code> if the supplied object is equivalent to this object, <code>false</code> otherwise
   */
  public boolean equals( Object obj ) {
    return ( obj instanceof CSSColorValue && super.equals( obj ) );
  }


  /**
   * Returns a string representation of this <code>Color</code>. This method is intended to be used only for debugging
   * purposes.  The content and format of the returned string might vary between implementations. The returned string
   * might be empty but cannot be <code>null</code>.
   *
   * @return a string representation of this <code>Color</code>.
   */
  public String toString() {
    return getCSSText();
  }

  public CSSType getType() {
    return CSSGenericType.GENERIC_TYPE;
  }
}

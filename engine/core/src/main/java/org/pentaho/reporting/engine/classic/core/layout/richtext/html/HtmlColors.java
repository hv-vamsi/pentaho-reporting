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


package org.pentaho.reporting.engine.classic.core.layout.richtext.html;

import java.awt.Color;
import java.util.HashMap;

/**
 * Creation-Date: 20.12.2005, 10:48:22
 *
 * @author Thomas Morgner
 */
public class HtmlColors {
  public static final Color BLACK = new Color( 0x000000, false );
  public static final Color GREEN = new Color( 0x008000, false );
  public static final Color SILVER = new Color( 0xC0C0C0, false );
  public static final Color LIME = new Color( 0x00FF00, false );
  public static final Color GRAY = new Color( 0x808080, false );
  public static final Color OLIVE = new Color( 0x808000, false );
  public static final Color WHITE = new Color( 0xFFFFFF, false );
  public static final Color YELLOW = new Color( 0xFFFF00, false );
  public static final Color MAROON = new Color( 0x800000, false );
  public static final Color NAVY = new Color( 0x000080, false );
  public static final Color RED = new Color( 0xFF0000, false );
  public static final Color BLUE = new Color( 0x0000FF, false );
  public static final Color PURPLE = new Color( 0x800080, false );
  public static final Color TEAL = new Color( 0x008080, false );
  public static final Color FUCHSIA = new Color( 0xFF00FF, false );
  public static final Color AQUA = new Color( 0x00FFFF, false );
  private static final HashMap colors;

  static {
    colors = new HashMap();
    colors.put( HtmlColors.BLACK, "black" );
    colors.put( HtmlColors.GREEN, "green" );
    colors.put( HtmlColors.SILVER, "silver" );
    colors.put( HtmlColors.LIME, "lime" );
    colors.put( HtmlColors.GRAY, "gray" );
    colors.put( HtmlColors.OLIVE, "olive" );
    colors.put( HtmlColors.WHITE, "white" );
    colors.put( HtmlColors.YELLOW, "yellow" );
    colors.put( HtmlColors.MAROON, "maroon" );
    colors.put( HtmlColors.NAVY, "navy" );
    colors.put( HtmlColors.BLUE, "blue" );
    colors.put( HtmlColors.PURPLE, "purple" );
    colors.put( HtmlColors.TEAL, "teal" );
    colors.put( HtmlColors.FUCHSIA, "fuchsia" );
    colors.put( HtmlColors.AQUA, "aqua" );
  }

  private HtmlColors() {

  }

  /**
   * Creates the color string for the given AWT color. If the color is one of the predefined HTML colors, then the
   * logical name is returned. For all other colors, the RGB-Tripple is returned.
   *
   * @param color
   *          the AWTColor that should be translated.
   * @return the translated html color definition
   */
  public static String getColorString( final Color color ) {

    final String colorName = (String) colors.get( color );
    if ( colorName != null ) {
      return colorName;
    }

    // no defined constant color, so this must be a user defined color
    final String colorText = Integer.toHexString( color.getRGB() & 0x00ffffff );
    final StringBuffer retval = new StringBuffer( 7 );
    retval.append( '#' );

    final int fillUp = 6 - colorText.length();
    for ( int i = 0; i < fillUp; i++ ) {
      retval.append( '0' );
    }

    retval.append( colorText );
    return retval.toString();
  }

}

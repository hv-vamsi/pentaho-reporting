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


package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * Creation-Date: 05.08.2007, 13:30:17
 *
 * @author Thomas Morgner
 */
public class BorderIT extends TestCase {
  public BorderIT( final String arg0 ) {
    super( arg0 );
  }

  public BorderIT() {
  }

  public void setUp() throws IOException {
    ClassicEngineBoot.getInstance().start();
  }

  public static void testBorder() throws IOException, ReportWriterException {
    Object[] columnNames = new Object[] { "Customer", "City", "Number" };

    DefaultTableModel reportTableModel =
        new DefaultTableModel( new Object[][] {
          { "Customer_ASDFSDFSDFSDFSaasdasdasdasweruzweurzwiezrwieuzriweuzriweu", "Bern", "123" },
          { "Hugo", "Z?rich", "2234" }, }, columnNames );

    MasterReport report = new MasterReport();

    report.setName( "BorderTest" );

    report.getItemBand().addElement(
        LabelElementFactory.createLabelElement( "CustomerLabel", new Rectangle2D.Double( 0, 0, 200, 100 ), Color.RED,
            ElementAlignment.LEFT, new FontDefinition( "Arial", 12 ), "CustomerLabel" ) );

    Element element =
        TextFieldElementFactory.createStringElement( "CustomerField", new Rectangle2D.Double( 210, 0, 150, 30 ),
            Color.black, ElementAlignment.LEFT, ElementAlignment.TOP, null, // font
            "-", // null string
            "Customer" );

    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, Color.RED );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, new Float( 1 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, BorderStyle.SOLID );

    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, Color.GREEN );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, new Float( 1 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, BorderStyle.SOLID );

    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, Color.YELLOW );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, new Float( 5 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, BorderStyle.SOLID );

    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, Color.CYAN );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, new Float( 5 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, BorderStyle.SOLID );

    element.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, new Color( 255, 127, 127, 120 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( 5 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( 5 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.OVERFLOW_X, Boolean.TRUE );

    report.getItemBand().addElement( element );
    report.setQuery( "default" );
    report.setDataFactory( new TableDataFactory( "default", reportTableModel ) );

    DebugReportRunner.execGraphics2D( report );
  }
}

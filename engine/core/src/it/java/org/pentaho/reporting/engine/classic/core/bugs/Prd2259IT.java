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


package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.print.PageFormat;

public class Prd2259IT extends TestCase {
  public Prd2259IT() {
    super();
  }

  public Prd2259IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private void addLabel( final String text, final Band band, final float height ) {
    final Element e = new Element();
    e.setId( text );
    e.setElementType( LabelType.INSTANCE );
    e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    e.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( height ) );
    e.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    band.addElement( e );
  }

  public void testBigBadCrash() throws Exception {
    final MasterReport report = new MasterReport();
    report.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final ReportHeader header = report.getReportHeader();

    final Band b = new Band();
    b.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
    b.getStyle().setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.MIDDLE );
    b.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 500 ) );
    b.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 100 ) );
    b.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, new Float( 2 ) );
    b.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, new Float( 2 ) );
    b.getStyle().setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, BorderStyle.SOLID );
    b.getStyle().setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, BorderStyle.SOLID );
    addLabel( "Text 1", b, 20 );
    addLabel( "Text 2", b, 40 );

    header.addElement( b );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( report, report.getReportHeader(), false, false );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    // ModelPrinter.print(logicalPageBox);
    new ValidateRunner().startValidation( logicalPageBox );

  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }

    protected boolean startBlockBox( final BlockRenderBox box ) {
      if ( "Text 1".equals( box.getName() ) ) {
        assertEquals( "Y=0pt", StrictGeomUtility.toInternalValue( 10 ), box.getY() );
        assertEquals( "Height=150pt", StrictGeomUtility.toInternalValue( 20 ), box.getHeight() );
      }
      if ( "Text 2".equals( box.getName() ) ) {
        assertEquals( "Y=0pt", 0, box.getY() );
        assertEquals( "Height=150pt", StrictGeomUtility.toInternalValue( 40 ), box.getHeight() );
      }
      return true;
    }
  }
}

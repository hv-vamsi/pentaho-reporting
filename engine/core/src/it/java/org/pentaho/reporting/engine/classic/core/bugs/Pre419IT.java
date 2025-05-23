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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.print.PageFormat;
import java.net.URL;

/**
 * Creation-Date: 10.08.2007, 13:45:14
 *
 * @author Thomas Morgner
 */
public class Pre419IT extends TestCase {
  public Pre419IT() {
  }

  public Pre419IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPre419() throws Exception {
    final URL target = Pre419IT.class.getResource( "Pre-419.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final LogicalPageBox logicalPageBox =
        DebugReportRunner.layoutSingleBand( basereport, report.getReportHeader(), false, true );
    // simple test, we assert that all paragraph-poolboxes are on either 485000 or 400000
    // and that only two lines exist for each
    new ValidateRunner().startValidation( logicalPageBox );

  }

  private static class ValidateRunner extends IterateStructuralProcessStep {
    public void startValidation( final LogicalPageBox logicalPageBox ) {
      startProcessing( logicalPageBox );
    }

    protected boolean startCanvasBox( final CanvasRenderBox box ) {
      if ( box.getName().equals( "test2" ) ) {
        assertEquals( "Y=10pt", StrictGeomUtility.toInternalValue( 10 ), box.getY() );
        assertEquals( "Height=10pt", StrictGeomUtility.toInternalValue( 10 ), box.getHeight() );
      }
      if ( box.getName().equals( "test" ) ) {
        assertEquals( "Y=5pt", StrictGeomUtility.toInternalValue( 5 ), box.getY() );
        assertEquals( "Height=20pt", StrictGeomUtility.toInternalValue( 20 ), box.getHeight() );
      }
      return super.startCanvasBox( box );
    }

    protected boolean startBlockBox( final BlockRenderBox box ) {
      if ( "reportheader".equals( box.getName() ) ) {
        assertEquals( "Y=0pt", 0, box.getY() );
        assertEquals( "Height=30pt", StrictGeomUtility.toInternalValue( 30 ), box.getHeight() );
      }
      return true;
    }
  }
}

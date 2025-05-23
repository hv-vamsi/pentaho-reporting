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


package org.pentaho.reporting.engine.classic.core.style;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleRule;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelector;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelectorFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;

public class StyleInheritanceIT extends TestCase {
  public StyleInheritanceIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private Element createLabel( final String text ) {
    final Element element = new Element();
    element.setName( text );
    element.setElementType( LabelType.INSTANCE );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 20 ) );
    element.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 200 ) );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    return element;
  }

  public void testStyleInheritance() throws Exception {
    MasterReport report = new MasterReport();
    ReportHeader reportHeader = report.getReportHeader();
    reportHeader.addElement( createLabel( "Master-Report-Header-Label" ) );
    report.setStyleDefinition( createStyleDefinition( "selected-font" ) );

    LogicalPageBox box = DebugReportRunner.layoutPage( report, 0 );
    RenderNode elementByName = MatchFactory.findElementByName( box, "Master-Report-Header-Label" );
    assertNotNull( elementByName );
    assertEquals( "selected-font", elementByName.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );
  }

  public void testStyleInheritanceOnSubReport() throws Exception {
    SubReport bandedSr = new SubReport();
    bandedSr.getReportHeader().addElement( createLabel( "Banded-SubReport-Header-Label" ) );

    SubReport inlineSr = new SubReport();
    inlineSr.getReportHeader().addElement( createLabel( "Inline-SubReport-Header-Label" ) );

    MasterReport report = new MasterReport();
    report.getReportFooter().addElement( inlineSr );
    report.getReportFooter().addElement( bandedSr );
    report.setStyleDefinition( createStyleDefinition( "selected-font" ) );

    LogicalPageBox box = DebugReportRunner.layoutPage( report, 0 );

    RenderNode inlineElement = MatchFactory.findElementByName( box, "Inline-SubReport-Header-Label" );
    assertNotNull( inlineElement );
    assertEquals( "selected-font", inlineElement.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );

    RenderNode bandedElement = MatchFactory.findElementByName( box, "Banded-SubReport-Header-Label" );
    assertNotNull( bandedElement );
    assertEquals( "selected-font", bandedElement.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );
  }

  public void testStylesOnSubreportAreNotSupported() throws Exception {
    SubReport bandedSr = new SubReport();
    bandedSr.getReportHeader().addElement( createLabel( "Banded-SubReport-Header-Label" ) );
    bandedSr.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET,
        createStyleDefinition( "selected-font-banded" ) );

    SubReport inlineSr = new SubReport();
    inlineSr.getReportHeader().addElement( createLabel( "Inline-SubReport-Header-Label" ) );
    inlineSr.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.STYLE_SHEET,
        createStyleDefinition( "selected-font-inline" ) );

    MasterReport report = new MasterReport();
    report.getReportFooter().addElement( inlineSr );
    report.getReportFooter().addElement( bandedSr );
    report.setStyleDefinition( createStyleDefinition( "selected-font" ) );

    LogicalPageBox box = DebugReportRunner.layoutPage( report, 0 );

    RenderNode inlineElement = MatchFactory.findElementByName( box, "Inline-SubReport-Header-Label" );
    assertNotNull( inlineElement );
    assertEquals( "selected-font", inlineElement.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );

    RenderNode bandedElement = MatchFactory.findElementByName( box, "Banded-SubReport-Header-Label" );
    assertNotNull( bandedElement );
    assertEquals( "selected-font", bandedElement.getStyleSheet().getStyleProperty( TextStyleKeys.FONT ) );
  }

  private ElementStyleDefinition createStyleDefinition( final String targetName ) {
    CSSSelectorFactory factory = new CSSSelectorFactory();

    ElementStyleRule rule = new ElementStyleRule();
    rule.addSelector( (CSSSelector) factory.createElementSelector( null, "label" ) );
    rule.setStyleProperty( TextStyleKeys.FONT, targetName );

    ElementStyleDefinition def = new ElementStyleDefinition();
    def.addRule( rule );
    return def;
  }

}

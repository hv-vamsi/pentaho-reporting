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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.AbstractElementReadHandler;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Map;

public class SubReportReadHandler extends AbstractElementReadHandler {
  private SubReport report;
  private ArrayList<SubReportParameterReadHandler> outputParameters;
  private ArrayList<SubReportParameterReadHandler> inputParameters;
  private ElementType elementType;
  private Class targetClass;

  public SubReportReadHandler() {
    this( SubReportType.INSTANCE, SubReport.class );
  }

  public SubReportReadHandler( final ElementType elementType, final Class targetClass ) {
    if ( elementType == null ) {
      throw new IllegalStateException();
    }

    this.targetClass = targetClass;
    this.elementType = elementType;
    this.outputParameters = new ArrayList<SubReportParameterReadHandler>();
    this.inputParameters = new ArrayList<SubReportParameterReadHandler>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String file = attrs.getValue( getUri(), "href" );
    if ( file != null ) {
      final Map parameters = deriveParseParameters();
      parameters.put( new FactoryParameterKey( ReportParserUtil.HELPER_OBJ_REPORT_NAME ), null );
      parameters.put( new FactoryParameterKey( ReportParserUtil.INCLUDE_PARSING_KEY ),
          ReportParserUtil.INCLUDE_PARSING_VALUE );
      try {
        report = (SubReport) performExternalParsing( file, targetClass, parameters );
      } catch ( ResourceLoadingException e ) {
        throw new ParseException( "The specified subreport was not found or could not be loaded.", e );
      }
    } else {
      throw new ParseException( "Required attribute 'href' is missing.", getLocator() );
    }

    initialize( elementType );
    super.startParsing( attrs );

    // clear the href, this is used only for internal purposes ...
    report.setAttribute( getUri(), "href", null );
  }

  protected Element createElement() throws ParseException {
    if ( report == null ) {
      throw new IllegalStateException();
    }
    return report;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( BundleNamespaces.LAYOUT.equals( uri ) ) {
      if ( "output-parameter".equals( tagName ) ) {
        final SubReportParameterReadHandler parameterReadHandler = new SubReportParameterReadHandler();
        outputParameters.add( parameterReadHandler );
        return parameterReadHandler;
      }
      if ( "input-parameter".equals( tagName ) ) {
        final SubReportParameterReadHandler parameterReadHandler = new SubReportParameterReadHandler();
        inputParameters.add( parameterReadHandler );
        return parameterReadHandler;
      }
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  public Element getElement() {
    return report;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    for ( int i = 0; i < inputParameters.size(); i++ ) {
      final SubReportParameterReadHandler handler = inputParameters.get( i );
      report.addInputParameter( handler.getMasterName(), handler.getDetailName() );
    }

    for ( int i = 0; i < outputParameters.size(); i++ ) {
      final SubReportParameterReadHandler handler = outputParameters.get( i );
      report.addExportParameter( handler.getMasterName(), handler.getDetailName() );
    }
  }
}

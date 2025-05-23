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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FormulaReadHandler extends AbstractXmlReadHandler {
  private PropertyReadHandler propertyReadHandler;

  public FormulaReadHandler() {
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "property".equals( tagName ) && "text".equals( atts.getValue( uri, "name" ) ) ) {
      propertyReadHandler = new PropertyReadHandler();
      return propertyReadHandler;
    }
    return null;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Expression getFormula() throws SAXException {
    if ( propertyReadHandler == null ) {
      return null;
    }

    String s = propertyReadHandler.getResult();
    if ( StringUtils.isEmpty( s, true ) == false ) {
      final FormulaExpression formulaExpression = new FormulaExpression();
      if ( s.startsWith( "report:" ) ) {
        s = "=" + s.substring( "report:".length() ).trim();
      } else {
        s = s.trim();
      }
      if ( s.endsWith( ";" ) ) {
        s = s.substring( 0, s.length() - 1 );
      }
      formulaExpression.setFormula( s );
      return formulaExpression;
    }
    return null;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return getFormula();
  }
}

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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.filter.types.EllipseType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

@Deprecated
public class EllipseReadHandler extends AbstractElementReadHandler {
  public EllipseReadHandler() throws ParseException {
    super( EllipseType.INSTANCE );
  }
}

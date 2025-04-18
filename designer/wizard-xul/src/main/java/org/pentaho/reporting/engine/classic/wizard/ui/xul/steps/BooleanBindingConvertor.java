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


package org.pentaho.reporting.engine.classic.wizard.ui.xul.steps;

import org.pentaho.ui.xul.binding.BindingConvertor;

/**
 * @author wseyler
 *         <p/>
 *         Does binding converstion between Boolean and Boolean (taking into account nulls)
 */
public class BooleanBindingConvertor extends BindingConvertor<Boolean, Boolean> {

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#sourceToTarget(java.lang.Object)
   */
  @Override
  public Boolean sourceToTarget( final Boolean value ) {
    return value == null ? false : value;
  }

  /* (non-Javadoc)
   * @see org.pentaho.ui.xul.binding.BindingConvertor#targetToSource(java.lang.Object)
   */
  @Override
  public Boolean targetToSource( final Boolean value ) {
    return value == null ? false : value;
  }

}

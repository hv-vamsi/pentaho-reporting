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


package org.pentaho.reporting.designer.core.util.dnd;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ElementMetaDataTransferable implements Transferable {
  public static final DataFlavor ELEMENT_FLAVOR =
    new DataFlavor( "application/x-pentaho-report-designer;class=" + ElementMetaData.class.getName(), //NON-NLS
      "ElementMetaData" ); //NON-NLS
  private ElementMetaData metaData;

  public ElementMetaDataTransferable( final ElementMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    this.metaData = metaData;
  }

  /**
   * Returns an array of DataFlavor objects indicating the flavors the data can be provided in.  The array should be
   * ordered according to preference for providing the data (from most richly descriptive to least descriptive).
   *
   * @return an array of data flavors in which this data can be transferred
   */
  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] { ELEMENT_FLAVOR };
  }

  /**
   * Returns whether or not the specified data flavor is supported for this object.
   *
   * @param flavor the requested flavor for the data
   * @return boolean indicating whether or not the data flavor is supported
   */
  public boolean isDataFlavorSupported( final DataFlavor flavor ) {
    return ELEMENT_FLAVOR.equals( flavor );
  }

  /**
   * Returns an object which represents the data to be transferred.  The class of the object returned is defined by the
   * representation class of the flavor.
   *
   * @param flavor the requested flavor for the data
   * @throws IOException                if the data is no longer available in the requested flavor.
   * @throws UnsupportedFlavorException if the requested data flavor is not supported.
   * @see DataFlavor#getRepresentationClass
   */
  public Object getTransferData( final DataFlavor flavor ) throws UnsupportedFlavorException, IOException {
    if ( isDataFlavorSupported( flavor ) == false ) {
      throw new UnsupportedFlavorException( flavor );
    }

    return metaData;
  }
}

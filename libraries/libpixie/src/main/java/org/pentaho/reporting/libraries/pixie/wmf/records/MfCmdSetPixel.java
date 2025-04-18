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


package org.pentaho.reporting.libraries.pixie.wmf.records;

import org.pentaho.reporting.libraries.pixie.wmf.GDIColor;
import org.pentaho.reporting.libraries.pixie.wmf.MfDcState;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

/**
 * Draws a single pixel with the given color on the specified position.
 */
public class MfCmdSetPixel extends MfCmd {
  private static final int RECORD_SIZE = 4;
  private static final int POS_COLOR = 0;
  private static final int POS_X = 3;
  private static final int POS_Y = 2;

  private int x;
  private int y;
  private int scaled_x;
  private int scaled_y;
  private Color color;

  public MfCmdSetPixel() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final Point p = getScaledTarget();
    final Graphics2D g = file.getGraphics2D();
    final MfDcState state = file.getCurrentState();

    state.prepareDraw();
    g.drawLine( p.x, p.y, p.x, p.y );
    state.postDraw();
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdSetPixel();
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setLongParam( POS_COLOR, GDIColor.translateColor( getColor() ) );
    final Point p = getTarget();
    record.setParam( POS_X, p.x );
    record.setParam( POS_Y, p.y );
    return record;
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int c = record.getLongParam( POS_COLOR );
    final Color color = new GDIColor( c );
    final int y = record.getParam( POS_Y );
    final int x = record.getParam( POS_X );
    setTarget( x, y );
    setColor( color );
  }

  public Point getTarget() {
    return new Point( x, y );
  }

  public Point getScaledTarget() {
    return new Point( scaled_x, scaled_y );
  }

  public void setTarget( final int x, final int y ) {
    this.x = x;
    this.y = y;
    scaleXChanged();
    scaleYChanged();
  }

  public void setTarget( final Point point ) {
    this.x = point.x;
    this.y = point.y;
  }

  public void setColor( final Color c ) {
    this.color = c;
  }

  public Color getColor() {
    return color;
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.SET_PIXEL;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[SET_PIXEL] target=" );
    b.append( getTarget() );
    b.append( " color=" );
    b.append( getColor() );
    return b.toString();
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_x = getScaledX( x );
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    scaled_y = getScaledY( y );
  }
}

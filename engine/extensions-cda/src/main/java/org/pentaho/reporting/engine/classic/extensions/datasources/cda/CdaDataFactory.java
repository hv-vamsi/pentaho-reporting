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


package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.table.TableModel;
import java.util.LinkedHashMap;

public class CdaDataFactory extends AbstractDataFactory {
  private LinkedHashMap<String, CdaQueryEntry> queries;
  private String baseUrl;
  private String baseUrlField;
  private boolean useLocalCall;
  private String username;
  private String password;

  private String solution;
  private String path;
  private String file;

  private boolean sugarMode;

  private CdaQueryBackend backend;
  private transient CdaQueryBackend effectiveBackend;

  public CdaDataFactory() {
    this.useLocalCall = true;
    this.queries = new LinkedHashMap<String, CdaQueryEntry>();
  }

  public boolean isUseLocalCall() {
    return useLocalCall;
  }

  public void setUseLocalCall( final boolean useLocalCall ) {
    this.useLocalCall = useLocalCall;
  }

  public void cancelRunningQuery() {
    if ( effectiveBackend != null ) {
      effectiveBackend.cancelRunningQuery();
    }
  }

  public void close() {
  }

  public String[] getQueryNames() {
    return queries.keySet().toArray( new String[ queries.size() ] );
  }

  public void initialize( final DataFactoryContext dataFactoryContext ) throws ReportDataFactoryException {
    super.initialize( dataFactoryContext );
    if ( backend != null ) {
      effectiveBackend = backend;
    } else {
      if ( useLocalCall ) {
        final String className = getConfiguration().getConfigProperty( CdaQueryBackend.class.getName() );
        effectiveBackend =
          ObjectUtilities.loadAndInstantiate( className, CdaQueryBackend.class, CdaQueryBackend.class );
      }
      if ( effectiveBackend == null ) {
        effectiveBackend = new HttpQueryBackend();
      }
    }
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return queries.containsKey( query );
  }

  public void setQueryEntry( final String name, final CdaQueryEntry cdaqueryentry ) {
    if ( cdaqueryentry == null ) {
      queries.remove( name );
    } else {
      queries.put( name, cdaqueryentry );
    }
  }

  public CdaQueryEntry getQueryEntry( final String name ) {
    return queries.get( name );
  }

  public TableModel queryData( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    final CdaQueryEntry realQuery = getQueryEntry( query );
    effectiveBackend.setFile( getFile() );
    effectiveBackend.setSolution( getSolution() );
    effectiveBackend.setPath( getPath() );
    effectiveBackend.setUsername( getUsername() );
    effectiveBackend.setPassword( getPassword() );
    effectiveBackend.setBaseUrl( computeBaseUrl( parameters ) );
    effectiveBackend.initialize( getDataFactoryContext() );
    effectiveBackend.setSugarMode( isSugarMode() );
    return effectiveBackend.queryData( realQuery, parameters );
  }

  private String computeBaseUrl( final DataRow dataRow ) {
    if ( baseUrlField != null ) {
      final Object baseUrlRaw = dataRow.get( baseUrlField );
      if ( baseUrlRaw != null ) {
        return String.valueOf( baseUrlRaw );
      }
    }
    return baseUrl;
  }

  public CdaDataFactory clone() {
    final CdaDataFactory dataFactory = (CdaDataFactory) super.clone();
    dataFactory.queries = (LinkedHashMap<String, CdaQueryEntry>) queries.clone();
    if ( backend != null ) {
      dataFactory.backend = (CdaQueryBackend) backend.clone();
    }
    return dataFactory;
  }

  public void setBackend( final CdaQueryBackend backend ) {
    if ( backend == null ) {
      throw new NullPointerException();
    }
    this.backend = backend;
  }

  public CdaQueryBackend getBackend() {
    return backend;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername( final String username ) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword( final String password ) {
    this.password = password;
  }

  public String getSolution() {
    return solution;
  }

  public void setSolution( final String solution ) {
    this.solution = solution;
  }

  public String getPath() {
    return path;
  }

  public void setPath( final String path ) {
    this.path = path;
  }

  public String getFile() {
    return file;
  }

  public void setFile( final String file ) {
    this.file = file;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl( final String baseUrl ) {
    this.baseUrl = baseUrl;
  }

  public String getBaseUrlField() {
    return baseUrlField;
  }

  public void setBaseUrlField( final String baseUrlField ) {
    this.baseUrlField = baseUrlField;
  }

  public boolean isSugarMode() {
    return sugarMode;
  }

  public void setSugarMode( boolean sugarMode ) {
    this.sugarMode = sugarMode;
  }

}

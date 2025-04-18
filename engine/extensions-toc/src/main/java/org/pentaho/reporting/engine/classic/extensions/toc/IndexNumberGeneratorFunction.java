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


package org.pentaho.reporting.engine.classic.extensions.toc;

import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

import java.util.ArrayList;

/**
 * A data-collector that collects table-of-contents items at group-starts. The function collects these items accross
 * subreport boundaries.
 *
 * @author Thomas Morgner.
 */
public class IndexNumberGeneratorFunction extends AbstractFunction {
  private int depth;
  private ArrayList groupCount;
  private boolean collectDetails;

  private transient int groupIndex;
  private transient boolean initialized;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public IndexNumberGeneratorFunction() {
    this.groupIndex = -1;
  }

  public boolean isCollectDetails() {
    return collectDetails;
  }

  public void setCollectDetails( final boolean collectDetails ) {
    this.collectDetails = collectDetails;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth( final int depth ) {
    this.depth = depth;
  }

  /**
   * Receives notification that report generation initializes the current run. <P> The event carries a
   * ReportState.Started state.  Use this to initialize the report.
   *
   * @param event The event.
   */
  public void reportInitialized( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      return;
    }

    if ( initialized == false ) {
      initialized = true;

      groupCount = new ArrayList( depth );
    }
    groupCount.clear();
    groupIndex = -1;
  }

  /**
   * Receives notification that the report has started.
   *
   * @param event the event.
   */
  public void reportStarted( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      return;
    }

    groupIndex = -1;
  }

  public void groupStarted( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      if ( "toc".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    groupIndex += 1;
    if ( groupCount.size() == groupIndex ) {
      // new level entered
      groupCount.add( IntegerCache.getInteger( 1 ) );
    } else {
      final int lastIndex = groupCount.size() - 1;
      if ( lastIndex == groupIndex ) {
        // existing level increased
        final Integer o = (Integer) groupCount.get( lastIndex );
        if ( o == null ) {
          throw new IllegalStateException();
        }
        groupCount.set( lastIndex, IntegerCache.getInteger( o.intValue() + 1 ) );
      } else {
        throw new IllegalStateException( "Out of index error: " + groupIndex + " " + groupCount.size() );
      }
    }
  }

  /**
   * Receives notification that a group of item bands is about to be processed. <P> The next events will be
   * itemsAdvanced events until the itemsFinished event is raised.
   *
   * @param event The event.
   */
  public void itemsStarted( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      if ( "toc".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    if ( collectDetails ) {
      groupIndex += 1;
    }
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      if ( "toc".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    if ( collectDetails ) {
      if ( groupIndex < depth || depth == 0 ) {
        if ( groupCount.size() == groupIndex ) {
          // new level entered
          groupCount.add( IntegerCache.getInteger( 1 ) );
        } else {
          final int lastIndex = groupCount.size() - 1;
          if ( lastIndex == groupIndex ) {
            // existing level increased
            final Integer o = (Integer) groupCount.get( lastIndex );
            if ( o == null ) {
              throw new IllegalStateException();
            }
            groupCount.set( lastIndex, IntegerCache.getInteger( o.intValue() + 1 ) );
          } else {
            throw new IllegalStateException( "Out of index error: " + groupIndex + " " + groupCount.size() );
          }
        }
      }
    }
  }


  /**
   * Receives notification that a group of item bands has been completed. <P> The itemBand is finished, the report
   * starts to close open groups.
   *
   * @param event The event.
   */
  public void itemsFinished( final ReportEvent event ) {
    if ( event.isDeepTraversing() ) {
      if ( "toc".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    if ( collectDetails ) {
      if ( ( groupIndex + 2 ) == groupCount.size() ) {
        groupCount.remove( groupCount.size() - 1 );
      }
      groupIndex -= 1;
    }
  }

  /**
   * Receives notification that a group has finished.
   *
   * @param event the event.
   */
  public void groupFinished( final ReportEvent event ) {
    super.groupFinished( event );
    if ( event.isDeepTraversing() ) {
      if ( "toc".equals( event.getOriginatingState().getReport().getMetaData().getName() ) ) {
        return;
      }
    }

    final Group group = FunctionUtilities.getCurrentDeepTraverseGroup( event );
    if ( group instanceof RelationalGroup == false ) {
      return;
    }

    if ( ( groupIndex + 2 ) == groupCount.size() ) {
      groupCount.remove( groupCount.size() - 1 );
    }
    groupIndex -= 1;
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Integer[] indexValues = new Integer[ groupCount.size() ];
    for ( int i = 0; i < indexValues.length; i++ ) {
      indexValues[ i ] = (Integer) this.groupCount.get( i );
    }
    return indexValues;
  }

  /**
   * Clones the expression.  The expression should be reinitialized after the cloning. <P> Expressions maintain no
   * state, cloning is done at the beginning of the report processing to disconnect the expression from any other object
   * space.
   *
   * @return a clone of this expression.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final IndexNumberGeneratorFunction o = (IndexNumberGeneratorFunction) super.clone();
    if ( groupCount != null ) {
      o.groupCount = (ArrayList) groupCount.clone();
    }
    return o;
  }

  /**
   * Checks whether this expression is a deep-traversing expression. Deep-traversing expressions receive events from all
   * sub-reports. This returns false by default, as ordinary expressions have no need to be deep-traversing.
   *
   * @return false.
   */
  public boolean isDeepTraversing() {
    return true;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final IndexNumberGeneratorFunction instance = (IndexNumberGeneratorFunction) super.getInstance();
    instance.groupCount = null;
    return instance;
  }
}

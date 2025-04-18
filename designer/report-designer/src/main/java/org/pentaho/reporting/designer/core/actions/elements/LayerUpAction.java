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


package org.pentaho.reporting.designer.core.actions.elements;

import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.groups.EditGroupsUndoEntry;
import org.pentaho.reporting.designer.core.editor.groups.GroupDataEntry;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.undo.BandedSubreportEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.DataSourceEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ExpressionAddedUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ExpressionRemoveUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ParameterEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.parameters.ModifiableReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

import javax.swing.*;
import java.util.ArrayList;

public class LayerUpAction extends AbstractLayerAction {
  public LayerUpAction() {
    putValue( Action.NAME, ActionMessages.getString( "LayerUpAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "LayerUpAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "LayerUpAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "LayerUpAction.Accelerator" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getLayerUpIcon() );
  }

  protected UndoEntry moveGroup( final RelationalGroup designerGroupElement )
    throws CloneNotSupportedException {
    if ( isSingleElementSelection() == false ) {
      return null;
    }

    final AbstractReportDefinition reportDefinition = getActiveContext().getReportDefinition();
    final GroupDataEntry[] entries = EditGroupsUndoEntry.buildGroupData( reportDefinition );
    final ArrayList<GroupDataEntry> list = new ArrayList<GroupDataEntry>( entries.length );
    int index = -1;
    for ( int i = 0; i < entries.length; i++ ) {
      final GroupDataEntry entry = entries[ i ];
      list.add( entry );
      if ( designerGroupElement.getObjectID() == entry.getInstanceID() ) {
        index = i;
      }
    }

    if ( index == -1 || index == ( list.size() - 1 ) ) {
      return null;
    }

    final GroupDataEntry o = list.get( index );
    list.remove( index );
    list.add( index + 1, o );

    final GroupDataEntry[] changedEntries = list.toArray( new GroupDataEntry[ list.size() ] );
    EditGroupsUndoEntry.applyGroupData( reportDefinition, changedEntries );

    return new EditGroupsUndoEntry( entries, changedEntries );
  }

  protected UndoEntry moveVisualElement( final AbstractReportDefinition report, final Element element ) {
    final Band reportElement = element.getParent();
    if ( reportElement != null == false ) {
      return null;
    }

    if ( element instanceof SubReport && reportElement instanceof AbstractRootLevelBand ) {
      final AbstractRootLevelBand re = (AbstractRootLevelBand) reportElement;
      final int count = re.getSubReportCount() - 1;
      for ( int i = 0; i < count; i++ ) {
        final SubReport sr = re.getSubReport( i );
        if ( sr == element ) {
          re.removeSubreport( sr );
          re.addSubReport( i + 1, sr );

          return new CompoundUndoEntry
            ( new BandedSubreportEditUndoEntry( re.getObjectID(), i, sr, null ),
              new BandedSubreportEditUndoEntry( re.getObjectID(), i - 1, null, sr ) );
        }
      }
    }

    final int count = reportElement.getElementCount() - 1;
    for ( int i = 0; i < count; i++ ) {
      final Element visualReportElement = reportElement.getElement( i );
      if ( element == visualReportElement ) {
        reportElement.removeElement( visualReportElement );
        reportElement.addElement( i + 1, visualReportElement );

        return new CompoundUndoEntry
          ( new ElementEditUndoEntry( reportElement.getObjectID(), i, visualReportElement, null ),
            new ElementEditUndoEntry( reportElement.getObjectID(), i + 1, null, visualReportElement ) );
      }
    }
    return null;
  }

  protected UndoEntry moveExpressions( final AbstractReportDefinition report, final Object element ) {
    final ExpressionCollection expressionCollection = report.getExpressions();
    final Expression[] expressions = expressionCollection.getExpressions();
    for ( int j = 1; j < expressions.length; j++ ) {
      final Expression expression = expressions[ j ];
      if ( element == expression ) {
        expressionCollection.removeExpression( j );
        expressionCollection.add( j - 1, expression );

        report.fireModelLayoutChanged( report, ReportModelEvent.NODE_STRUCTURE_CHANGED, expression );
        return new CompoundUndoEntry
          ( new ExpressionRemoveUndoEntry( j, expression ), new ExpressionAddedUndoEntry( j - 1, expression ) );
      }
    }
    return null;
  }


  protected UndoEntry moveDataFactories( final AbstractReportDefinition report, final Object element )
    throws ReportDataFactoryException {
    final CompoundDataFactory collection = (CompoundDataFactory) report.getDataFactory();
    final int dataFactoryCount = collection.size();
    for ( int j = 1; j < dataFactoryCount; j++ ) {
      final DataFactory dataFactory = collection.getReference( j );
      if ( element == dataFactory ) {
        collection.remove( j );
        collection.add( j - 1, dataFactory );

        report.fireModelLayoutChanged( report, ReportModelEvent.NODE_STRUCTURE_CHANGED, dataFactory );
        return new CompoundUndoEntry
          ( new DataSourceEditUndoEntry( j, dataFactory, null ),
            new DataSourceEditUndoEntry( j - 1, null, dataFactory ) );
      }
    }
    return null;
  }

  protected boolean collectChange( final Object[] selectedElements,
                                   final AbstractReportDefinition report,
                                   final ArrayList<UndoEntry> undos ) {
    for ( int i = selectedElements.length - 1; i >= 0; i-- ) {
      final Object element = selectedElements[ i ];
      if ( move( element, report, undos ) == false ) {
        return false;
      }
    }
    return true;
  }

  protected UndoEntry moveParameters( final AbstractReportDefinition report, final Object element ) {
    if ( report instanceof MasterReport == false ) {
      return null;
    }
    final MasterReport mr = (MasterReport) report;
    final ReportParameterDefinition definition = mr.getParameterDefinition();
    if ( definition instanceof ModifiableReportParameterDefinition == false ) {
      return null;
    }

    final ModifiableReportParameterDefinition dpd = (ModifiableReportParameterDefinition) definition;
    final ParameterDefinitionEntry[] entries = dpd.getParameterDefinitions();
    for ( int i = 1; i < entries.length; i++ ) {
      final ParameterDefinitionEntry entry = entries[ i ];
      if ( element == entry ) {
        dpd.removeParameterDefinition( i );
        dpd.addParameterDefinition( i - 1, entry );
        report.fireModelLayoutChanged( report, ReportModelEvent.NODE_STRUCTURE_CHANGED, entry );
        return new CompoundUndoEntry
          ( new ParameterEditUndoEntry( i, entry, null ), new ParameterEditUndoEntry( i - 1, null, entry ) );
      }
    }
    return null;
  }

}

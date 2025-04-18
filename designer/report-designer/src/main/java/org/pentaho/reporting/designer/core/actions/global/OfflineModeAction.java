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


package org.pentaho.reporting.designer.core.actions.global;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class OfflineModeAction extends AbstractDesignerContextAction
  implements ToggleStateAction, SettingsListener {
  public OfflineModeAction() {
    putValue( Action.NAME, ActionMessages.getString( "OfflineModeAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "OfflineModeAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "OfflineModeAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "OfflineModeAction.Accelerator" ) );

    WorkspaceSettings.getInstance().addSettingsListener( this );
    settingsChanged();
  }

  public boolean isSelected() {
    return Boolean.TRUE.equals( getValue( Action.SELECTED_KEY ) );
  }

  public void setSelected( final boolean selected ) {
    putValue( Action.SELECTED_KEY, selected );
  }

  public void settingsChanged() {
    putValue( Action.SELECTED_KEY, WorkspaceSettings.getInstance().isOfflineMode() );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final boolean snap = WorkspaceSettings.getInstance().isOfflineMode();
    WorkspaceSettings.getInstance().setOfflineMode( !snap );
  }
}

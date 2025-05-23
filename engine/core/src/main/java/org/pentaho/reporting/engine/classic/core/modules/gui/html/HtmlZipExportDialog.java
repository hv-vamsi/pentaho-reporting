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


package org.pentaho.reporting.engine.classic.core.modules.gui.html;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.AbstractExportDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.EncodingComboBoxModel;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.JStatusBar;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

/**
 * A dialog that is used to perform the printing of a report into an HTML file.
 *
 * @author Heiko Evermann
 */
public class HtmlZipExportDialog extends AbstractExportDialog {
  /**
   * The 'HTML encoding' property key.
   */
  public static final String HTML_OUTPUT_ENCODING =
      "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Encoding"; //$NON-NLS-1$
  /**
   * A default value of the 'HTML encoding' property key.
   */
  public static final String HTML_OUTPUT_ENCODING_DEFAULT = "UTF-16"; //$NON-NLS-1$

  /**
   * An action to select the export target file.
   */
  private class ActionSelectDirFile extends AbstractAction {
    /**
     * Default constructor.
     */
    protected ActionSelectDirFile( final ResourceBundle resources ) {
      putValue( Action.NAME, resources.getString( "htmlexportdialog.selectFile" ) ); //$NON-NLS-1$
    }

    /**
     * Receives notification that the action has occurred.
     *
     * @param e
     *          the action event.
     */
    public void actionPerformed( final ActionEvent e ) {
      performSelectFileZip();
    }
  }

  /**
   * Filename text field.
   */
  private JTextField txFilename;

  /**
   * Data file name field.
   */
  private JTextField txDataFilename;

  /**
   * Title text field.
   */
  private JTextField txTitle;

  /**
   * Author text field.
   */
  private JTextField txAuthor;

  /**
   * A combo-box for selecting the encoding.
   */
  private JComboBox cbEncoding;

  /**
   * The encoding data model.
   */
  private EncodingComboBoxModel encodingModel;

  /**
   * A check-box for selecting 'strict layout'.
   */
  private JCheckBox cbxStrictLayout;

  /**
   * A check-box for...
   */
  private JCheckBox cbxCopyExternalReferences;

  /**
   * A file chooser for a ZIP file.
   */
  private JFileChooser fileChooserZip;

  private static final String ZIP_FILE_EXTENSION = ".zip"; //$NON-NLS-1$
  private static final String JAR_FILE_EXTENSION = ".jar"; //$NON-NLS-1$
  private JStatusBar statusBar;
  private JTextField txKeywords;
  private JTextField txDescription;

  /**
   * Creates a new HTML save dialog.
   *
   * @param owner
   *          the dialog owner.
   */
  public HtmlZipExportDialog( final Frame owner ) {
    super( owner );
    initConstructor();
  }

  /**
   * Creates a new HTML export dialog.
   *
   * @param owner
   *          the dialog owner.
   */
  public HtmlZipExportDialog( final Dialog owner ) {
    super( owner );
    initConstructor();
  }

  /**
   * Creates a new HTML save dialog. The created dialog is modal.
   */
  public HtmlZipExportDialog() {
    initConstructor();
  }

  /**
   * Initialisation.
   */
  private void initConstructor() {
    statusBar = new JStatusBar();
    setTitle( getResources().getString( "htmlexportdialog.dialogtitle" ) ); //$NON-NLS-1$
    initialize();
    clear();
  }

  public JStatusBar getStatusBar() {
    return statusBar;
  }

  private JPanel createExportPanel() {
    final JLabel lblDirFileName = new JLabel( getResources().getString( "htmlexportdialog.filename" ) ); //$NON-NLS-1$
    final JLabel lblDirDataFileName = new JLabel( getResources().getString( "htmlexportdialog.datafilename" ) ); //$NON-NLS-1$

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets( 1, 1, 1, 5 );
    contentPane.add( lblDirFileName, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 1;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( txFilename, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 2;
    gbc.gridy = 1;
    contentPane.add( new JButton( new ActionSelectDirFile( getResources() ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets( 1, 1, 1, 5 );
    contentPane.add( lblDirDataFileName, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.weightx = 1;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( txDataFilename, gbc );

    final JPanel advancedOptionsPane = new JPanel();
    advancedOptionsPane.setLayout( new BorderLayout() );
    advancedOptionsPane.add( contentPane, BorderLayout.NORTH );
    return advancedOptionsPane;
  }

  private JPanel createExportOptionsPanel() {
    final JLabel lblEncoding = new JLabel( getResources().getString( "htmlexportdialog.encoding" ) ); //$NON-NLS-1$
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.weightx = 1;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( cbxCopyExternalReferences, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblEncoding, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( cbEncoding, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( cbxStrictLayout, gbc );

    final JPanel advancedOptionsPane = new JPanel();
    advancedOptionsPane.setLayout( new BorderLayout() );
    advancedOptionsPane.add( contentPane, BorderLayout.NORTH );
    return advancedOptionsPane;
  }

  private JPanel createMetaDataPanel() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );

    final JLabel lblAuthor = new JLabel( getResources().getString( "htmlexportdialog.author" ) ); //$NON-NLS-1$
    final JLabel lblTitel = new JLabel( getResources().getString( "htmlexportdialog.title" ) ); //$NON-NLS-1$
    final JLabel lblKeywords = new JLabel( getResources().getString( "htmlexportdialog.keywords" ) ); //$NON-NLS-1$
    final JLabel lblDescription = new JLabel( getResources().getString( "htmlexportdialog.description" ) ); //$NON-NLS-1$

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    contentPane.add( lblTitel, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblAuthor, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblKeywords, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( lblDescription, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 3, 1, 1, 1 );
    contentPane.add( txTitle, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( txAuthor, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( txKeywords, gbc );

    gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 1, 1, 1, 1 );
    contentPane.add( txDescription, gbc );

    final JPanel advancedOptionsPane = new JPanel();
    advancedOptionsPane.setLayout( new BorderLayout() );
    advancedOptionsPane.add( contentPane, BorderLayout.NORTH );
    return advancedOptionsPane;
  }

  /**
   * Initializes the Swing components of this dialog.
   */
  private void initialize() {

    txAuthor = new JTextField();
    txAuthor.setColumns( 40 );
    txTitle = new JTextField();
    txTitle.setColumns( 40 );
    txKeywords = new JTextField();
    txKeywords.setColumns( 40 );
    txDescription = new JTextField();
    txDescription.setColumns( 40 );
    txFilename = new JTextField();
    txFilename.setColumns( 40 );
    txDataFilename = new JTextField();
    txDataFilename.setColumns( 40 );

    encodingModel = EncodingComboBoxModel.createDefaultModel( Locale.getDefault() );
    encodingModel.sort();

    cbEncoding = new JComboBox( encodingModel );
    cbxStrictLayout = new JCheckBox( getResources().getString( "htmlexportdialog.strict-layout" ) ); //$NON-NLS-1$
    cbxCopyExternalReferences = new JCheckBox( getResources().getString( "htmlexportdialog.copy-external-references" ) ); //$NON-NLS-1$

    getFormValidator().registerButton( cbxStrictLayout );
    getFormValidator().registerTextField( txFilename );
    getFormValidator().registerTextField( txDataFilename );
    getFormValidator().registerComboBox( cbEncoding );

    final JPanel exportPane = createExportPanel();

    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    final boolean advancedSettingsTabAvail =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.zip.AdvancedSettingsAvailable" ) );
    final boolean metaDataSettingsTabAvail =
        "true"
            .equals( config
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.zip.MetaDataSettingsAvailable" ) );
    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add( getResources().getString( "htmlexportdialog.export-settings" ), exportPane ); //$NON-NLS-1$
    tabbedPane.add( getResources().getString( "htmlexportdialog.parameters" ), getParametersPanel() ); //$NON-NLS-1$

    if ( metaDataSettingsTabAvail ) {
      tabbedPane.add( getResources().getString( "htmlexportdialog.metadata-settings" ), createMetaDataPanel() ); //$NON-NLS-1$
    }
    if ( advancedSettingsTabAvail ) {
      tabbedPane.add( getResources().getString( "htmlexportdialog.advanced-settings" ), createExportOptionsPanel() ); //$NON-NLS-1$
    }

    setContentPane( createContentPane( tabbedPane ) );
  }

  /**
   * Returns the title of the HTML file.
   *
   * @return the title
   */
  public String getHTMLTitle() {
    return txTitle.getText();
  }

  /**
   * Defines the title of the HTML file.
   *
   * @param title
   *          the title
   */
  public void setHTMLTitle( final String title ) {
    this.txTitle.setText( title );
  }

  /**
   * Gets the author of the dialog. This is not yet implemented in the HTML-Target.
   *
   * @return the name of the author of this report.
   */
  public String getAuthor() {
    return txAuthor.getText();
  }

  /**
   * Defines the Author of the report. Any freeform text is valid. This defaults to the value of the systemProperty
   * "user.name".
   *
   * @param author
   *          the name of the author.
   */
  public void setAuthor( final String author ) {
    this.txAuthor.setText( author );
  }

  public void setKeywords( final String keywords ) {
    this.txKeywords.setText( keywords );
  }

  public String getKeywords() {
    return this.txKeywords.getText();
  }

  public void setDescription( final String description ) {
    this.txDescription.setText( description );
  }

  public String getDescription() {
    return this.txDescription.getText();
  }

  /**
   * Clears all selections and input fields.
   */
  public void clear() {
    txAuthor.setText( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( "user.name", "" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    txFilename.setText( "" ); //$NON-NLS-1$
    txDataFilename.setText( "" ); //$NON-NLS-1$
    txTitle.setText( "" ); //$NON-NLS-1$
    txKeywords.setText( "" ); //$NON-NLS-1$
    txDescription.setText( "" ); //$NON-NLS-1$
    cbEncoding.setSelectedIndex( encodingModel.indexOf( EncodingRegistry.getPlatformDefaultEncoding() ) );
    cbxCopyExternalReferences.setSelected( false );
    cbxStrictLayout.setSelected( false );
  }

  /**
   * Returns a new (and not connected to the default config from the job) configuration containing all properties from
   * the dialog.
   *
   * @param full
   * @return
   */
  protected Configuration grabDialogContents( final boolean full ) {
    final DefaultConfiguration p = new DefaultConfiguration();
    if ( full ) {
      p.setProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.ExportMethod", "zip" ); //$NON-NLS-1$ //$NON-NLS-2$
      p.setProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.zip.DataDirectory", //$NON-NLS-1$
          getDataFilename() );
      p.setProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.zip.TargetFileName", //$NON-NLS-1$
          getFilename() );
    }

    p.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Keywords", //$NON-NLS-1$
        getKeywords() );
    p.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Description", //$NON-NLS-1$
        getDescription() );
    p.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Author", //$NON-NLS-1$
        getAuthor() );
    p.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Encoding", //$NON-NLS-1$
        getEncoding() );
    p.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Title", //$NON-NLS-1$
        getHTMLTitle() );
    p.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.StrictLayout",
    //$NON-NLS-1$
        String.valueOf( isStrictLayout() ) );
    return p;
  }

  protected void setDialogContents( final Configuration p ) {
    setHTMLTitle( p.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Title", //$NON-NLS-1$
        p.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Title" ) ) ); //$NON-NLS-1$
    setAuthor( p.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Author",
    //$NON-NLS-1$
        p.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Author" ) ) ); //$NON-NLS-1$
    setKeywords( p.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Keywords", //$NON-NLS-1$
        p.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Keywords" ) ) ); //$NON-NLS-1$
    setDescription( p.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Description", //$NON-NLS-1$
        p.getConfigProperty( "org.pentaho.reporting.engine.classic.core.metadata.Description" ) ) ); //$NON-NLS-1$

    setStrictLayout( "true".equals( p.getConfigProperty( //$NON-NLS-1$
        "org.pentaho.reporting.engine.classic.core.modules.output.table.html.StrictLayout" ) ) ); //$NON-NLS-1$

    final String encoding =
        p.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.Encoding", //$NON-NLS-1$
            EncodingRegistry.getPlatformDefaultEncoding() );
    encodingModel.ensureEncodingAvailable( encoding );
    setEncoding( encoding );

    setDataFilename( p.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.gui.html.zip.DataDirectory", "" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    setFilename( p.getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.html.zip.TargetFileName",
        "" ) ); //$NON-NLS-1$ //$NON-NLS-2$

  }

  /**
   * Returns the directory data file name.
   *
   * @return The file name.
   */
  public String getDataFilename() {
    return txDataFilename.getText();
  }

  /**
   * Sets the directory data file name.
   *
   * @param dirFilename
   *          the file name.
   */
  public void setDataFilename( final String dirFilename ) {
    this.txDataFilename.setText( dirFilename );
  }

  /**
   * Returns the directory file name.
   *
   * @return The directory file name.
   */
  public String getFilename() {
    return txFilename.getText();
  }

  /**
   * Sets the directory file name.
   *
   * @param dirFilename
   *          the file name.
   */
  public void setFilename( final String dirFilename ) {
    this.txFilename.setText( dirFilename );
  }

  /**
   * Returns the setting of the 'strict layout' check-box.
   *
   * @return A boolean.
   */
  public boolean isStrictLayout() {
    return cbxStrictLayout.isSelected();
  }

  /**
   * Sets the 'strict layout' check-box.
   *
   * @param s
   *          boolean.
   */
  public void setStrictLayout( final boolean s ) {
    cbxStrictLayout.setSelected( s );
  }

  /**
   * Returns the selected encoding.
   *
   * @return The encoding name.
   */
  public String getEncoding() {
    if ( cbEncoding.getSelectedIndex() == -1 ) {
      return EncodingRegistry.getPlatformDefaultEncoding();
    } else {
      return encodingModel.getEncoding( cbEncoding.getSelectedIndex() );
    }
  }

  /**
   * Sets the encoding.
   *
   * @param encoding
   *          the encoding name.
   */
  public void setEncoding( final String encoding ) {
    cbEncoding.setSelectedIndex( encodingModel.indexOf( encoding ) );
  }

  /**
   * Selects a file to use as target for the report processing.
   */
  protected void performSelectFileZip() {
    final File file = new File( getFilename() );

    if ( fileChooserZip == null ) {
      fileChooserZip = new JFileChooser();
      fileChooserZip.addChoosableFileFilter( new FilesystemFilter( new String[] {
        HtmlZipExportDialog.ZIP_FILE_EXTENSION, HtmlZipExportDialog.JAR_FILE_EXTENSION }, getResources().getString(
          "htmlexportdialog.zip-archives" ), true ) ); //$NON-NLS-1$
      fileChooserZip.setMultiSelectionEnabled( false );
    }

    fileChooserZip.setCurrentDirectory( file );
    fileChooserZip.setSelectedFile( file );
    final int option = fileChooserZip.showSaveDialog( this );
    if ( option == JFileChooser.APPROVE_OPTION ) {
      final File selFile = fileChooserZip.getSelectedFile();
      String selFileName = selFile.getAbsolutePath();

      // Test if ends on xls
      if ( StringUtils.endsWithIgnoreCase( selFileName, HtmlZipExportDialog.ZIP_FILE_EXTENSION ) == false ) {
        selFileName = selFileName + HtmlZipExportDialog.ZIP_FILE_EXTENSION;
      }
      setFilename( selFileName );
    }
  }

  /**
   * Validates the contents of the dialog's input fields. If the selected file exists, it is also checked for validity.
   *
   * @return true, if the input is valid, false otherwise
   */
  public boolean performValidate() {
    getStatusBar().clear();

    final String filename = getFilename();
    if ( filename.trim().length() == 0 ) {
      getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "htmlexportdialog.targetIsEmpty" ) ); //$NON-NLS-1$
      return false;
    }
    final File f = new File( filename );
    if ( f.exists() ) {
      if ( f.isFile() == false ) {
        getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "htmlexportdialog.targetIsNoFile" ) ); //$NON-NLS-1$
        return false;
      }
      if ( f.canWrite() == false ) {
        getStatusBar().setStatus( StatusType.ERROR, getResources().getString( "htmlexportdialog.targetIsNotWritable" ) ); //$NON-NLS-1$
        return false;
      }

      final String message = MessageFormat.format( getResources().getString( "htmlexportdialog.targetExistsWarning" ), //$NON-NLS-1$
          new Object[] { filename } );
      getStatusBar().setStatus( StatusType.WARNING, message );
    }

    try {
      final File dataDir = new File( getDataFilename() );
      final File baseDir = new File( "" ); //$NON-NLS-1$

      if ( IOUtils.getInstance().isSubDirectory( baseDir, dataDir ) == false ) {
        getStatusBar()
            .setStatus( StatusType.ERROR, getResources().getString( "htmlexportdialog.targetPathIsAbsolute" ) ); //$NON-NLS-1$
        return false;
      }
    } catch ( Exception e ) {
      getStatusBar().setStatus( StatusType.ERROR, "error.validationfailed" ); //$NON-NLS-1$
      return false;
    }
    return true;
  }

  protected boolean performConfirm() {
    final String filename = getFilename();
    final File f = new File( filename ).getAbsoluteFile();
    if ( f.exists() ) {
      final String key1 = "htmlexportdialog.targetOverwriteConfirmation"; //$NON-NLS-1$
      final String key2 = "htmlexportdialog.targetOverwriteTitle"; //$NON-NLS-1$
      if ( JOptionPane.showConfirmDialog( this, MessageFormat.format( getResources().getString( key1 ),
          new Object[] { getFilename() } ), getResources().getString( key2 ), JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE ) == JOptionPane.NO_OPTION ) {
        return false;
      }
    }

    return true;
  }

  protected String getConfigurationSuffix() {
    return "_html-zip-export"; //$NON-NLS-1$
  }

  protected String getResourceBaseName() {
    return HtmlExportGUIModule.BASE_RESOURCE_CLASS;
  }

  protected String getConfigurationPrefix() {
    return "org.pentaho.reporting.engine.classic.core.modules.gui.html.zip."; //$NON-NLS-1$
  }
}

/**
 * MIT License
 *
 * Copyright (c) 2020 Mark Schmieder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the ConverterToolkit Library
 *
 * You should have received a copy of the MIT License along with the
 * FxConverterToolkit Library. If not, see
 * <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/convertertoolkit
 */
package com.mhschmieder.convertertoolkit.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.mhschmieder.convertertoolkit.eps.EpsExportUtilities;
import com.mhschmieder.convertertoolkit.pdf.PdfExportUtilities;
import com.mhschmieder.convertertoolkit.svg.SvgExportUtilities;
import com.mhschmieder.guitoolkit.component.XFrame;

/**
 * {@code ConverterDemoFrame} is an example of a Swing-based window that can
 * export to vector graphics.
 * <p>
 * All that is needed, is a menu system and file chooser, plus a custom dialog,
 * to query the title and File Name. Almost any existing Swing application will
 * already have this support.
 * <p>
 * This runnable sample frame is only supplied as an example of the minimal code
 * needed when not starting from scratch. The more common use case these days is
 * a legacy Swing panel hosted inside a JavaFX application, but to keep JavaFX
 * dependencies out of this library distribution (due to it being decoupled from
 * the JRE starting with Java 11), that sample code will be distributed in a
 * separate bundle.
 * <p>
 * This code is provided only as a example for adding this functionality to your
 * own application, and is not meant to serve as a mini-application for
 * converting GUI visuals to vector graphics; nor has it been tested fully. It
 * is the maximal subset of non-proprietary code in the author's commercial
 * application that could be safely extracted for public consumption.
 * <p>
 * Most importantly, this is not currently a fully working application; in test
 * runs, the output is blank except for the title header. Most exports will be
 * done on content other than GUI controls such as buttons; especially charts
 * and the like. This is just meant as an example of how to hook the Export
 * Vector Graphics call into an existing application, targeting the main panel,
 * or a preferred sub-panel, of an application window or frame. I do not have
 * the patience to write fully working from-scratch Swing applications anymore;
 * this library works fine within JavaFX applications that contain legacy Swing
 * panels and windows, or using JFXConverter to vectorize the JavaFX GUI layout
 * elements and graphics.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class ConverterDemoFrame extends XFrame {
    /**
     * Unique Serial Version ID for this class, to avoid class loader conflicts.
     */
    private static final long serialVersionUID = -6103053457616195619L;

    ////////////////// Main method for simple invocation /////////////////////

    /**
     * This method instantiates this class as the main frame for an
     * application. Note that it must ensure that the frame be instantiated on
     * the AWT Event Queue, which is the only safe thread for GUI rendering.
     *
     * @param args
     *            The command-line arguments for executing this class as the
     *            main entry point for an application
     *
     * @since 1.0
     */
    public static void main( final String[] args ) {
        // Set the Nimbus look and feel, as the most reliable across platforms.
        // If Nimbus is not available, stick with the default look and feel.
        //
        // http://download.oracle.com/genericse/tutorial/uiswing/lookandfeel/plaf.html
        try {
            for ( final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels() ) {
                if ( "Nimbus".equals( info.getName() ) ) { //$NON-NLS-1$
                    UIManager.setLookAndFeel( info.getClassName() );
                    break;
                }
            }
        }
        catch ( final Exception e ) {
            Logger.getLogger( ConverterDemoFrame.class.getName() ).log( Level.SEVERE, null, e );
        }

        // Create and display this frame as the main application window.
        EventQueue.invokeLater( () -> {
            final JFrame mainFrame =
                                   new ConverterDemoFrame( "Test the Vector Graphics Export Feature", //$NON-NLS-1$
                                                           true );
            mainFrame.setVisible( true );
            mainFrame.toFront();
        } );
    }

    /**
     * This {@link ConverterDemoPanel} mostly just contains the primary layout
     * element, apart from the menus and tool bars.
     */
    protected ConverterDemoPanel converterDemoPanel;

    //////////////////////////// Constructors ////////////////////////////////

    /**
     * This is the preferred constructor; it may be dangerous to call the other
     * superclass constructors as it could result in incomplete initialization.
     * <p>
     * The title in this case is the initial title, as this class manages the
     * dirty flag for the frame and modifies the frame title bar accordingly.
     *
     * @param title
     *            The initial title to use for the frame
     * @param resizable
     *            {@code true} if this frame is resizable by the user;
     *            {@code false} otherwise
     * @throws HeadlessException
     *             If {@code GraphicsEnvironment.isHeadless()} returns
     *             {@code true}
     */
    public ConverterDemoFrame( final String title, final boolean resizable )
            throws HeadlessException {
        // Always call the superclass constructor first!
        super( title, resizable );

        try {
            initFrame();
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    /////////////////////// Initialization methods ///////////////////////////

    /**
     * Initializes this frame in an encapsulated way that protects all
     * constructors from run-time exceptions that might prevent instantiation.
     *
     * @since 1.0
     */
    private void initFrame() {
        // Add quick-and-dirty buttons just to test the export feature.
        final JButton epsExportButton = new JButton( "Export to EPS" ); //$NON-NLS-1$
        final JButton pdfExportButton = new JButton( "Export to PDF" ); //$NON-NLS-1$
        final JButton svgExportButton = new JButton( "Export to SVG" ); //$NON-NLS-1$
        final JPanel exportPanel = new JPanel();
        exportPanel.setLayout( new BorderLayout( 10, 10 ) );
        exportPanel.add( epsExportButton, BorderLayout.WEST );
        exportPanel.add( pdfExportButton, BorderLayout.CENTER );
        exportPanel.add( svgExportButton, BorderLayout.EAST );

        // Make the primary layout element, and add it to the content pane.
        //
        // This code will be refactored once the XFrame class is completed, so
        // that this work is done in an override method rather than here.
        converterDemoPanel = new ConverterDemoPanel();
        final Container contentPane = getContentPane();
        contentPane.setLayout( new BorderLayout( 10, 10 ) );
        contentPane.add( exportPanel, BorderLayout.NORTH );
        contentPane.add( converterDemoPanel, BorderLayout.CENTER );

        // Add a listener for the Export EPS action button.
        epsExportButton.addActionListener( e -> {
            // This is a very basic example, as I exclusively do JavaFX at the
            // app-level by now, with only a few legacy panels still written in
            // Swing, so I no longer have working infrastructure for the old
            // ways of doing things (I use JavaFX for file actions in Swing).
            final JFileChooser fileChooser = new JFileChooser();
            final int returnValue = fileChooser.showSaveDialog( null );
            if ( returnValue == JFileChooser.APPROVE_OPTION ) {
                final File file = fileChooser.getSelectedFile();
                exportToEps( file, "Fake EPS Title" ); //$NON-NLS-1$
            }
        } );

        // Add a listener for the Export PDF action button.
        pdfExportButton.addActionListener( e -> {
            // This is a very basic example, as I exclusively do JavaFX at the
            // app-level by now, with only a few legacy panels still written in
            // Swing, so I no longer have working infrastructure for the old
            // ways of doing things (I use JavaFX for file actions in Swing).
            final JFileChooser fileChooser = new JFileChooser();
            final int returnValue = fileChooser.showSaveDialog( null );
            if ( returnValue == JFileChooser.APPROVE_OPTION ) {
                final File file = fileChooser.getSelectedFile();
                exportToPdf( file, "Fake PDF Title" ); //$NON-NLS-1$
            }
        } );

        // Add a listener for the Export SVG action button.
        svgExportButton.addActionListener( e -> {
            // This is a very basic example, as I exclusively do JavaFX at the
            // app-level by now, with only a few legacy panels still written in
            // Swing, so I no longer have working infrastructure for the old
            // ways of doing things (I use JavaFX for file actions in Swing).
            final JFileChooser fileChooser = new JFileChooser();
            final int returnValue = fileChooser.showSaveDialog( null );
            if ( returnValue == JFileChooser.APPROVE_OPTION ) {
                final File file = fileChooser.getSelectedFile();
                exportToSvg( file, "Fake SVG Title" ); //$NON-NLS-1$
            }
        } );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setSize( 420, 280 );
        setLocationRelativeTo( null );

        // Set the application to be friendly to paper-oriented output colors.
        setForegroundFromBackground( Color.WHITE );
    }

    //////////////////// Export Action handler methods ///////////////////////

    /**
     * This method exports the contents of the current {@link JFrame} to an
     * EPS Document written to the supplied {@link File}.
     *
     * @param file
     *            The File to use for the EPS Export
     * @param title
     *            The {@link String} to use as the EPS Document's title
     * @return {@code true} if the export succeeded; {@code false} if it failed
     *
     * @since 1.0
     */
    public boolean exportToEps( final File file, final String title ) {
        // Sync the EPS-exportable Panel's EPS Title with the new one.
        converterDemoPanel.setTitle( title );

        // Until we add a GUI field for creator, set this app as default author.
        final String creator = "Saved from ConverterDemoFrame"; //$NON-NLS-1$

        // Write the EPS contents indirectly to the supplied file via
        // paintComponent(), using RGB Mode, and vectorized text (especially so
        // that rotated text is rotated in EPS).
        //
        // In a real application, the page width and page height would probably
        // either be queried per export action, or grabbed from the current Page
        // Setup attributes. There are no limits on allowed values; units are
        // points (1/72 inch) and we default to North American Letter size here.
        final boolean fileSaved = EpsExportUtilities
                .createDocument( file, converterDemoPanel, title, creator );

        return fileSaved;
    }

    /**
     * This method exports the contents of the current {@link JFrame} to an
     * SVG Document written to the supplied {@link File}.
     *
     * @param file
     *            The File to use for the SVG Export
     * @param title
     *            The {@link String} to use as the SVG Document's title
     * @return {@code true} if the export succeeded; {@code false} if it failed
     *
     * @since 1.0
     */
    public boolean exportToSvg( final File file, final String title ) {
        // Sync the SVG-exportable Panel's SVG Title with the new one.
        converterDemoPanel.setTitle( title );

        // Write the SVG contents indirectly to the supplied file via
        // paintComponent, using RGB Mode, and vectorized text (especially so
        // that rotated text is rotated in SVG).
        //
        // In a real application, the page width and page height would probably
        // either be queried per export action, or grabbed from the current Page
        // Setup attributes. There are no limits on allowed values; units are
        // points (1/72 inch) and we default to North American Letter size here.
        final boolean fileSaved = SvgExportUtilities
                .createDocument( file, converterDemoPanel, title );

        return fileSaved;
    }

    /**
     * This method exports the contents of the current {@link JFrame} to a
     * PDF Document written to the supplied {@link File}.
     *
     * @param file
     *            The File to use for the PDF Export
     * @param title
     *            The {@link String} to use as the SVG Document's title
     * @return {@code true} if the export succeeded; {@code false} if it failed
     *
     * @since 1.0
     */
    public boolean exportToPdf( final File file, final String title ) {
        // Sync the PDF-exportable Panel's PDF Title with the new one.
        converterDemoPanel.setTitle( title );

        // Until we add a GUI field for author, set this app as default author.
        final String author = "Saved from ConverterDemoFrame"; //$NON-NLS-1$

        // Write the PDF contents indirectly to the supplied file via
        // paintComponent, using RGB Mode, and vectorized text (especially so
        // that rotated text is rotated in PDF).
        //
        // In a real application, the page width and page height would probably
        // either be queried per export action, or grabbed from the current Page
        // Setup attributes. There are no limits on allowed values; units are
        // points (1/72 inch) and we default to North American Letter size here.
        final boolean fileSaved = PdfExportUtilities
                .createDocument( file, converterDemoPanel, title, author );

        return fileSaved;
    }

    //////////////////////// XFrame method overrides /////////////////////////

    /**
     * This method sets the specified background color for the layout hierarchy,
     * and where appropriate, uses it to set an appropriate foreground color to
     * complement for text-based components as well as line graphics.
     *
     * @param backColor
     *            The current background color to apply to the layout hierarchy
     *
     * @since 1.0
     */
    @Override
    public void setForegroundFromBackground( final Color backColor ) {
        // Always call the superclass first!
        super.setForegroundFromBackground( backColor );

        // Forward this method to the subcomponents.
        converterDemoPanel.setForegroundFromBackground( backColor );
    }

}

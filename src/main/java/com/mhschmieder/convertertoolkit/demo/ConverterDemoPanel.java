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
 * ConverterToolkit Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/convertertoolkit
 */
package com.mhschmieder.convertertoolkit.demo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.mhschmieder.graphicstoolkit.color.ColorUtilities;
import com.mhschmieder.guitoolkit.component.TitledVectorizationXPanel;
import com.mhschmieder.guitoolkit.component.VectorSource;
import com.mhschmieder.guitoolkit.component.VectorizationPanel;

/**
 * {@code ConverterDemoPanel} is an example panel that shows how to use the
 * implementation methods that are contracted by the {@link VectorSource}
 * interface as implemented in the example {@link TitledVectorizationXPanel}
 * parent class provided here. It is a more detailed example that shows how to
 * composite multiple sub-panels into the same page layout and
 * {@link Graphics2D} derived Graphics Context, thus avoiding multi-threading.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class ConverterDemoPanel extends TitledVectorizationXPanel {
    /**
     * Unique Serial Version ID for this class, to avoid class loader conflicts.
     */
    private static final long    serialVersionUID = 1763104890145442760L;

    /**
     * The top panel is an example of simple content for a layout region that
     * needs to be individually composited onto the output's page layout.
     */
    protected VectorizationPanel topPanel;

    /**
     * This is the bottom parent container (layout panel) for the side-by-side
     * bottom left and bottom right content panels. It cannot be vectorized as
     * only its children have actual content. It supplies padding and spacing.
     */
    protected JPanel             bottomPanel;

    /**
     * The bottom left panel is an example of simple content for a layout region
     * that needs to be individually composited onto the output's page layout.
     */
    protected VectorizationPanel bottomLeftPanel;

    /**
     * The bottom right panel is an example of simple content for a layout
     * region that needs to be individually composited onto the output's page
     * layout.
     */
    protected VectorizationPanel bottomRightPanel;

    //////////////////////////// Constructors ////////////////////////////////

    /**
     * Default constructor.
     *
     * @since 1.0
     */
    public ConverterDemoPanel() {
        // Always call the superclass constructor first!
        super();

        // Avoid constructor failure by wrapping the layout initialization in an
        // exception handler that logs the exception and then returns an object.
        try {
            initPanel();
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    /////////////////////// Initialization methods ///////////////////////////

    /**
     * Initializes this panel in an encapsulated way that protects all
     * constructors from run-time exceptions that might prevent instantiation.
     *
     * @since 1.0
     */
    @SuppressWarnings("nls")
    private void initPanel() {
        // Make three labels to display in an asymmetric grid, as an example of
        // typical application panel layouts that will need to export to vector
        // graphics via compositing (that is, separate renderings per panel).
        final JLabel goodbyeLabel = new JLabel( "Goodbye" );
        goodbyeLabel.setFont( new Font( "SansSerif", Font.BOLD, 48 ) );
        final JLabel cruelLabel = new JLabel( "Cruel" );
        cruelLabel.setFont( new Font( "SansSerif", Font.ITALIC, 36 ) );
        final JLabel worldLabel = new JLabel( "World" );
        worldLabel.setFont( new Font( "SansSerif", Font.ITALIC, 36 ) );

        // Make three check boxes to display in an asymmetric grid, as an
        // example of typical application panel layouts that will need to export
        // to vector graphics via compositing (that is, several passes in order
        // to accomplish separate renderings per sub-panel or layout region).
        final JCheckBox maybeCheckBox = new JCheckBox( "Maybe" );
        final JCheckBox yesCheckBox = new JCheckBox( "Yes" );
        final JCheckBox noCheckBox = new JCheckBox( "No" );

        // Line up the "Goodbye" label and the "Maybe" check box top-to-bottom
        // in the top panel using a Box Layout.
        topPanel = new VectorizationPanel();
        topPanel.setLayout( new BoxLayout( topPanel, BoxLayout.PAGE_AXIS ) );
        topPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        topPanel.add( goodbyeLabel );
        topPanel.add( maybeCheckBox );

        // Line up the "Cruel" label and the "Yes" check box top-to-bottom in
        // the bottom left panel using a Box Layout.
        bottomLeftPanel = new VectorizationPanel();
        bottomLeftPanel.setLayout( new BoxLayout( bottomLeftPanel, BoxLayout.PAGE_AXIS ) );
        bottomLeftPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        bottomLeftPanel.add( cruelLabel );
        bottomLeftPanel.add( yesCheckBox );

        // Line up the "World" label and the "No" check box top-to-bottom in the
        // bottom right panel using a Box Layout.
        bottomRightPanel = new VectorizationPanel();
        bottomRightPanel.setLayout( new BoxLayout( bottomRightPanel, BoxLayout.PAGE_AXIS ) );
        bottomRightPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        bottomRightPanel.add( worldLabel );
        bottomRightPanel.add( noCheckBox );

        // Line up the bottom left and bottom right panels side-by-side in the
        // bottom panel container using a Box Layout.
        bottomPanel = new JPanel();
        bottomPanel.setLayout( new BoxLayout( bottomPanel, BoxLayout.LINE_AXIS ) );
        bottomPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        bottomPanel.add( bottomLeftPanel );
        bottomPanel.add( bottomRightPanel );

        // Stack the top and bottom panels vertically, so that we thoroughly
        // exemplify a typical application's asymmetric layout and can thus use
        // this sample panel to verify the vector graphics output as having
        // composited correctly, so that the page layout matches the screen
        // layout (aside from different preferences for spacing due to the North
        // American Letter page having a different aspect ratio from a typical
        // Swing application window on the screen).
        setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
        setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        add( topPanel );
        add( bottomPanel );
    }

    ////////////// TitledVectorizationXPanel method overrides ////////////////

    /**
     * Export this panel to vector graphics, using the provided export options.
     * <p>
     * Note that this is an override implementation, which avoids multiple
     * titles due to there being multiple sub-panels to export.
     * <p>
     * Ideally, we would take a less hacky approach to the incremental page
     * offsets during output compositing; there may be some better examples to
     * draw from in a future release, from some other export projects we have
     * worked on. Some compositors handle this with two separate "cursors".
     *
     * @param graphicsContext
     *            The {@link Graphics2D} Graphics Context for vectorizing the
     *            content of this panel
     * @return {@code true} if the export succeeded; {@code false} if it failed
     *
     * @since 1.0
     */
    @Override
    public boolean vectorize( final Graphics2D graphicsContext ) {
        // Default to "not exported" status, in case of unrecoverable errors.
        boolean panelExported = false;

        // Cache the current background color.
        final Color background = getBackground();

        // Note that we set the background to white, as otherwise we would need
        // to vectorize with line width doubled due to downstream resolution (it
        // is often difficult to see lines plotted against a black background).
        setForegroundFromBackground( Color.WHITE );

        // First take care of any title or header graphics common to all panels.
        super.vectorize( graphicsContext );

        // Save the top panel to the file, knowing that the title's height has
        // already been accounted for in repositioning the content.
        panelExported = topPanel.vectorize( graphicsContext );
        if ( panelExported ) {
            // Account for all of the vertical offsets so far, to determine the
            // page positioning of the side-by-side bottom panels below the top
            // panel.
            final int titleOffsetY = getTitleOffsetY();
            final int titleAdjustmentY = titleOffsetY + TITLE_PADDING_BOTTOM;
            final int topPanelHeight = topPanel.getHeight();
            graphicsContext.translate( 0, topPanelHeight + titleAdjustmentY + 20 );
            panelExported = bottomLeftPanel.vectorize( graphicsContext );
            if ( panelExported ) {
                // Adjust the position of the bottom right panel's export on the
                // page by the width of the just-written bottom left panel, and
                // then readjust afterwards so that any additional panels or
                // layout container hierarchies left-align below the bottom
                // panel's layout row.
                final int bottomLeftPanelWidth = bottomLeftPanel.getWidth();
                graphicsContext.translate( bottomLeftPanelWidth, 0 );
                panelExported = bottomRightPanel.vectorize( graphicsContext );
                graphicsContext.translate( -bottomLeftPanelWidth, 0 );
            }
        }

        // Restore the current background color.
        setForegroundFromBackground( background );

        return panelExported;
    }

    //////////////////////// XPanel method overrides /////////////////////////

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

        // Make sure the foreground color is never masked by the background.
        final Color foreColor = ColorUtilities.getForegroundFromBackground( backColor );

        // Forward this method to the subcomponents.
        topPanel.setBackground( backColor );
        topPanel.setForeground( foreColor );

        bottomPanel.setBackground( backColor );
        bottomPanel.setForeground( foreColor );

        bottomLeftPanel.setBackground( backColor );
        bottomLeftPanel.setForeground( foreColor );

        bottomRightPanel.setBackground( backColor );
        bottomRightPanel.setForeground( foreColor );

        // Any change to background requires regenerating the off-screen buffer.
        regenerateOffScreenImage = true;
    }

}

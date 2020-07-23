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
package com.mhschmieder.convertertoolkit.eps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.mhschmieder.epstoolkit.EpsDocument;
import com.mhschmieder.epstoolkit.graphics.EpsGraphics2D;
import com.mhschmieder.epstoolkit.graphics.EpsRenderingHints;
import com.mhschmieder.graphicstoolkit.color.ColorMode;
import com.mhschmieder.graphicstoolkit.print.PaperConstants;
import com.mhschmieder.guitoolkit.component.VectorSource;

/**
 * {@code EpsExportUtilities} is a utility class for methods that export a
 * {@link VectorSource} implementing component or object to an EPS Document
 * using a custom EPS-specific wrapper for AWT's {@code Graphics2d} class.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class EpsExportUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private EpsExportUtilities() {}

    /**
     * Creates the EPS Document corresponding to the {@link VectorSource}, and
     * writes it to the provided {@link File}, using UTF-8 encoding due to the
     * need to handle locale sensitive characters for the EPS Title and content.
     * <p>
     * This minimally specified entry point defaults to North American Letter
     * as the target page paper size and orientation, for clients that don't
     * want to bother specifying it or querying it either from the user or the
     * current Page Layout. It also presumes that RGB output is wanted, and
     * vectorized text (especially so that rotated text is rotated in EPS).
     *
     * @param file
     *            The {@link File} destination for writing the EPS content
     * @param component
     *            The {@link VectorSource} AWT/Swing component to export to EPS
     * @param title
     *            The {@link String} to use as the EPS Document's title
     * @param creator
     *            The {@link String} to use as the EPS Document's creator
     * @return The status of whether EPS Document creation succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final File file,
                                          final VectorSource component,
                                          final String title,
                                          final String creator ) {
        // Use North American Letter as the target page paper size and page
        // orientation. There are no limits on allowed values; units are points
        // (1/72 inch) but it is common to specify Letter Size and then convert.
        final double pageWidth = PaperConstants.NA_LETTER_WIDTH_POINTS;
        final double pageHeight = PaperConstants.NA_LETTER_HEIGHT_POINTS;
        final boolean fileSaved = createDocument( file,
                                                  component,
                                                  title,
                                                  creator,
                                                  pageWidth,
                                                  pageHeight,
                                                  ColorMode.RGB,
                                                  true );

        return fileSaved;
    }

    /**
     * Creates the EPS Document corresponding to the {@link VectorSource}, and
     * writes it to the provided {@link File}, using UTF-8 encoding due to the
     * need to handle locale sensitive characters for the EPS Title and content,
     * as well as meeting the restriction that PostScript does not support the
     * Java default of UTF-16. Thus we customize an {@link OutputStreamWriter}.
     *
     * @param file
     *            The {@link File} destination for writing the EPS content
     * @param component
     *            The {@link VectorSource} AWT/Swing component to export to EPS
     * @param title
     *            The {@link String} to use as the EPS Document's title
     * @param creator
     *            The {@link String} to use as the EPS Document's creator
     * @param pageWidth
     *            The target page width, in points (1/72 inch)
     * @param pageHeight
     *            The target page height, in points (1/72 inch)
     * @param colorMode
     *            The {@link ColorMode} to use, compatible with EPS specs
     * @param useVectorizedText
     *            Set to {@code true} if Vectorized Text Mode is desired;
     *            {@code false} otherwise (that is, if text is to be rendered as
     *            strings, sometimes referred to as Basic Text Mode)
     * @return The status of whether EPS Document creation succeeded or not
     *
     * @since 1.0
     */
    @SuppressWarnings("nls")
    public static boolean createDocument( final File file,
                                          final VectorSource component,
                                          final String title,
                                          final String creator,
                                          final double pageWidth,
                                          final double pageHeight,
                                          final ColorMode colorMode,
                                          final boolean useVectorizedText ) {
        boolean fileSaved = false;

        // Using a safe try-with-resources clause, chain a BufferedWriter to an
        // OutputStreamWriter to a FileOutputStream using UTF-8, for better
        // performance and to guarantee platform-independence of newlines and
        // overall system-neutrality and locale-sensitivity of text data. As EPS
        // does not support UTF-16, which is the default character set for Java,
        // we must use {@link OutputStreamWriter} instead of {@link FileWriter}.
        try ( final FileOutputStream fileOutputStream = new FileOutputStream( file );
                final OutputStreamWriter outputStreamWriter =
                                                            new OutputStreamWriter( fileOutputStream,
                                                                                    "UTF-8" );
                final BufferedWriter bufferedWriter = new BufferedWriter( outputStreamWriter ) ) {
            // Write the EPS contents indirectly via Swing's paintComponent().
            fileSaved = createDocument( bufferedWriter,
                                        component,
                                        title,
                                        creator,
                                        pageWidth,
                                        pageHeight,
                                        colorMode,
                                        useVectorizedText );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return fileSaved;
    }

    /**
     * Creates the EPS Document corresponding to the {@link VectorSource}, and
     * writes it to the provided {@link Writer} (usually a Character Stream
     * based Output Writer) using EpsToolkit.
     *
     * @param writer
     *            The wrapped {@link Writer} for channeling the EPS content
     * @param component
     *            The {@link VectorSource} AWT/Swing component to export to EPS
     * @param title
     *            The {@link String} to use as the EPS Document's title
     * @param creator
     *            The {@link String} to use as the EPS Document's creator
     * @param pageWidth
     *            The target page width, in points (1/72 inch)
     * @param pageHeight
     *            The target page height, in points (1/72 inch)
     * @param colorMode
     *            The {@link ColorMode} to use, compatible with EPS specs
     * @param useVectorizedText
     *            Set to {@code true} if Vectorized Text Mode is desired;
     *            {@code false} otherwise (that is, if text is to be rendered as
     *            strings, sometimes referred to as Basic Text Mode)
     * @return The status of whether EPS Document creation succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final Writer writer,
                                          final VectorSource component,
                                          final String title,
                                          final String creator,
                                          final double pageWidth,
                                          final double pageHeight,
                                          final ColorMode colorMode,
                                          final boolean useVectorizedText ) {
        boolean fileSaved = false;

        final double minX = component.getVectorSourceMinX();
        final double minY = component.getVectorSourceMinY();
        final double maxX = component.getVectorSourceMaxX();
        final double maxY = component.getVectorSourceMaxY();

        // Create a new EPS Document.
        //
        // The global translation and scale factors are calculated by the
        // EpsGraphics2D implementation as fonts display upside-down if we
        // instead set the transform on the Graphics Context to use on all
        // subsequent graphics operations. This unfortunately introduces a
        // slight inconsistency vs. how PDF and SVG are handled for export.
        final EpsDocument epsDocument = new EpsDocument();

        // Get the Graphics Context wrapper for drawing the EPS content.
        final EpsGraphics2D epsGraphics = epsDocument.getGraphics2D();

        // Apply the Color Mode, in case it is something other than RGB.
        epsGraphics.setColorMode( colorMode );

        // Vectorize the text, to avoid missing fonts and to allow more
        // flexibility in how to work with text in downstream applications.
        final Object textRenderingHint = useVectorizedText
            ? EpsRenderingHints.VALUE_TEXT_RENDERING_MODE_VECTOR
            : EpsRenderingHints.VALUE_TEXT_RENDERING_MODE_TEXT;
        epsGraphics.setRenderingHint( EpsRenderingHints.KEY_TEXT_RENDERING_MODE,
                                      textRenderingHint );

        try {
            // Write the EPS contents indirectly via Swing's paintComponent().
            fileSaved = component.vectorize( epsGraphics );

            // Get the full EPS Document (header, content, footer, etc.).
            final String epsContent = epsDocument.getEpsDocument( title,
                                                                  creator,
                                                                  ( float ) pageWidth,
                                                                  ( float ) pageHeight,
                                                                  ( float ) minX,
                                                                  ( float ) minY,
                                                                  ( float ) maxX,
                                                                  ( float ) maxY );

            // Save the EPS Document from memory to disc.
            writer.write( epsContent );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return fileSaved;
    }

}

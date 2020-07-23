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
package com.mhschmieder.convertertoolkit.pdf;

import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.mhschmieder.graphicstoolkit.color.ColorMode;
import com.mhschmieder.graphicstoolkit.graphics.GraphicsUtilities;
import com.mhschmieder.graphicstoolkit.print.PaperConstants;
import com.mhschmieder.guitoolkit.component.VectorSource;
import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.PDFHints;
import com.orsonpdf.Page;

/**
 * {@code PdfExportUtilities} is a utility class for methods that export a
 * {@link VectorSource} implementing component or object to a PDF Document
 * using a custom PDF-specific wrapper for AWT's {@code Graphics2d} class.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public class PdfExportUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private PdfExportUtilities() {}

    /**
     * Creates the PDF Document corresponding to the {@link VectorSource}, and
     * writes it to the provided {@link File}, using UTF-16 encoding due to the
     * need to handle locale sensitive characters for the PDF Title and content.
     * <p>
     * This minimally specified entry point defaults to North American Letter
     * as the target page paper size and orientation, for clients that don't
     * want to bother specifying it or querying it either from the user or the
     * current Page Layout. It also presumes that RGB output is wanted, and
     * vectorized text (especially so that rotated text is rotated in PDF).
     *
     * @param file
     *            The {@link File} destination for writing the PDF content
     * @param component
     *            The {@link VectorSource} AWT/Swing component to export to PDF
     * @param title
     *            The {@link String} to use as the PDF Document's title
     * @param author
     *            The {@link String} to use as the PDF Document's author
     * @return The status of whether PDF Document creation succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final File file,
                                          final VectorSource component,
                                          final String title,
                                          final String author ) {
        // Use North American Letter as the target page paper size and paper
        // orientation. There are no limits on allowed values; units are points
        // (1/72 inch) but it is common to specify Letter Size and then convert.
        final double pageWidth = PaperConstants.NA_LETTER_WIDTH_POINTS;
        final double pageHeight = PaperConstants.NA_LETTER_HEIGHT_POINTS;
        final boolean fileSaved = createDocument( file,
                                                  component,
                                                  title,
                                                  author,
                                                  pageWidth,
                                                  pageHeight,
                                                  ColorMode.RGB,
                                                  true );

        return fileSaved;
    }

    /**
     * Creates the PDF Document corresponding to the {@link VectorSource}, and
     * writes it to the provided {@link File}, using UTF-16 encoding due to the
     * need to handle locale sensitive characters for the PDF Title and content.
     * <p>
     * Note that the Color Mode isn't used yet, until OrsonPDF supports it.
     *
     * @param file
     *            The {@link File} destination for writing the PDF content
     * @param component
     *            The {@link VectorSource} AWT/Swing component to export to PDF
     * @param title
     *            The {@link String} to use as the PDF Document's title
     * @param author
     *            The {@link String} to use as the PDF Document's author
     * @param pageWidth
     *            The target page width, in points (1/72 inch)
     * @param pageHeight
     *            The target page height, in points (1/72 inch)
     * @param colorMode
     *            The {@link ColorMode} to use, compatible with PDF specs
     * @param useVectorizedText
     *            Set to {@code true} if Vectorized Text Mode is desired;
     *            {@code false} otherwise (that is, if text is to be rendered as
     *            strings, sometimes referred to as Basic Text Mode)
     * @return The status of whether PDF Document creation succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final File file,
                                          final VectorSource component,
                                          final String title,
                                          final String author,
                                          final double pageWidth,
                                          final double pageHeight,
                                          final ColorMode colorMode,
                                          final boolean useVectorizedText ) {
        boolean fileSaved = false;

        // Using a safe try-with-resources clause, chain a BufferedOutputStream
        // to a FileOutputStream using the former output wrapper's default
        // UTF-16 encoding (which also matches PDF's default), for better
        // performance and to guarantee platform-independence of newlines and
        // overall system-neutrality and locale-sensitivity of text data. As
        // OrsonPDF returns the entire file contents as a byte array, we must
        // use {@link BufferedOutputStream} instead of {@link FileWriter}.
        try ( final FileOutputStream fileOutputStream = new FileOutputStream( file );
                final BufferedOutputStream bufferedOutputStream =
                                                                new BufferedOutputStream( fileOutputStream ) ) {
            // Write the PDF contents indirectly via Swing's paintComponent().
            fileSaved = createDocument( bufferedOutputStream,
                                        component,
                                        title,
                                        author,
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
     * Creates the PDF Document corresponding to the {@link VectorSource}, and
     * writes it to the provided {@link OutputStream} using OrsonPDF.
     * <p>
     * Note that the Color Mode isn't used yet, until OrsonPDF supports it.
     *
     * @param outputStream
     *            The wrapped {@link OutputStream} for channeling the PDF
     *            content
     * @param component
     *            The {@link VectorSource} AWT/Swing component to export to PDF
     * @param title
     *            The {@link String} to use as the PDF Document's title
     * @param author
     *            The {@link String} to use as the PDF Document's author
     * @param pageWidth
     *            The target page width, in points (1/72 inch)
     * @param pageHeight
     *            The target page height, in points (1/72 inch)
     * @param colorMode
     *            The {@link ColorMode} to use, compatible with PDF specs
     * @param useVectorizedText
     *            Set to {@code true} if Vectorized Text Mode is desired;
     *            {@code false} otherwise (that is, if text is to be rendered as
     *            strings, sometimes referred to as Basic Text Mode)
     * @return The status of whether PDF Document creation succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final OutputStream outputStream,
                                          final VectorSource component,
                                          final String title,
                                          final String author,
                                          final double pageWidth,
                                          final double pageHeight,
                                          final ColorMode colorMode,
                                          final boolean useVectorizedText ) {
        boolean fileSaved = false;

        final double minX = component.getVectorSourceMinX();
        final double minY = component.getVectorSourceMinY();
        final double maxX = component.getVectorSourceMaxX();
        final double maxY = component.getVectorSourceMaxY();

        // Create a new PDF Document.
        final PDFDocument document = new PDFDocument();

        // Write the file title for the PDF Document (null permitted).
        document.setTitle( title );

        // Write the file author to the PDF Document (null permitted).
        document.setAuthor( author );

        // Create a new PDF Page, and add it to the PDF Document.
        final Page page = document
                .createPage( new Rectangle2D.Double( 0d, 0d, pageWidth, pageHeight ) );

        // Get the Graphics Context wrapper for drawing the PDF content.
        final PDFGraphics2D pdfGraphics = page.getGraphics2D();

        // Vectorize the text, to avoid missing fonts and to allow more
        // flexibility in how to work with text in downstream applications.
        final Object textRenderingHint = useVectorizedText
            ? PDFHints.VALUE_DRAW_STRING_TYPE_VECTOR
            : PDFHints.VALUE_DRAW_STRING_TYPE_STANDARD;
        pdfGraphics.setRenderingHint( PDFHints.KEY_DRAW_STRING_TYPE, textRenderingHint );

        // Calculate and apply a global transform for all of the AWT transcoding
        // from source coordinates to PDF-oriented page coordinates.
        //
        // PDF starts at the bottom left corner, like EPS and PostScript.
        GraphicsUtilities.applySourceToDestinationTransform( pdfGraphics,
                                                             minX,
                                                             minY,
                                                             maxX,
                                                             maxY,
                                                             pageWidth,
                                                             pageHeight );

        try {
            // Write the PDF contents indirectly via Swing's paintComponent().
            fileSaved = component.vectorize( pdfGraphics );

            // Get the full PDF Document as an encoded byte array.
            final byte[] pdfBytes = document.getPDFBytes();

            // Save the PDF Document from memory to disc.
            outputStream.write( pdfBytes );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return fileSaved;
    }

}

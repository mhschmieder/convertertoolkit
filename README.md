# ConverterToolkit
ConverterToolkit is a toolkit for converting AWT based graphics and GUI elements to various vector graphics output formats such as EPS, SVG and PDF.

This library may have to be split into separate ones for each output format, depending on whether I discover licensing discrepancies between them.

For now, I am hoping they can all be bundled together in one library, and then when I do the modularized version for Java 11 and beyond, that may be the best time for encapsulating the different JAR dependencies such that people only need to pull in the format(s) they care about.

Note that a separate library will be created for JavaFX output to these same vector graphics formats, as JavaFX itself has been decoupled from the main Java distribution with current versions of Java (Java 14+) and not everyone needs that support. Also, it uses another third-party JAR with its own licensing model.

This is a placeholder for now, as the dependency libraries need to be posted first, and they still have some minor details to wrap up, including a final decision on which licensing model to use.

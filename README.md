# ConverterToolkit
ConverterToolkit is a toolkit for converting AWT based graphics and GUI elements to various vector graphics output formats such as EPS, SVG and PDF.

In order to avoid bloat of too many small wrapper libraries, I decided to combine all the formats into one wrapper library for now, but another possibility is to break out the demo app as a separate library and have the converter wrapper part specify the three support libraries for EPS, SVG and PDF as optional dependencies, so that downstream clients can only include the formats they decide to support. The demo is just an example of combining all three into consistent app handling. This may have to be revisited anyway when I do the modularized version for Java 9 and beyond.

There is a separate library for JavaFX applications to output to these same vector graphics formats, as JavaFX itself has been decoupled from the main Java distribution with current versions of Java (Java 14+) and not everyone needs that support. Also, it uses another third-party JAR with its own licensing model.

Eclipse and NetBeans related support files are included as they are generic and are agnostic to the OS or to the user's system details and file system structure, so it seems helpful to post them in order to accelerate the integration of this library into a user's normal IDE project workflow and build cycle.

The Javadocs are 100% compliant and complete, but I am still learning how to publish those at the hosting site that I think is part of Maven Central, as it is a bad idea to bloat a GitHub project with such files and to complicate repository changes (just as with binary files and archices). Hopefully later tonight!

This projects depends on my GraphicsToolkit, GuiToolkit, and EpsToolkit libraries, as well as depending on Object Refinery's JFreeSVG and Orson PDF libraries, and is marked as such in the Maven POM file.

Please note that for now my forks must be used for both external dependencies, as I had to modify the POM and the Eclipse classpath file in order to specify Java 1.8 vs. Java 11 (JFreeSVG) or Java 1.6 (OrsonPDF), switch to a compatible Maven plug-in version in a couple of items in the POM, and correct some inconsistencies in the Group ID and Artifact ID.

As Object Refinery only migrated to GitHub recently (just as I am new to it, after years of Subversion), they do not yet have consistent versions posted for free vs. paid, so I may instead have to pre-build the JAR's for those libraries in order to match the correct revisions with the compatible Java 1.8 compiler in order to simplify what clients do when they download this converter library in its current Java 1.8 compliant version.

package ch.deletescape.markdown.doclet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.RootDoc;

import ch.deletescape.markdown.MDBuilder;
import ch.deletescape.markdown.MDBuilder.TextStyle;

public class MDDoclet extends Doclet {
  private static final String DEFAULT_EXTENSION = ".md";
  private static final String DEFAULT_TITLE = "MDDoc";
  private static Path outDir;
  private static boolean flat;
  private static String extension;
  private static String title;

  public static boolean start(RootDoc root) {
    MDBuilder indexBuilder = new MDBuilder();
    if (outDir == null) {
      outDir = Paths.get("doc");
    }
    if (extension == null) {
      extension = DEFAULT_EXTENSION;
    } else {
      Util.println("Using custom file extension \"" + extension + "\"...");
    }
    if (title == null) {
      title = DEFAULT_TITLE;
    }
    indexBuilder.header(1).text(title, true).newList();
    boolean first = true;
    for (ClassDoc classDoc : root.classes()) {
      MDBuilder builder = new MDBuilder();
      if (!first) {
        indexBuilder.listItem();
      }
      header(classDoc, builder);
      builder.text(Util.codeAndLinkParse(classDoc.commentText()), true);
      MethodMDDoc.methodSummary(builder, classDoc.methods());
      MethodMDDoc.methodDetail(builder, classDoc.methods());
      String filename = filenameFromType(classDoc);
      writeToFile(filename, builder.get());
      indexBuilder.text("[").text(classDoc.simpleTypeName()).text("](").text(filename).text(")", true);
      indexBuilder.text("   ").text(Util.codeAndLinkParse(classDoc.commentText()));
      first = false;
    }
    indexBuilder.endList();
    writeToFile("index" + extension, indexBuilder.get());
    return true;
  }

  public static boolean validOptions(String options[][], DocErrorReporter reporter) {
    for (String[] opt : options) {
      switch (opt[0]) {
        case "-d":
          outDir = Paths.get(opt[1]);
          break;
        case "-flat":
          flat = true;
          break;
        case "-quiet":
          Util.setQuiet(true);
          break;
        case "-extension":
          extension = opt[1];
          break;
        case "-doctitle":
          title = opt[1];
          break;
      }
    }
    return true;
  }

  public static int optionLength(String option) {
    switch (option) {
      case "-d":
      case "-extension":
      case "-doctitle":
        // Some standard doclet options are accepted, but ignored
      case "-windowtitle":
        return 2;
      case "-flat":
      case "-quiet":
        return 1;
      default:
        return -1;
    }
  }

  private static String filenameFromType(ClassDoc classDoc) {
    if (flat) {
      return classDoc.typeName();
    }
    return classDoc.qualifiedTypeName().replace('.', '/') + extension;
  }

  private static void header(ClassDoc classDoc, MDBuilder builder) {
    builder.header(1).text("Class ").text(classDoc.typeName(), true);
    builder.text("Package ").text(classDoc.containingPackage().name(), TextStyle.CODE, true);
    builder.text(classDoc.modifiers() + " class " + classDoc.typeName(), TextStyle.CODE).softWrap();
    builder.text("extends " + classDoc.superclassType().toString(), TextStyle.CODE, true);
  }

  private static void writeToFile(String filename, String text) {
    try {
      Path path = outDir.resolve(filename);
      Files.createDirectories(path.getParent());
      Files.deleteIfExists(path);
      Util.println("Writing to " + path.toString() + "...");
      try (BufferedWriter bw = Files.newBufferedWriter(path)) {
        bw.write(text);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

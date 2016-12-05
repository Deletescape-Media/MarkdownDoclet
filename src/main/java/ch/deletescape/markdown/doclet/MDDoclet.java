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
  private static final String FILE_EXTENSION = ".md";
  private static Path outDir;

  public static boolean start(RootDoc root) {
    if (outDir == null) {
      outDir = Paths.get("doc");
    }
    for (ClassDoc classDoc : root.classes()) {
      MDBuilder builder = new MDBuilder();
      header(classDoc, builder);
      MethodMDDoc.methodSummary(builder, classDoc.methods());
      MethodMDDoc.methodDetail(builder, classDoc.methods());
      String filename = classDoc.qualifiedTypeName().replace('.', '/');
      writeToFile(filename, builder.get());
    }
    return true;
  }

  public static boolean validOptions(String options[][], DocErrorReporter reporter) {
    for (String[] opt : options) {
      if ("-d".equals(opt[0])) {
        outDir = Paths.get(opt[1]);
      }
    }
    return true;
  }

  public static int optionLength(String option) {
    switch (option) {
      case "-d":
        return 2;
      default:
        return -1;
    }
  }

  private static void header(ClassDoc classDoc, MDBuilder builder) {
    builder.header(1).text("Class ").text(classDoc.typeName(), true);
    builder.text("Package ").text(classDoc.containingPackage().name(), TextStyle.CODE, true);
    builder.text(classDoc.modifiers() + " class " + classDoc.typeName(), TextStyle.CODE).softWrap();
    builder.text("extends " + classDoc.superclassType().toString(), TextStyle.CODE, true);
  }

  private static void writeToFile(String filename, String text) {
    try {
      Path path = outDir.resolve(filename + FILE_EXTENSION);
      Files.createDirectories(path.getParent());
      Files.deleteIfExists(path);
      try (BufferedWriter bw = Files.newBufferedWriter(path)) {
        bw.write(text);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

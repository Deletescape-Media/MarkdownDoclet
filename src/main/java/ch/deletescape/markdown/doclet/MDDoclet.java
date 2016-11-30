package ch.deletescape.markdown.doclet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;

import ch.deletescape.markdown.MDBuilder;
import ch.deletescape.markdown.MDBuilder.TextStyle;

public class MDDoclet extends Doclet {
  public static boolean start(RootDoc root) {
    MDBuilder builder = new MDBuilder();
    builder.header(1).text("Javadoc", true);
    for (ClassDoc classDoc : root.classes()) {
      builder.header(2).text("Class ").text(classDoc.typeName(), true);
      builder.text("Package ").text(classDoc.containingPackage().name(), TextStyle.CODE, true);
      builder.header(3).text("Method Summary", TextStyle.ITALIC, true);
      methodSummary(builder, classDoc);
    }
    System.out.println(builder.get());
    return true;
  }

  private static void methodSummary(MDBuilder builder, ClassDoc classDoc) {
    for (MethodDoc methodDoc : classDoc.methods()) {
      builder.header(4).text(methodDoc.modifiers(), TextStyle.ITALIC).text(" ");
      methodSummaryMethod(builder, methodDoc);
      builder.text(codeAndLinkParse(methodDoc.commentText()), true);
    }
  }

  private static void methodSummaryMethod(MDBuilder builder, MethodDoc methodDoc) {
    builder.text(methodDoc.name()).text("(");
    Parameter[] parameters = methodDoc.parameters();
    for (int i = 0; i < parameters.length; i++) {
      builder.text(parameters[i].typeName()).text(" ").text(parameters[i].name());
      if (i < parameters.length - 1) {
        builder.text(", ");
      }
    }
    builder.text(")", true);
  }

  private static String codeAndLinkParse(String text) {
    String tmp = text;
    tmp = tmp.replaceAll("\\{@code (.*?)\\}", "`$1`");
    tmp = tmp.replaceAll("\\{@link (.*?)#(\\w*?)\\((.*)\\)\\}", "[$1.$2($3)]($1.md#$2)");
    tmp = tmp.replaceAll("\\{@link (.*?)\\}", "[$1]($1.md)");
    return tmp;
  }
}

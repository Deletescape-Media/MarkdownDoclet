package ch.deletescape.markdown.doclet;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;

import ch.deletescape.markdown.MDBuilder;
import ch.deletescape.markdown.MDBuilder.TextStyle;

public class MDDoclet extends Doclet {
  public static boolean start(RootDoc root) {
    for (ClassDoc classDoc : root.classes()) {
      MDBuilder builder = new MDBuilder();
      builder.header(1).text("Class ").text(classDoc.typeName(), true);
      builder.text("Package ").text(classDoc.containingPackage().name(), TextStyle.CODE, true);
      builder.text(classDoc.modifiers() + " class " + classDoc.typeName(), TextStyle.CODE).softWrap();
      builder.text("extends " + classDoc.superclassType().toString(), TextStyle.CODE, true);
      builder.header(2).text("Method Summary", TextStyle.ITALIC, true);
      methodSummary(builder, classDoc);
      methodDetail(builder, classDoc);
      System.out.println(builder.get());
      try {
        Files.deleteIfExists(Paths.get(classDoc.name() + ".md"));
        try (FileWriter fw = new FileWriter(classDoc.name() + ".md")) {
          fw.write(builder.get());
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return true;
  }

  private static void methodSummary(MDBuilder builder, ClassDoc classDoc) {
    for (MethodDoc methodDoc : classDoc.methods()) {
      builder.header(3).text(methodDoc.modifiers()).text(" ");
      methodSignature(builder, methodDoc);
      builder.text(codeAndLinkParse(methodDoc.commentText()), true);
    }
  }

  private static void methodDetail(MDBuilder builder, ClassDoc classDoc) {
    builder.header(2).text("Method Detail", TextStyle.ITALIC, true);
    for (MethodDoc methodDoc : classDoc.methods()) {
      builder.header(3).text(methodDoc.modifiers(), TextStyle.ITALIC).text(" ");
      methodSignature(builder, methodDoc);
      builder.text(codeAndLinkParse(methodDoc.commentText()), true);
      methodDetailParameterList(builder, methodDoc);
      methodDetailThrowsTags(builder, methodDoc);
      methodDetailReturnTags(builder, methodDoc);
      methodDetailSeeTags(builder, methodDoc);
    }
  }

  private static void methodDetailParameterList(MDBuilder builder, MethodDoc methodDoc) {
    ParamTag[] params = methodDoc.paramTags();
    for (int i = 0; i < params.length; i++) {
      if (i == 0) {
        builder.header(4).text("Parameters:", true);
        builder.newList();
      } else {
        builder.listItem();
      }
      builder.text(params[i].parameterName(), TextStyle.CODE).text(" - ");
      builder.text(codeAndLinkParse(params[i].parameterComment()));
    }
    builder.newLine();
  }

  private static void methodDetailReturnTags(MDBuilder builder, MethodDoc methodDoc) {
    Tag[] tags = methodDoc.tags("@return");
    for (int i = 0; i < tags.length; i++) {
      if (i == 0) {
        builder.header(4).text("Returns:", true);
        builder.newList();
      } else {
        builder.listItem();
      }
      builder.text(codeAndLinkParse(tags[i].text()));
    }
    builder.newLine();
  }

  private static void methodDetailSeeTags(MDBuilder builder, MethodDoc methodDoc) {
    SeeTag[] tags = methodDoc.seeTags();
    for (int i = 0; i < tags.length; i++) {
      if (i == 0) {
        builder.header(4).text("See Also:", true);
        builder.newList();
      } else {
        builder.listItem();
      }
      SeeTag tag = tags[i];
      if (tag.referencedMemberName() != null) {
        builder.text(tag.referencedClassName() + "." + tag.referencedMemberName(), TextStyle.CODE);
      } else {
        builder.text(tag.referencedClassName(), TextStyle.CODE);
      }
    }
    builder.newLine();
  }

  private static void methodDetailThrowsTags(MDBuilder builder, MethodDoc methodDoc) {
    ThrowsTag[] tags = methodDoc.throwsTags();
    for (int i = 0; i < tags.length; i++) {
      if (i == 0) {
        builder.header(4).text("See Also:", true);
        builder.newList();
      } else {
        builder.listItem();
      }
      ThrowsTag tag = tags[i];
      builder.text(tag.exceptionName(), TextStyle.CODE).text(" - ").text(tag.exceptionComment());
    }
    builder.newLine();
  }

  private static void methodSignature(MDBuilder builder, MethodDoc methodDoc) {
    builder.text(methodDoc.name()).text("(`");
    Parameter[] parameters = methodDoc.parameters();
    for (int i = 0; i < parameters.length; i++) {
      builder.text(parameters[i].typeName()).text(" ").text(parameters[i].name());
      if (i < parameters.length - 1) {
        builder.text(", ");
      }
    }
    builder.text("`)", true);
  }

  private static String codeAndLinkParse(String text) {
    String tmp = text;
    tmp = tmp.replaceAll("\\{@code (.*?)\\}", "`$1`");
    // Links currently don't work as expected
    tmp = tmp.replaceAll("\\{@link (.*?)#(\\w*?)\\((.*)\\)\\}", "**$1.$2($3)**");// "[$1.$2($3)]($1.md#$2)");
    tmp = tmp.replaceAll("\\{@link (.*?)\\}", "**$1**");// "[$1]($1.md)");
    return tmp;
  }
}

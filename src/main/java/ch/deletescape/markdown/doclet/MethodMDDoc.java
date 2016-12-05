package ch.deletescape.markdown.doclet;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;

import ch.deletescape.markdown.MDBuilder;
import ch.deletescape.markdown.MDBuilder.TextStyle;

public class MethodMDDoc {

  static void methodSummary(MDBuilder builder, MethodDoc[] methods) {
    builder.header(2).text("Method Summary", TextStyle.ITALIC, true);
    builder.newList();
    for (int i = 0; i < methods.length; i++) {
      (i == 0 ? builder : builder.listItem()).text("[").text(methods[i].modifiers(), TextStyle.BOLD).text(" ");
      methodSignature(builder, methods[i]);
      builder.text("](#").text(Util.linkEncode(builder.getCurrentLine())).text(")", true).text("   ");
      builder.text(Util.codeAndLinkParse(methods[i].commentText()));
    }
    builder.endList();
  }

  static void methodDetail(MDBuilder builder, MethodDoc[] methods) {
    builder.header(2).text("Method Detail", TextStyle.ITALIC, true);
    for (MethodDoc methodDoc : methods) {
      builder.header(3).text(methodDoc.modifiers(), TextStyle.ITALIC).text(" ");
      methodSignature(builder, methodDoc);
      builder.newLine().text(Util.codeAndLinkParse(methodDoc.commentText()), true);
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
        builder.header(4).text("Parameters:", true).newList();
      } else {
        builder.listItem();
      }
      builder.text(params[i].parameterName(), TextStyle.CODE).text(" - ");
      builder.text(Util.codeAndLinkParse(params[i].parameterComment()));
    }
    builder.newLine();
  }

  private static void methodDetailThrowsTags(MDBuilder builder, MethodDoc methodDoc) {
    ThrowsTag[] tags = methodDoc.throwsTags();
    for (int i = 0; i < tags.length; i++) {
      if (i == 0) {
        builder.header(4).text("Throws:", true).newList();
      } else {
        builder.listItem();
      }
      ThrowsTag tag = tags[i];
      builder.text(tag.exceptionName(), TextStyle.CODE).text(" - ").text(tag.exceptionComment());
    }
    builder.newLine();
  }

  private static void methodDetailReturnTags(MDBuilder builder, MethodDoc methodDoc) {
    Tag[] tags = methodDoc.tags("@return");
    for (int i = 0; i < tags.length; i++) {
      if (i == 0) {
        builder.header(4).text("Returns:", true).newList();
      } else {
        builder.listItem();
      }
      builder.text(Util.codeAndLinkParse(tags[i].text()));
    }
    builder.newLine();
  }

  private static void methodDetailSeeTags(MDBuilder builder, MethodDoc methodDoc) {
    SeeTag[] tags = methodDoc.seeTags();
    for (int i = 0; i < tags.length; i++) {
      if (i == 0) {
        builder.header(4).text("See Also:", true).newList();
      } else {
        builder.listItem();
      }
      builder.text(tags[i].text().replace('#', '.'), TextStyle.CODE);
    }
    builder.newLine();
  }

  private static void methodSignature(MDBuilder builder, MethodDoc methodDoc) {
    Parameter[] parameters = methodDoc.parameters();
    builder.text(methodDoc.returnType().simpleTypeName(), TextStyle.BOLD).text(" ");
    builder.text(methodDoc.name(), TextStyle.BOLD);
    int length = parameters.length;
    if (length > 0) {
      builder.text("(`");
      for (int i = 0; i < length; i++) {
        builder.text(parameters[i].typeName()).text(" ").text(parameters[i].name());
        if (i < length - 1) {
          builder.text(", ");
        }
      }
      builder.text("`)");
    } else {
      builder.text("()");
    }
  }

}

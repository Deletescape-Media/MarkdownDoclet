package ch.deletescape.markdown.doclet;

public class Util {

  static String linkEncode(String str) {
    return str.replaceAll("[^\\w- ]", "").trim().replaceAll(" ", "-");
  }

  static String codeAndLinkParse(String text) {
    String tmp = text.replaceAll("\\{@code (.*?)\\}", "`$1`");
    // There is no way to actually properly hook up the links
    tmp = tmp.replaceAll("\\{@link (.*?)#(\\w*?)\\((.*)\\)\\}", "**$1.$2($3)**");
    return tmp.replaceAll("\\{@link (.*?)\\}", "**$1**");
  }

}

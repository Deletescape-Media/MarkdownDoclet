package ch.deletescape.markdown.doclet;

public class Util {
  private static boolean quiet = false;

  static String linkEncode(String str) {
    return str.replaceAll("[^\\w- ]", "").trim().replaceAll(" ", "-");
  }

  static String codeAndLinkParse(String text) {
    String tmp = text.replaceAll("\\{@code (.*?)\\}", "`$1`");
    // There is no way to actually properly hook up the links
    tmp = tmp.replaceAll("\\{@link (.*?)#(\\w*?)\\((.*)\\)\\}", "**$1.$2($3)**");
    return tmp.replaceAll("\\{@link (.*?)\\}", "**$1**");
  }

  static void println(String str) {
    if (!quiet) {
      System.out.println(str);
    }
  }

  static void setQuiet(boolean quiet) {
    Util.quiet = quiet;
  }

  static boolean isQuiet() {
    return quiet;
  }
}

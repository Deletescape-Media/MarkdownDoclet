package ch.deletescape.markdown;

public class MDBuilder {
  private final StringBuilder sb;

  public MDBuilder() {
    sb = new StringBuilder();
  }

  public MDBuilder text(String text) {
    return text(text, TextStyle.NONE);
  }

  public MDBuilder text(String text, boolean lineBreak) {
    return text(text, TextStyle.NONE, lineBreak);
  }

  public MDBuilder text(String text, TextStyle style) {
    return text(text, style, false);
  }

  public MDBuilder text(String text, TextStyle style, boolean lineBreak) {
    StringBuilder token = new StringBuilder(4);
    switch (style) {
      case BOLD:
        token.append("**");
        break;
      case ITALIC:
        token.append('_');
        break;
      case CODE:
        token.append('`');
        break;
      case BOLD_ITALIC:
        token.append("**_");
        break;
      case BOLD_CODE:
        token.append("**`");
        break;
      case ITALIC_CODE:
        token.append("_`");
        break;
      case BOLD_ITALIC_CODE:
        token.append("_**`");
        break;
      default:
        break;
    }
    sb.append(token);
    sb.append(text);
    sb.append(token.reverse());
    if (lineBreak) {
      newLine();
    }
    return this;
  }

  public MDBuilder header(int level) {
    if (level < 1 || level > 6) {
      throw new MarkdownSyntaxException("Invalid header level " + level);
    }
    for (int i = 0; i < level; i++) {
      sb.append('#');
    }
    sb.append(' ');
    return this;
  }

  public MDBuilder newList() {
    return newList(ListType.UNORDERED);
  }

  public MDBuilder newList(ListType listType) {
    if (listType.isOrdered()) {
      sb.append("1. ");
    } else {
      sb.append("* ");
    }
    return this;
  }

  public MDBuilder listItem() {
    sb.append("\n* ");
    return this;
  }

  public MDBuilder endList() {
    newLine();
    return this;
  }

  public MDBuilder blockquote() {
    sb.append("> ");
    return this;
  }

  public MDBuilder link(String text, String url) {
    sb.append('[');
    sb.append(text);
    sb.append("](");
    sb.append(url);
    sb.append(')');
    return this;
  }

  public MDBuilder link(String url) {
    return link(url, url);
  }

  public MDBuilder newLine() {
    sb.append("\n\n");
    return this;
  }

  public MDBuilder softWrap() {
    sb.append("  \n");
    return this;
  }

  public String get() {
    return sb.toString();
  }

  public enum TextStyle {
    NONE, BOLD, ITALIC, CODE, BOLD_ITALIC, BOLD_CODE, ITALIC_CODE, BOLD_ITALIC_CODE;
  }

  public enum ListType {
    UNORDERED, ORDERED;
    public boolean isOrdered() {
      return equals(ORDERED);
    }
  }
}

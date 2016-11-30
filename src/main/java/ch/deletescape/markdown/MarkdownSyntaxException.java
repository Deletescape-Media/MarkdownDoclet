package ch.deletescape.markdown;

public class MarkdownSyntaxException extends RuntimeException {
  private static final long serialVersionUID = -746520118779798088L;

  public MarkdownSyntaxException(String message) {
    super(message);
  }
}

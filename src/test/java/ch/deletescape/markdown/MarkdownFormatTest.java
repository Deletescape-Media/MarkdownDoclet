package ch.deletescape.markdown;

import org.junit.Test;

import ch.deletescape.markdown.MDBuilder.TextStyle;

public class MarkdownFormatTest {

  @Test
  public void test() {
    MDBuilder builder = new MDBuilder();
    builder.header(1).text("Test", true);
    builder.header(2).text("First Chapter", true);
    builder.text("This is a ").text("test", TextStyle.BOLD_ITALIC).text(" paragraph.", true);
    builder.header(3).text("Politics", true);
    builder.text("Barack Obama said:", true).blockquote().text("Yes, we can!", true);
    builder.header(2).text("Links", true);
    builder.newList().link("Google", "http://google.com").listItem().link("Wikipedia", "http://wikipedia.org")
        .listItem().link(new MDBuilder().text("Bold link", TextStyle.BOLD).get(), "#").listItem()
        .link("http://google.com").endList();
    builder.header(2).text("Lorem Ipsum", true);
    builder.text("italic", TextStyle.ITALIC).text(" ").text("code", TextStyle.BOLD_ITALIC_CODE);
    System.out.println(builder.get());
  }

}

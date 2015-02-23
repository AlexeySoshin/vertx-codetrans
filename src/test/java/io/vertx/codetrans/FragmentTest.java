package io.vertx.codetrans;

import io.vertx.core.Handler;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FragmentTest extends ConversionTestBase {

  @Test
  public void testFragmentParser() {
    assertFragment("", "");
    assertFragment("", " ");
    assertFragment("\n", "\n");
    assertFragment("<a>", "//a");
    assertFragment("<a>\n", "//a\n");
    assertFragment("<a>\n", " //a\n");
    assertFragment("/*a*/", "/*a*/");
    assertFragment("/*a*/", "/*a*/");
    assertFragment("/***/", "/***/");
    assertFragment("/****/", "/****/");
    assertFragment("/*\n*/",
        "/*\n" +
        "*/");
    assertFragment("/*\n*/",
        " /*\n" +
        " */");
    assertFragment("/*\n */",
        " /*\n" +
        "  */");
    assertFragment("/*\na */",
        " /*\n" +
        "a */");
    assertFragment("/* \n*/",
        " /* \n" +
        "*/");
  }

  private void assertFragment(String expected, String s) {
    StringBuilder test = new StringBuilder();
    new FragmentParser() {
      @Override
      public void onNewline() {
        test.append('\n');
      }
      @Override
      public void onComment(char c) {
        test.append(c);
      }
      @Override
      public void onBeginComment(boolean multiline) {
        test.append(multiline ? "/*" : "<");
      }
      @Override
      public void onEndComment(boolean multiline) {
        test.append(multiline ? "*/" : ">");
      }
    }.parse(s);
    assertEquals(expected, test.toString());
  }

  @Test
  public void testEmpty() {
    Result.Source s = (Result.Source) convert(new GroovyLang(), "fragment/Fragment", "fragment/Fragment_empty.groovy");
    assertEquals("def a = null\n", s.getValue());
  }

  @Test
  public void testOnlyLineComments() {
    Result.Source s = (Result.Source) convert(new GroovyLang(), "fragment/Fragment", "fragment/Fragment_onlyLineComments.groovy");
    assertEquals("" +
        "// foo\n" +
        "// bar\n" +
        "// juu\n", s.getValue());
  }

  @Test
  public void testLineComments() {
    Result.Source s = (Result.Source) convert(new GroovyLang(), "fragment/Fragment", "fragment/Fragment_lineComments.groovy");
    assertEquals("" +
        "// foo\n" +
        "def t = null\n" +
        "// bar\n" +
        "def s = null\n" +
        "// juu\n", s.getValue());
  }

  @Test
  public void testMultiLineComments() {
    Result.Source s = (Result.Source) convert(new GroovyLang(), "fragment/Fragment", "fragment/Fragment_multiLineComments.groovy");
    assertEquals("" +
        "/*a\n" +
        "  b\n" +
        " c\n" +
        "d\n" +
        "e\n" +
        "f\n" +
        " */\n" +
        "def t = null\n" +
        "/*\n" +
        " * a\n" +
        " */\n", s.getValue());
  }

  public static void someMethod(Handler<String> handler) {
  }

  @Test
  public void testLineCommentsInLambda() {
    Result.Source s = (Result.Source) convert(new GroovyLang(), "fragment/Fragment", "fragment/Fragment_lineCommentsInLamba.groovy");
    assertEquals("" +
        "io.vertx.codetrans.FragmentTest.someMethod({ arg ->\n" +
        "  // foo\n" +
        "  def t = null\n" +
        "  // bar\n" +
        "  def s = null\n" +
        "  // juu\n" +
        "})\n", s.getValue());
  }
}

/**
 * Note: These are not Unit tests. But quick functions written for debugging.
 */
package Utils;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(MockitoJUnitRunner.class)
class UIHelperTest {

    void mock1() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRegexEscape() {
        String patternStr = "$1234[1-9]?(asd)|(qwe)+";
        String escaped1 = Pattern.quote(patternStr);
        String escaped2 = Matcher.quoteReplacement(patternStr);
        System.out.println(escaped1);
        System.out.println(escaped2);
        Pattern pattern1 = Pattern.compile(escaped1);
        Pattern pattern2 = Pattern.compile(escaped2);
        System.out.println(pattern1.matcher(patternStr).matches() ? "YES" : "NO");
        System.out.println(pattern2.matcher(patternStr).matches() ? "YES" : "NO");
    }

}
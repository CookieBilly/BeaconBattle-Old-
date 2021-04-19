

package javax.annotation;

import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
import javax.annotation.meta.TypeQualifierNickname;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

@Documented
@Syntax("RegEx")
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface RegEx {
    When when() default When.ALWAYS;
    
    public static class Checker implements TypeQualifierValidator<RegEx>
    {
        public When forConstantValue(final RegEx regEx, final Object o) {
            if (!(o instanceof String)) {
                return When.NEVER;
            }
            try {
                Pattern.compile((String)o);
            }
            catch (PatternSyntaxException ex) {
                return When.NEVER;
            }
            return When.ALWAYS;
        }
    }
}

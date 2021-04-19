

package javax.annotation;

import java.util.regex.Pattern;
import javax.annotation.meta.When;
import javax.annotation.meta.TypeQualifierValidator;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

@Documented
@TypeQualifier(applicableTo = String.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchesPattern {
    @RegEx
    String value();
    
    int flags() default 0;
    
    public static class Checker implements TypeQualifierValidator<MatchesPattern>
    {
        public When forConstantValue(final MatchesPattern matchesPattern, final Object o) {
            if (Pattern.compile(matchesPattern.value(), matchesPattern.flags()).matcher((CharSequence)o).matches()) {
                return When.ALWAYS;
            }
            return When.NEVER;
        }
    }
}

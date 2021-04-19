

package javax.annotation;

import javax.annotation.meta.TypeQualifierValidator;
import javax.annotation.meta.When;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import javax.annotation.meta.TypeQualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Annotation;

@Documented
@TypeQualifier(applicableTo = Number.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnegative {
    When when() default When.ALWAYS;
    
    public static class Checker implements TypeQualifierValidator<Nonnegative>
    {
        public When forConstantValue(final Nonnegative nonnegative, final Object o) {
            if (!(o instanceof Number)) {
                return When.NEVER;
            }
            final Number n = (Number)o;
            boolean b;
            if (n instanceof Long) {
                b = (n.longValue() < 0L);
            }
            else if (n instanceof Double) {
                b = (n.doubleValue() < 0.0);
            }
            else if (n instanceof Float) {
                b = (n.floatValue() < 0.0f);
            }
            else {
                b = (n.intValue() < 0);
            }
            if (b) {
                return When.NEVER;
            }
            return When.ALWAYS;
        }
    }
}

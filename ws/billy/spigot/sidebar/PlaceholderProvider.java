

package ws.billy.spigot.sidebar;

import java.util.Objects;
import java.util.concurrent.Callable;

public class PlaceholderProvider
{
    private String placeholder;
    private final Callable<String> replacement;
    
    public PlaceholderProvider(final String placeholder, final Callable<String> replacement) {
        this.placeholder = placeholder;
        this.replacement = replacement;
    }
    
    public String getPlaceholder() {
        return this.placeholder;
    }
    
    public String getReplacement() {
        try {
            return this.replacement.call();
        }
        catch (Exception ex) {
            return "-";
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof PlaceholderProvider && ((PlaceholderProvider)o).placeholder.equalsIgnoreCase(this.placeholder);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.placeholder, this.replacement);
    }
}



package ws.billy.spigot.sidebar;

import org.jetbrains.annotations.NotNull;

public abstract class SidebarLine
{
    private boolean hasPlaceholders;
    
    public SidebarLine() {
        this.hasPlaceholders = false;
    }
    
    @NotNull
    public abstract String getLine();
    
    public void setHasPlaceholders(final boolean hasPlaceholders) {
        this.hasPlaceholders = hasPlaceholders;
    }
    
    public boolean isHasPlaceholders() {
        return this.hasPlaceholders;
    }
}



package ws.billy.spigot.sidebar;

import org.jetbrains.annotations.NotNull;

public class SidebarLineAnimated extends SidebarLine
{
    private String[] lines;
    private int pos;
    
    public SidebarLineAnimated(final String[] lines) {
        this.pos = -1;
        this.lines = lines;
    }
    
    @NotNull
    @Override
    public String getLine() {
        return this.lines[(++this.pos == this.lines.length) ? (this.pos = 0) : this.pos];
    }
    
    public String[] getLines() {
        return this.lines;
    }
}



package ws.billy.spigot.sidebar;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import org.bukkit.Bukkit;

public class SidebarManager
{
    private SidebarProvider sidebarProvider;
    private static PAPISupport papiSupport;
    
    public SidebarManager() {
        try {
            this.sidebarProvider = (SidebarProvider)Class.forName("ws.billy.spigot.sidebar.Provider_" + Bukkit.getServer().getClass().getName().split("\\.")[3]).newInstance();
        }
        catch (ClassNotFoundException | IllegalAccessException ex) {
            throw new InstantiationException("Server not supported.");
        }
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            SidebarManager.papiSupport = new PAPIAdapter();
        }
        catch (ClassNotFoundException ex2) {}
    }
    
    public Sidebar createSidebar(final SidebarLine sidebarLine2, @NotNull final Collection<SidebarLine> collection, final Collection<PlaceholderProvider> collection2) {
        collection.forEach(sidebarLine -> collection2.forEach(placeholderProvider -> {
            if (sidebarLine.getLine().contains(placeholderProvider.getPlaceholder())) {
                sidebarLine.setHasPlaceholders(true);
            }
        }));
        return this.sidebarProvider.createSidebar(sidebarLine2, collection, collection2);
    }
    
    protected static PAPISupport getPapiSupport() {
        return SidebarManager.papiSupport;
    }
    
    static {
        SidebarManager.papiSupport = new PAPISupport() {
            @Override
            public String replacePlaceholders(final Player player, final String s) {
                return s;
            }
            
            @Override
            public boolean hasPlaceholders(final String s) {
                return false;
            }
            
            @Override
            public boolean isSupported() {
                return false;
            }
        };
    }
}

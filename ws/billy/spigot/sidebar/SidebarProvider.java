

package ws.billy.spigot.sidebar;

import java.util.Collection;

abstract class SidebarProvider
{
    public abstract Sidebar createSidebar(final SidebarLine p0, final Collection<SidebarLine> p1, final Collection<PlaceholderProvider> p2);
}

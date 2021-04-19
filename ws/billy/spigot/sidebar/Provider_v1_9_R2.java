

package ws.billy.spigot.sidebar;

import java.util.Collection;

class Provider_v1_9_R2 extends SidebarProvider
{
    @Override
    public Sidebar createSidebar(final SidebarLine sidebarLine, final Collection<SidebarLine> collection, final Collection<PlaceholderProvider> collection2) {
        return new Sidebar_v1_9_R2(sidebarLine, collection, collection2);
    }
}

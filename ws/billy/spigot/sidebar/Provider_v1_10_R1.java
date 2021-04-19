

package ws.billy.spigot.sidebar;

import java.util.Collection;

class Provider_v1_10_R1 extends SidebarProvider
{
    @Override
    public Sidebar createSidebar(final SidebarLine sidebarLine, final Collection<SidebarLine> collection, final Collection<PlaceholderProvider> collection2) {
        return new Sidebar_v1_10_R1(sidebarLine, collection, collection2);
    }
}

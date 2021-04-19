

package ws.billy.bedwars.support.vault;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WithChat implements Chat
{
    private static net.milkbowl.vault.chat.Chat chat;
    
    @Override
    public String getPrefix(final Player player) {
        return ChatColor.translateAlternateColorCodes('&', WithChat.chat.getPlayerPrefix(player));
    }
    
    @Override
    public String getSuffix(final Player player) {
        return ChatColor.translateAlternateColorCodes('&', WithChat.chat.getPlayerSuffix(player));
    }
    
    public static void setChat(final net.milkbowl.vault.chat.Chat chat) {
        WithChat.chat = chat;
    }
}

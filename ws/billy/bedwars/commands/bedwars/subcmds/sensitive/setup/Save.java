

package ws.billy.bedwars.commands.bedwars.subcmds.sensitive.setup;

import java.util.List;

import net.md_5.bungee.api.chat.BaseComponent;
import ws.billy.bedwars.api.BeaconBattle;
import ws.billy.bedwars.arena.Misc;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import ws.billy.bedwars.arena.SetupSession;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.CommandSender;
import ws.billy.bedwars.configuration.Permissions;
import ws.billy.bedwars.api.command.ParentCommand;
import ws.billy.bedwars.api.command.SubCommand;

public class Save extends SubCommand
{
    public Save(final ParentCommand parentCommand, final String s) {
        super(parentCommand, s);
        this.setArenaSetupCommand(true);
        this.setPermission(Permissions.PERMISSION_SETUP_ARENA);
    }
    
    @Override
    public boolean execute(final String[] array, final CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) {
            return false;
        }
        final Player player = (Player)commandSender;
        final SetupSession session = SetupSession.getSession(player.getUniqueId());
        if (session == null) {
            return false;
        }
        for (final Entity entity : player.getWorld().getEntities()) {
            if (entity.getType() == EntityType.ARMOR_STAND) {
                entity.remove();
            }
        }
        if (Bukkit.getWorld(ws.billy.bedwars.BeaconBattle.getLobbyWorld()) != null) {
            player.teleport(Bukkit.getWorld(ws.billy.bedwars.BeaconBattle.getLobbyWorld()).getSpawnLocation());
        }
        else {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        session.done();
        player.sendMessage(session.getPrefix() + "Arena changes saved!");
        player.sendMessage(session.getPrefix() + "You can now enable it using:");
        player.spigot().sendMessage((BaseComponent) Misc.msgHoverClick(ChatColor.GOLD + "/" + this.getParent().getName() + " enableArena " + session.getWorldName() + ChatColor.GRAY + " (click to enable)", ChatColor.GREEN + "Enable this arena.", "/" + this.getParent().getName() + " enableArena " + session.getWorldName(), ClickEvent.Action.RUN_COMMAND));
        return true;
    }
    
    @Override
    public List<String> getTabComplete() {
        return null;
    }
    
    @Override
    public boolean canSee(final CommandSender commandSender, final BeaconBattle BeaconBattle) {
        return !(commandSender instanceof ConsoleCommandSender) && SetupSession.isInSetupSession(((Player)commandSender).getUniqueId()) && this.hasPermission(commandSender);
    }
}

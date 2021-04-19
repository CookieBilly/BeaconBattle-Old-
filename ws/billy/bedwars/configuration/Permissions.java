

package ws.billy.bedwars.configuration;

import ws.billy.bedwars.BeaconBattle;

public class Permissions
{
    public static final String PERMISSION_FORCESTART;
    public static final String PERMISSION_ALL;
    public static final String PERMISSION_COMMAND_BYPASS;
    public static final String PERMISSION_SHOUT_COMMAND;
    public static final String PERMISSION_SETUP_ARENA;
    public static final String PERMISSION_ARENA_GROUP;
    public static final String PERMISSION_BUILD;
    public static final String PERMISSION_CLONE;
    public static final String PERMISSION_DEL_ARENA;
    public static final String PERMISSION_ARENA_ENABLE;
    public static final String PERMISSION_ARENA_DISABLE;
    public static final String PERMISSION_NPC;
    public static final String PERMISSION_RELOAD;
    public static final String PERMISSION_REJOIN;
    public static final String PERMISSION_LEVEL;
    
    static {
        PERMISSION_FORCESTART = BeaconBattle.mainCmd + ".forcestart";
        PERMISSION_ALL = BeaconBattle.mainCmd + ".*";
        PERMISSION_COMMAND_BYPASS = BeaconBattle.mainCmd + ".cmd.bypass";
        PERMISSION_SHOUT_COMMAND = BeaconBattle.mainCmd + ".shout";
        PERMISSION_SETUP_ARENA = BeaconBattle.mainCmd + ".setup";
        PERMISSION_ARENA_GROUP = BeaconBattle.mainCmd + ".groups";
        PERMISSION_BUILD = BeaconBattle.mainCmd + ".build";
        PERMISSION_CLONE = BeaconBattle.mainCmd + ".clone";
        PERMISSION_DEL_ARENA = BeaconBattle.mainCmd + ".delete";
        PERMISSION_ARENA_ENABLE = BeaconBattle.mainCmd + ".enableRotation";
        PERMISSION_ARENA_DISABLE = BeaconBattle.mainCmd + ".disable";
        PERMISSION_NPC = BeaconBattle.mainCmd + ".npc";
        PERMISSION_RELOAD = BeaconBattle.mainCmd + ".reload";
        PERMISSION_REJOIN = BeaconBattle.mainCmd + ".rejoin";
        PERMISSION_LEVEL = BeaconBattle.mainCmd + ".level";
    }
}

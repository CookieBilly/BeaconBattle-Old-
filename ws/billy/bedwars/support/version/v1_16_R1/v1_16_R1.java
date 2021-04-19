

package ws.billy.bedwars.support.version.v1_16_R1;

import net.minecraft.server.v1_16_R1.SoundEffect;
import net.minecraft.server.v1_16_R1.Vec3D;
import net.minecraft.server.v1_16_R1.EnumMoveType;
import net.minecraft.server.v1_16_R1.GenericAttributes;
import net.minecraft.server.v1_16_R1.EntityInsentient;
import net.minecraft.server.v1_16_R1.AttributeProvider;
import net.minecraft.server.v1_16_R1.ReputationEvent;
import net.minecraft.server.v1_16_R1.EntityLightning;
import net.minecraft.server.v1_16_R1.GlobalPos;
import net.minecraft.server.v1_16_R1.MemoryModuleType;
import net.minecraft.server.v1_16_R1.VillagerData;
import net.minecraft.server.v1_16_R1.EntityVillager;
import org.bukkit.block.data.type.WallSign;
import net.minecraft.server.v1_16_R1.MinecraftServer;
import net.minecraft.server.v1_16_R1.DedicatedServer;
import org.bukkit.event.inventory.InventoryEvent;
import net.minecraft.server.v1_16_R1.PacketPlayOutPlayerInfo;
import org.bukkit.scoreboard.Team;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import ws.billy.bedwars.api.arena.team.TeamColor;
import org.bukkit.block.Block;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R1.Blocks;
import net.minecraft.server.v1_16_R1.BlockBase;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EnderDragon;
import java.util.logging.Level;
import net.minecraft.server.v1_16_R1.EntityPlayer;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_16_R1.IMaterial;
import net.minecraft.server.v1_16_R1.Item;
import net.minecraft.server.v1_16_R1.EnumItemSlot;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import net.minecraft.server.v1_16_R1.DamageSource;
import net.minecraft.server.v1_16_R1.WorldServer;
import org.bukkit.event.entity.CreatureSpawnEvent;
import net.minecraft.server.v1_16_R1.World;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import net.minecraft.server.v1_16_R1.NBTTagCompound;
import org.bukkit.entity.ArmorStand;
import ws.billy.bedwars.api.arena.shop.ShopHolo;
import ws.billy.bedwars.api.language.Language;
import ws.billy.bedwars.api.arena.IArena;
import java.util.Map;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.EnumCreatureType;
import net.minecraft.server.v1_16_R1.DataConverterTypes;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.server.v1_16_R1.SharedConstants;
import net.minecraft.server.v1_16_R1.DataConverterRegistry;
import net.minecraft.server.v1_16_R1.IProjectile;
import net.minecraft.server.v1_16_R1.ItemBow;
import net.minecraft.server.v1_16_R1.ItemAxe;
import net.minecraft.server.v1_16_R1.ItemSword;
import net.minecraft.server.v1_16_R1.ItemTool;
import net.minecraft.server.v1_16_R1.ItemArmor;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import java.lang.reflect.Field;
import net.minecraft.server.v1_16_R1.EntityLiving;
import net.minecraft.server.v1_16_R1.EntityTNTPrimed;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftTNTPrimed;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import net.minecraft.server.v1_16_R1.PacketPlayOutChat;
import net.minecraft.server.v1_16_R1.ChatMessageType;
import net.minecraft.server.v1_16_R1.PacketPlayOutEntityDestroy;

import java.util.List;
import ws.billy.bedwars.api.events.player.PlayerKillEvent;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.entity.Despawnable;
import ws.billy.bedwars.api.arena.team.ITeam;
import org.bukkit.Location;
import net.minecraft.server.v1_16_R1.Packet;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import net.minecraft.server.v1_16_R1.IChatBaseComponent;
import net.minecraft.server.v1_16_R1.PacketPlayOutTitle;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_16_R1.CraftServer;
import org.bukkit.command.Command;
import ws.billy.bedwars.support.version.common.VersionCommon;
import org.bukkit.plugin.Plugin;
import java.util.UUID;
import ws.billy.bedwars.api.server.VersionSupport;

public class v1_16_R1 extends VersionSupport
{
    private static int renderDistance;
    private static final UUID chatUUID;
    
    public v1_16_R1(final Plugin plugin, final String s) {
        super(plugin, s);
        this.loadDefaultEffects();
    }
    
    @Override
    public void registerVersionListeners() {
        new VersionCommon(this);
    }
    
    @Override
    public void registerCommand(final String s, final Command command) {
        ((CraftServer)this.getPlugin().getServer()).getCommandMap().register(s, command);
    }
    
    @Override
    public void sendTitle(final Player player, final String str, final String str2, final int n, final int n2, final int n3) {
        if (str != null && !str.isEmpty()) {
            final PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, (IChatBaseComponent)IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + str + "\"}"));
            final PacketPlayOutTitle packetPlayOutTitle2 = new PacketPlayOutTitle(n, n2, n3);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)packetPlayOutTitle);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)packetPlayOutTitle2);
        }
        if (str2 != null && !str2.isEmpty()) {
            final PacketPlayOutTitle packetPlayOutTitle3 = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, (IChatBaseComponent)IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + str2 + "\"}"));
            final PacketPlayOutTitle packetPlayOutTitle4 = new PacketPlayOutTitle(n, n2, n3);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)packetPlayOutTitle3);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)packetPlayOutTitle4);
        }
    }
    
    @Override
    public void spawnSilverfish(final Location location, final ITeam team, final double n, final double n2, final int n3, final double n4) {
        new Despawnable(Silverfish.spawn(location, team, n, n2, n3, n4), team, n3, Messages.SHOP_UTILITY_NPC_SILVERFISH_NAME, PlayerKillEvent.PlayerKillCause.SILVERFISH_FINAL_KILL, PlayerKillEvent.PlayerKillCause.SILVERFISH);
    }
    
    @Override
    public void spawnIronGolem(final Location location, final ITeam team, final double n, final double n2, final int n3) {
        new Despawnable(IGolem.spawn(location, team, n, n2, n3), team, n3, Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME, PlayerKillEvent.PlayerKillCause.IRON_GOLEM_FINAL_KILL, PlayerKillEvent.PlayerKillCause.IRON_GOLEM);
    }
    
    @Override
    public void hidePlayer(final Player player, final List<Player> list) {
        for (final Player player2 : list) {
            if (player2 == player) {
                continue;
            }
            player2.hidePlayer(this.getPlugin(), player);
        }
    }
    
    @Override
    public void hidePlayer(final Player player, final Player player2) {
        if (player == player2) {
            return;
        }
        ((CraftPlayer)player2).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutEntityDestroy(new int[] { player.getEntityId() }));
    }
    
    @Override
    public void playAction(final Player player, final String str) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutChat((IChatBaseComponent)IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + str + "\"}"), ChatMessageType.GAME_INFO, v1_16_R1.chatUUID));
    }
    
    @Override
    public boolean isBukkitCommandRegistered(final String s) {
        return ((CraftServer)this.getPlugin().getServer()).getCommandMap().getCommand(s) != null;
    }
    
    @Override
    public ItemStack getItemInHand(final Player player) {
        return player.getInventory().getItemInMainHand();
    }
    
    @Override
    public void hideEntity(final Entity entity, final Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutEntityDestroy(new int[] { entity.getEntityId() }));
    }
    
    @Override
    public void minusAmount(final Player player, final ItemStack itemStack, final int n) {
        itemStack.setAmount(itemStack.getAmount() - n);
    }
    
    @Override
    public void setSource(final TNTPrimed tntPrimed, final Player player) {
        final EntityLiving handle = ((CraftLivingEntity)player).getHandle();
        final EntityTNTPrimed handle2 = ((CraftTNTPrimed)tntPrimed).getHandle();
        try {
            final Field declaredField = EntityTNTPrimed.class.getDeclaredField("source");
            declaredField.setAccessible(true);
            declaredField.set(handle2, handle);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public boolean isArmor(final ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack) != null && CraftItemStack.asNMSCopy(itemStack).getItem() != null && CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemArmor;
    }
    
    @Override
    public boolean isTool(final ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack) != null && CraftItemStack.asNMSCopy(itemStack).getItem() != null && CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemTool;
    }
    
    @Override
    public boolean isSword(final ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack) != null && CraftItemStack.asNMSCopy(itemStack).getItem() != null && CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemSword;
    }
    
    @Override
    public boolean isAxe(final ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack).getItem() != null && CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemAxe;
    }
    
    @Override
    public boolean isBow(final ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack) != null && CraftItemStack.asNMSCopy(itemStack).getItem() != null && CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ItemBow;
    }
    
    @Override
    public boolean isProjectile(final ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack) != null && CraftItemStack.asNMSCopy(itemStack).getItem() != null && CraftItemStack.asNMSCopy(itemStack).A() instanceof IProjectile;
    }
    
    @Override
    public void registerEntities() {
        final Map types = DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion())).findChoiceType(DataConverterTypes.ENTITY).types();
        types.put("minecraft:bwvillager", types.get("minecraft:villager"));
        EntityTypes.Builder.a(VillagerShop::new, EnumCreatureType.CREATURE).a("bwvillager");
        types.put("minecraft:bwsilverfish", types.get("minecraft:silverfish"));
        EntityTypes.Builder.a(Silverfish::new, EnumCreatureType.MONSTER).a("bwsilverfish");
        types.put("minecraft:bwgolem", types.get("minecraft:iron_golem"));
        EntityTypes.Builder.a(IGolem::new, EnumCreatureType.AMBIENT).a("bwgolem");
    }
    
    @Override
    public void spawnShop(final Location location, final String s, final List<Player> list, final IArena arena) {
        final Location clone = location.clone();
        this.spawnVillager(clone);
        for (final Player player : list) {
            final String[] split = Language.getMsg(player, s).split(",");
            if (split.length == 1) {
                new ShopHolo(Language.getPlayerLanguage(player).getIso(), createArmorStand(split[0], clone.clone().add(0.0, 1.85, 0.0)), null, clone, arena);
            }
            else {
                new ShopHolo(Language.getPlayerLanguage(player).getIso(), createArmorStand(split[0], clone.clone().add(0.0, 2.1, 0.0)), createArmorStand(split[1], clone.clone().add(0.0, 1.85, 0.0)), clone, arena);
            }
        }
        for (final ShopHolo shopHolo : ShopHolo.getShopHolo()) {
            if (shopHolo.getA() == arena) {
                shopHolo.update();
            }
        }
    }
    
    @Override
    public double getDamage(final ItemStack itemStack) {
        final net.minecraft.server.v1_16_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        return (nmsCopy.hasTag() ? nmsCopy.getTag() : new NBTTagCompound()).getDouble("generic.attackDamage");
    }
    
    private static ArmorStand createArmorStand(final String customName, final Location location) {
        if (location == null) {
            return null;
        }
        if (location.getWorld() == null) {
            return null;
        }
        final ArmorStand armorStand = (ArmorStand)location.getWorld().spawn(location, (Class)ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(customName);
        return armorStand;
    }
    
    private void spawnVillager(final Location location) {
        final WorldServer handle = ((CraftWorld)location.getWorld()).getHandle();
        final VillagerShop villagerShop = new VillagerShop(EntityTypes.VILLAGER, (World)handle);
        villagerShop.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftLivingEntity)villagerShop.getBukkitEntity()).setRemoveWhenFarAway(false);
        handle.addEntity((net.minecraft.server.v1_16_R1.Entity)villagerShop, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
    
    @Override
    public void voidKill(final Player player) {
        ((CraftPlayer)player).getHandle().damageEntity(DamageSource.OUT_OF_WORLD, 1000.0f);
    }
    
    @Override
    public void hideArmor(final Player player, final Player player2) {
        final ArrayList<Pair> list = new ArrayList<Pair>();
        final ArrayList<Pair> list2 = new ArrayList<Pair>();
        list2.add(new Pair((Object)EnumItemSlot.MAINHAND, (Object)new net.minecraft.server.v1_16_R1.ItemStack((IMaterial)Item.getById(0))));
        list2.add(new Pair((Object)EnumItemSlot.OFFHAND, (Object)new net.minecraft.server.v1_16_R1.ItemStack((IMaterial)Item.getById(0))));
        list.add(new Pair((Object)EnumItemSlot.HEAD, (Object)new net.minecraft.server.v1_16_R1.ItemStack((IMaterial)Item.getById(0))));
        list.add(new Pair((Object)EnumItemSlot.CHEST, (Object)new net.minecraft.server.v1_16_R1.ItemStack((IMaterial)Item.getById(0))));
        list.add(new Pair((Object)EnumItemSlot.LEGS, (Object)new net.minecraft.server.v1_16_R1.ItemStack((IMaterial)Item.getById(0))));
        list.add(new Pair((Object)EnumItemSlot.FEET, (Object)new net.minecraft.server.v1_16_R1.ItemStack((IMaterial)Item.getById(0))));
        final PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(player.getEntityId(), (List)list);
        final PacketPlayOutEntityEquipment packetPlayOutEntityEquipment2 = new PacketPlayOutEntityEquipment(player.getEntityId(), (List)list2);
        final EntityPlayer handle = ((CraftPlayer)player2).getHandle();
        if (player != player2) {
            handle.playerConnection.sendPacket((Packet)packetPlayOutEntityEquipment2);
        }
        handle.playerConnection.sendPacket((Packet)packetPlayOutEntityEquipment);
    }
    
    @Override
    public void showArmor(final Player player, final Player player2) {
        final ArrayList<Pair> list = new ArrayList<Pair>();
        final ArrayList<Pair> list2 = new ArrayList<Pair>();
        list2.add(new Pair((Object)EnumItemSlot.MAINHAND, (Object)CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand())));
        list2.add(new Pair((Object)EnumItemSlot.OFFHAND, (Object)CraftItemStack.asNMSCopy(player.getInventory().getItemInOffHand())));
        list.add(new Pair((Object)EnumItemSlot.HEAD, (Object)CraftItemStack.asNMSCopy(player.getInventory().getHelmet())));
        list.add(new Pair((Object)EnumItemSlot.CHEST, (Object)CraftItemStack.asNMSCopy(player.getInventory().getChestplate())));
        list.add(new Pair((Object)EnumItemSlot.LEGS, (Object)CraftItemStack.asNMSCopy(player.getInventory().getLeggings())));
        list.add(new Pair((Object)EnumItemSlot.FEET, (Object)CraftItemStack.asNMSCopy(player.getInventory().getBoots())));
        final PacketPlayOutEntityEquipment packetPlayOutEntityEquipment = new PacketPlayOutEntityEquipment(player.getEntityId(), (List)list);
        final PacketPlayOutEntityEquipment packetPlayOutEntityEquipment2 = new PacketPlayOutEntityEquipment(player.getEntityId(), (List)list2);
        final EntityPlayer handle = ((CraftPlayer)player2).getHandle();
        if (player != player2) {
            handle.playerConnection.sendPacket((Packet)packetPlayOutEntityEquipment2);
        }
        handle.playerConnection.sendPacket((Packet)packetPlayOutEntityEquipment);
    }
    
    @Override
    public void spawnDragon(final Location location, final ITeam team) {
        if (location == null || location.getWorld() == null) {
            this.getPlugin().getLogger().log(Level.WARNING, "Could not spawn Dragon. Location is null");
            return;
        }
        ((EnderDragon)location.getWorld().spawnEntity(location, EntityType.ENDER_DRAGON)).setPhase(EnderDragon.Phase.CIRCLING);
    }
    
    @Override
    public void colorBed(final ITeam team) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                final BlockState state = team.getBed().clone().add((double)i, 0.0, (double)j).getBlock().getState();
                if (state instanceof Bed) {
                    state.setType(team.getColor().bedMaterial());
                    state.update();
                }
            }
        }
    }
    
    @Override
    public void registerTntWhitelist() {
        try {
            final Field declaredField = BlockBase.class.getDeclaredField("durability");
            declaredField.setAccessible(true);
            declaredField.set(Blocks.END_STONE, 12.0f);
            declaredField.set(Blocks.GLASS, 300.0f);
        }
        catch (NoSuchFieldException | IllegalAccessException ex) {
            final Throwable t;
            t.printStackTrace();
        }
    }
    
    @Override
    public void showPlayer(final Player player, final Player player2) {
        if (player == player2) {
            return;
        }
        if (player.getLocation().getWorld() == null) {
            return;
        }
        if (!player.getLocation().getWorld().equals(player2.getWorld())) {
            return;
        }
        if (player.getLocation().distanceSquared(player2.getLocation()) <= v1_16_R1.renderDistance) {
            ((CraftPlayer)player2).getHandle().playerConnection.sendPacket((Packet)new PacketPlayOutNamedEntitySpawn((EntityHuman)((CraftPlayer)player).getHandle()));
        }
    }
    
    @Override
    public void showPlayer(final Player obj, final List<Player> list) {
        for (final Player player : list) {
            if (player.equals(obj)) {
                continue;
            }
            player.showPlayer(this.getPlugin(), obj);
        }
    }
    
    @Override
    public void setBlockTeamColor(final Block block, final TeamColor teamColor) {
        if (block.getType().toString().contains("STAINED_GLASS") || block.getType().toString().equals("GLASS")) {
            block.setType(teamColor.glassMaterial());
        }
        else if (block.getType().toString().contains("_TERRACOTTA")) {
            block.setType(teamColor.glazedTerracottaMaterial());
        }
        else if (block.getType().toString().contains("_WOOL")) {
            block.setType(teamColor.woolMaterial());
        }
    }
    
    @Override
    public void setCollide(final Player player, final IArena arena, final boolean collidable) {
        player.setCollidable(collidable);
        if (arena == null) {
            return;
        }
        arena.updateSpectatorCollideRule(player, collidable);
    }
    
    @Override
    public ItemStack addCustomData(final ItemStack itemStack, final String s) {
        final net.minecraft.server.v1_16_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsCopy.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            nmsCopy.setTag(tag);
        }
        tag.setString("BeaconBattle1058", s);
        return CraftItemStack.asBukkitCopy(nmsCopy);
    }
    
    @Override
    public boolean isCustomBeaconBattleItem(final ItemStack itemStack) {
        final NBTTagCompound tag = CraftItemStack.asNMSCopy(itemStack).getTag();
        return tag != null && tag.hasKey("BeaconBattle1058");
    }
    
    @Override
    public String getCustomData(final ItemStack itemStack) {
        final NBTTagCompound tag = CraftItemStack.asNMSCopy(itemStack).getTag();
        if (tag == null) {
            return "";
        }
        return tag.getString("BeaconBattle1058");
    }
    
    @Override
    public ItemStack setSkullOwner(final ItemStack itemStack, final Player owningPlayer) {
        if (!(itemStack.getItemMeta() instanceof SkullMeta)) {
            return itemStack;
        }
        final SkullMeta itemMeta = (SkullMeta)itemStack.getItemMeta();
        itemMeta.setOwningPlayer((OfflinePlayer)owningPlayer);
        itemStack.setItemMeta((ItemMeta)itemMeta);
        return itemStack;
    }
    
    @Override
    public ItemStack colourItem(final ItemStack itemStack, final ITeam team) {
        if (itemStack == null) {
            return null;
        }
        final String string = itemStack.getType().toString();
        if (string.contains("_BED")) {
            return new ItemStack(team.getColor().bedMaterial(), itemStack.getAmount());
        }
        if (string.contains("_STAINED_GLASS_PANE")) {
            return new ItemStack(team.getColor().glassPaneMaterial(), itemStack.getAmount());
        }
        if (string.contains("STAINED_GLASS") || string.equals("GLASS")) {
            return new ItemStack(team.getColor().glassMaterial(), itemStack.getAmount());
        }
        if (string.contains("_TERRACOTTA")) {
            return new ItemStack(team.getColor().glazedTerracottaMaterial(), itemStack.getAmount());
        }
        if (string.contains("_WOOL")) {
            return new ItemStack(team.getColor().woolMaterial(), itemStack.getAmount());
        }
        return itemStack;
    }
    
    @Override
    public ItemStack createItemStack(final String str, final int n, final short n2) {
        ItemStack itemStack;
        try {
            itemStack = new ItemStack(Material.valueOf(str), n);
        }
        catch (Exception ex) {
            this.getPlugin().getLogger().log(Level.WARNING, str + " is not a valid " + VersionSupport.getName() + " material!");
            itemStack = new ItemStack(Material.BEDROCK);
        }
        return itemStack;
    }
    
    @Override
    public void teamCollideRule(final Team team) {
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.setCanSeeFriendlyInvisibles(true);
    }
    
    @Override
    public Material materialFireball() {
        return Material.FIRE_CHARGE;
    }
    
    @Override
    public Material materialPlayerHead() {
        return Material.PLAYER_HEAD;
    }
    
    @Override
    public Material materialSnowball() {
        return Material.SNOWBALL;
    }
    
    @Override
    public Material materialGoldenHelmet() {
        return Material.GOLDEN_HELMET;
    }
    
    @Override
    public Material materialGoldenChestPlate() {
        return Material.GOLDEN_CHESTPLATE;
    }
    
    @Override
    public Material materialGoldenLeggings() {
        return Material.GOLDEN_LEGGINGS;
    }
    
    @Override
    public Material materialCake() {
        return Material.CAKE;
    }
    
    @Override
    public Material materialCraftingTable() {
        return Material.CRAFTING_TABLE;
    }
    
    @Override
    public Material materialEnchantingTable() {
        return Material.ENCHANTING_TABLE;
    }
    
    @Override
    public Material woolMaterial() {
        return Material.WHITE_WOOL;
    }
    
    @Override
    public String getShopUpgradeIdentifier(final ItemStack itemStack) {
        final NBTTagCompound tag = CraftItemStack.asNMSCopy(itemStack).getTag();
        return (tag == null) ? "null" : (tag.hasKey("tierIdentifier") ? tag.getString("tierIdentifier") : "null");
    }
    
    @Override
    public ItemStack setShopUpgradeIdentifier(final ItemStack itemStack, final String s) {
        final net.minecraft.server.v1_16_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsCopy.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            nmsCopy.setTag(tag);
        }
        tag.setString("tierIdentifier", s);
        return CraftItemStack.asBukkitCopy(nmsCopy);
    }
    
    @Override
    public ItemStack getPlayerHead(final Player player, final ItemStack itemStack) {
        ItemStack bukkitCopy = new ItemStack(this.materialPlayerHead());
        if (itemStack != null) {
            final net.minecraft.server.v1_16_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(bukkitCopy);
            nmsCopy.setTag(CraftItemStack.asNMSCopy(itemStack).getTag());
            bukkitCopy = CraftItemStack.asBukkitCopy(nmsCopy);
        }
        final SkullMeta skullMeta = (SkullMeta)bukkitCopy.getItemMeta();
        try {
            final Field declaredField = skullMeta.getClass().getDeclaredField("profile");
            declaredField.setAccessible(true);
            declaredField.set(skullMeta, ((CraftPlayer)player).getProfile());
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            final Throwable t;
            t.printStackTrace();
        }
        bukkitCopy.setItemMeta((ItemMeta)skullMeta);
        return bukkitCopy;
    }
    
    @Override
    public void invisibilityFix(final Player obj, final IArena arena) {
        final EntityPlayer handle = ((CraftPlayer)obj).getHandle();
        for (final Player player : arena.getPlayers()) {
            if (player.equals(obj)) {
                continue;
            }
            if (arena.getRespawn().containsKey(player)) {
                continue;
            }
            if (player.getLocation().distance(obj.getLocation()) > v1_16_R1.renderDistance) {
                continue;
            }
            final EntityPlayer handle2 = ((CraftPlayer)player).getHandle();
            if (!arena.getShowTime().containsKey(player)) {
                handle.playerConnection.sendPacket((Packet)new PacketPlayOutNamedEntitySpawn((EntityHuman)handle2));
                handle.playerConnection.sendPacket((Packet)new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] { handle2 }));
                this.showArmor(player, obj);
            }
            handle2.playerConnection.sendPacket((Packet)new PacketPlayOutNamedEntitySpawn((EntityHuman)handle));
            handle2.playerConnection.sendPacket((Packet)new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] { handle }));
            this.showArmor(obj, player);
        }
    }
    
    @Override
    public String getInventoryName(final InventoryEvent inventoryEvent) {
        return inventoryEvent.getView().getTitle();
    }
    
    @Override
    public void setUnbreakable(final ItemMeta itemMeta) {
        itemMeta.setUnbreakable(true);
    }
    
    @Override
    public String getMainLevel() {
        return ((DedicatedServer)MinecraftServer.getServer()).getDedicatedServerProperties().levelName;
    }
    
    @Override
    public int getVersion() {
        return 8;
    }
    
    @Override
    public void setJoinSignBackground(final BlockState blockState, final Material type) {
        if (blockState.getBlockData() instanceof WallSign) {
            blockState.getBlock().getRelative(((WallSign)blockState.getBlockData()).getFacing().getOppositeFace()).setType(type);
        }
    }
    
    static {
        v1_16_R1.renderDistance = 48;
        chatUUID = new UUID(0L, 0L);
    }
    
    public static class VillagerShop extends EntityVillager
    {
        public void openTrade(final EntityHuman entityHuman, final IChatBaseComponent chatBaseComponent, final int n) {
        }
        
        public void setTradingPlayer(final EntityHuman entityHuman) {
        }
        
        VillagerShop(final EntityTypes entityTypes, final World world) {
            super(entityTypes, world);
        }
        
        public void collide(final net.minecraft.server.v1_16_R1.Entity entity) {
        }
        
        public boolean damageEntity(final DamageSource damageSource, final float n) {
            return false;
        }
        
        public void setVillagerData(final VillagerData villagerData) {
        }
        
        public void a(final MemoryModuleType<GlobalPos> memoryModuleType) {
        }
        
        public void onLightningStrike(final EntityLightning entityLightning) {
        }
        
        public void a(final EntityVillager entityVillager, final long n) {
        }
        
        public void a(final ReputationEvent reputationEvent, final net.minecraft.server.v1_16_R1.Entity entity) {
        }
        
        public int getExperience() {
            return 0;
        }
        
        public static AttributeProvider.Builder eX() {
            return EntityInsentient.p().a(GenericAttributes.MOVEMENT_SPEED, 0.0).a(GenericAttributes.FOLLOW_RANGE, 0.0);
        }
        
        public void move(final EnumMoveType enumMoveType, final Vec3D vec3D) {
        }
        
        public void playSound(final SoundEffect soundEffect, final float n, final float n2) {
        }
        
        public void fd() {
        }
    }
}

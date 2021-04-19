

package ws.billy.bedwars.support.version.v1_8_R3;

import ws.billy.bedwars.support.version.common.VersionCommon;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minecraft.server.v1_8_R3.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.apache.commons.lang3.StringUtils;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.language.Language;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import java.lang.reflect.Field;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalFloat;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R3.World;
import ws.billy.bedwars.api.arena.team.ITeam;
import net.minecraft.server.v1_8_R3.EntitySilverfish;

public class Silverfish extends EntitySilverfish
{
    private ITeam team;
    
    public Silverfish(final World world, final ITeam team) {
        super(world);
        if (team == null) {
            return;
        }
        try {
            final Field declaredField = PathfinderGoalSelector.class.getDeclaredField("b");
            declaredField.setAccessible(true);
            final Field declaredField2 = PathfinderGoalSelector.class.getDeclaredField("c");
            declaredField2.setAccessible(true);
            declaredField.set(this.goalSelector, new UnsafeList());
            declaredField.set(this.targetSelector, new UnsafeList());
            declaredField2.set(this.goalSelector, new UnsafeList());
            declaredField2.set(this.targetSelector, new UnsafeList());
        }
        catch (IllegalAccessException | NoSuchFieldException ex) {
            final Throwable t;
            t.printStackTrace();
        }
        this.team = team;
        this.goalSelector.a(1, (PathfinderGoal)new PathfinderGoalFloat((EntityInsentient)this));
        this.goalSelector.a(2, (PathfinderGoal)new PathfinderGoalMeleeAttack((EntityCreature)this, 1.9, false));
        this.targetSelector.a(1, (PathfinderGoal)new PathfinderGoalHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.goalSelector.a(3, (PathfinderGoal)new PathfinderGoalRandomStroll((EntityCreature)this, 2.0));
        this.targetSelector.a(2, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)EntityHuman.class, 20, true, false, o -> o != null && ((EntityHuman)o).isAlive() && !this.team.wasMember(((EntityHuman)o).getUniqueID()) && !this.team.getArena().isReSpawning(((EntityHuman)o).getUniqueID()) && !this.team.getArena().isSpectator(((EntityHuman)o).getUniqueID())));
        this.targetSelector.a(3, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)IGolem.class, 20, true, false, o -> o != null && ((IGolem)o).getTeam() != this.team));
        this.targetSelector.a(4, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)Silverfish.class, 20, true, false, o -> o != null && ((Silverfish)o).getTeam() != this.team));
    }
    
    public ITeam getTeam() {
        return this.team;
    }
    
    public static LivingEntity spawn(final Location location, final ITeam team, final double value, final double value2, final int i, final double value3) {
        final WorldServer handle = ((CraftWorld)location.getWorld()).getHandle();
        final Silverfish silverfish = new Silverfish((World)handle, team);
        silverfish.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        silverfish.getAttributeInstance(GenericAttributes.maxHealth).setValue(value2);
        silverfish.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(value);
        silverfish.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(value3);
        ((CraftLivingEntity)silverfish.getBukkitEntity()).setRemoveWhenFarAway(false);
        silverfish.setCustomName(Language.getDefaultLanguage().m(Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME).replace("{despawn}", String.valueOf(i).replace("{health}", StringUtils.repeat(Language.getDefaultLanguage().m(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH) + " ", 10)).replace("{TeamColor}", team.getColor().chat().toString())));
        silverfish.setCustomNameVisible(true);
        handle.addEntity((Entity)silverfish, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (LivingEntity)silverfish.getBukkitEntity();
    }
    
    public void die(final DamageSource damageSource) {
        super.die(damageSource);
        this.team = null;
        VersionCommon.api.getVersionSupport().getDespawnablesList().remove(this.getUniqueID());
    }
    
    public void die() {
        super.die();
        this.team = null;
        VersionCommon.api.getVersionSupport().getDespawnablesList().remove(this.getUniqueID());
    }
}

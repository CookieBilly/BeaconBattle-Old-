

package ws.billy.bedwars.support.version.v1_13_R2;

import net.minecraft.server.v1_13_R2.DamageSource;
import ws.billy.bedwars.support.version.common.VersionCommon;
import net.minecraft.server.v1_13_R2.WorldServer;
import org.apache.commons.lang.StringUtils;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.language.Language;
import net.minecraft.server.v1_13_R2.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import java.lang.reflect.Field;
import net.minecraft.server.v1_13_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_13_R2.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_13_R2.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_13_R2.EntityCreature;
import net.minecraft.server.v1_13_R2.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_13_R2.PathfinderGoal;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import net.minecraft.server.v1_13_R2.PathfinderGoalFloat;
import net.minecraft.server.v1_13_R2.Navigation;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_13_R2.PathfinderGoalSelector;
import net.minecraft.server.v1_13_R2.World;
import ws.billy.bedwars.api.arena.team.ITeam;
import net.minecraft.server.v1_13_R2.EntityIronGolem;

public class IGolem extends EntityIronGolem
{
    private ITeam team;
    
    public IGolem(final World world, final ITeam team) {
        super(world);
        this.team = team;
        try {
            final Field declaredField = PathfinderGoalSelector.class.getDeclaredField("b");
            declaredField.setAccessible(true);
            final Field declaredField2 = PathfinderGoalSelector.class.getDeclaredField("c");
            declaredField2.setAccessible(true);
            declaredField.set(this.goalSelector, Sets.newLinkedHashSet());
            declaredField.set(this.targetSelector, Sets.newLinkedHashSet());
            declaredField2.set(this.goalSelector, Sets.newLinkedHashSet());
            declaredField2.set(this.targetSelector, Sets.newLinkedHashSet());
        }
        catch (IllegalAccessException | NoSuchFieldException ex) {
            final Throwable t;
            t.printStackTrace();
        }
        this.setSize(1.4f, 2.9f);
        ((Navigation)this.getNavigation()).a(true);
        this.goalSelector.a(1, (PathfinderGoal)new PathfinderGoalFloat((EntityInsentient)this));
        this.goalSelector.a(2, (PathfinderGoal)new PathfinderGoalMeleeAttack((EntityCreature)this, 1.5, false));
        this.targetSelector.a(1, (PathfinderGoal)new PathfinderGoalHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.goalSelector.a(3, (PathfinderGoal)new PathfinderGoalRandomStroll((EntityCreature)this, 1.0));
        this.goalSelector.a(4, (PathfinderGoal)new PathfinderGoalRandomLookaround((EntityInsentient)this));
        this.targetSelector.a(2, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)EntityHuman.class, 20, true, false, entityHuman -> entityHuman != null && entityHuman.isAlive() && !this.team.wasMember(entityHuman.getUniqueID()) && !this.team.getArena().isReSpawning(entityHuman.getUniqueID()) && !this.team.getArena().isSpectator(entityHuman.getUniqueID())));
        this.targetSelector.a(3, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)IGolem.class, 20, true, false, golem -> golem != null && golem.getTeam() != this.team));
        this.targetSelector.a(4, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)Silverfish.class, 20, true, false, silverfish -> silverfish != null && silverfish.getTeam() != this.team));
    }
    
    public IGolem(final World world) {
        super(world);
    }
    
    public ITeam getTeam() {
        return this.team;
    }
    
    public static LivingEntity spawn(final Location location, final ITeam team, final double value, final double value2, final int i) {
        final WorldServer handle = ((CraftWorld)location.getWorld()).getHandle();
        final IGolem golem = new IGolem((World)handle, team);
        golem.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftLivingEntity)golem.getBukkitEntity()).setRemoveWhenFarAway(false);
        golem.setCustomNameVisible(true);
        golem.getAttributeInstance(GenericAttributes.maxHealth).setValue(value2);
        golem.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(value);
        handle.addEntity((Entity)golem, CreatureSpawnEvent.SpawnReason.CUSTOM);
        golem.getBukkitEntity().setCustomName(Language.getDefaultLanguage().m(Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME).replace("{despawn}", String.valueOf(i).replace("{health}", StringUtils.repeat(Language.getDefaultLanguage().m(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH) + " ", 10)).replace("{TeamColor}", team.getColor().chat().toString())));
        return (LivingEntity)golem.getBukkitEntity();
    }
    
    public void die() {
        super.die();
        this.team = null;
        VersionCommon.api.getVersionSupport().getDespawnablesList().remove(this.getUniqueID());
    }
    
    public void die(final DamageSource damageSource) {
        super.die(damageSource);
        this.team = null;
        VersionCommon.api.getVersionSupport().getDespawnablesList().remove(this.getUniqueID());
    }
}

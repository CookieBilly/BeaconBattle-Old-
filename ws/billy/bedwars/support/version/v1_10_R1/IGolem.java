

package ws.billy.bedwars.support.version.v1_10_R1;

import net.minecraft.server.v1_10_R1.DamageSource;
import ws.billy.bedwars.support.version.common.VersionCommon;
import net.minecraft.server.v1_10_R1.MinecraftKey;
import net.minecraft.server.v1_10_R1.WorldServer;
import net.minecraft.server.v1_10_R1.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.apache.commons.lang3.StringUtils;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.language.Language;
import net.minecraft.server.v1_10_R1.GenericAttributes;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import java.lang.reflect.Field;
import net.minecraft.server.v1_10_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_10_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_10_R1.EntityCreature;
import net.minecraft.server.v1_10_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_10_R1.PathfinderGoal;
import net.minecraft.server.v1_10_R1.EntityInsentient;
import net.minecraft.server.v1_10_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_10_R1.Navigation;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_10_R1.World;
import ws.billy.bedwars.api.arena.team.ITeam;
import net.minecraft.server.v1_10_R1.EntityIronGolem;

public class IGolem extends EntityIronGolem
{
    private ITeam team;
    
    private IGolem(final World world, final ITeam team) {
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
        this.goalSelector.a(2, (PathfinderGoal)new PathfinderGoalMeleeAttack((EntityCreature)this, 15.0, false));
        this.targetSelector.a(1, (PathfinderGoal)new PathfinderGoalHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.goalSelector.a(3, (PathfinderGoal)new PathfinderGoalRandomStroll((EntityCreature)this, 1.0));
        this.goalSelector.a(4, (PathfinderGoal)new PathfinderGoalRandomLookaround((EntityInsentient)this));
        this.targetSelector.a(2, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)EntityHuman.class, 10, true, false, o -> ((EntityHuman)o).isAlive() && !team.wasMember(((EntityHuman)o).getUniqueID()) && !team.getArena().isReSpawning(((EntityHuman)o).getUniqueID()) && !team.getArena().isSpectator(((EntityHuman)o).getUniqueID())));
        this.targetSelector.a(3, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)IGolem.class, 10, true, false, o -> ((IGolem)o).getTeam() != team));
        this.targetSelector.a(4, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)Silverfish.class, 10, true, false, o -> ((Silverfish)o).getTeam() != team));
    }
    
    public ITeam getTeam() {
        return this.team;
    }
    
    public static LivingEntity spawn(final Location location, final ITeam team, final double n, final double value, final int i) {
        final WorldServer handle = ((CraftWorld)location.getWorld()).getHandle();
        final IGolem golem = new IGolem((World)handle, team);
        golem.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftLivingEntity)golem.getBukkitEntity()).setRemoveWhenFarAway(false);
        golem.setCustomNameVisible(true);
        golem.getAttributeInstance(GenericAttributes.maxHealth).setValue(value);
        golem.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(n);
        golem.getAttributeInstance(GenericAttributes.f).setValue(n);
        golem.setCustomName(Language.getDefaultLanguage().m(Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME).replace("{despawn}", String.valueOf(i).replace("{health}", StringUtils.repeat(Language.getDefaultLanguage().m(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH) + " ", 10)).replace("{TeamColor}", team.getColor().chat().toString())));
        handle.addEntity((Entity)golem, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (LivingEntity)golem.getBukkitEntity();
    }
    
    protected MinecraftKey J() {
        return null;
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

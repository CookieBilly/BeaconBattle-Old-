

package ws.billy.bedwars.support.version.v1_16_R1;

import net.minecraft.server.v1_16_R1.DamageSource;
import ws.billy.bedwars.support.version.common.VersionCommon;
import net.minecraft.server.v1_16_R1.WorldServer;
import org.apache.commons.lang.StringUtils;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.language.Language;
import net.minecraft.server.v1_16_R1.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import net.minecraft.server.v1_16_R1.GenericAttributes;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import net.minecraft.server.v1_16_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R1.EntityHuman;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_16_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_16_R1.EntityCreature;
import net.minecraft.server.v1_16_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_16_R1.PathfinderGoal;
import net.minecraft.server.v1_16_R1.EntityInsentient;
import net.minecraft.server.v1_16_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_16_R1.World;
import net.minecraft.server.v1_16_R1.EntityTypes;
import ws.billy.bedwars.api.arena.team.ITeam;
import net.minecraft.server.v1_16_R1.EntityIronGolem;

public class IGolem extends EntityIronGolem
{
    private ITeam team;
    
    private IGolem(final EntityTypes<? extends EntityIronGolem> entityTypes, final World world, final ITeam team) {
        super((EntityTypes)entityTypes, world);
        this.team = team;
    }
    
    public IGolem(final EntityTypes entityTypes, final World world) {
        super(entityTypes, world);
    }
    
    protected void initPathfinder() {
        this.goalSelector.a(1, (PathfinderGoal)new PathfinderGoalFloat((EntityInsentient)this));
        this.goalSelector.a(2, (PathfinderGoal)new PathfinderGoalMeleeAttack((EntityCreature)this, 1.5, false));
        this.targetSelector.a(1, (PathfinderGoal)new PathfinderGoalHurtByTarget((EntityCreature)this, new Class[0]));
        this.goalSelector.a(3, (PathfinderGoal)new PathfinderGoalRandomStroll((EntityCreature)this, 1.0));
        this.goalSelector.a(4, (PathfinderGoal)new PathfinderGoalRandomLookaround((EntityInsentient)this));
        this.targetSelector.a(2, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityInsentient)this, (Class)EntityHuman.class, 20, true, false, entityHuman -> entityHuman.isAlive() && !this.team.wasMember(entityHuman.getUniqueID()) && !this.team.getArena().isReSpawning(entityHuman.getUniqueID()) && !this.team.getArena().isSpectator(entityHuman.getUniqueID())));
        this.targetSelector.a(3, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityInsentient)this, (Class)IGolem.class, 20, true, false, golem -> golem.getTeam() != this.team));
        this.targetSelector.a(4, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityInsentient)this, (Class)Silverfish.class, 20, true, false, silverfish -> silverfish.getTeam() != this.team));
    }
    
    public ITeam getTeam() {
        return this.team;
    }
    
    public static LivingEntity spawn(final Location location, final ITeam team, final double value, final double value2, final int i) {
        final WorldServer handle = ((CraftWorld)location.getWorld()).getHandle();
        final IGolem golem = new IGolem((EntityTypes<? extends EntityIronGolem>)EntityTypes.IRON_GOLEM, (World)handle, team);
        golem.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ((CraftLivingEntity)golem.getBukkitEntity()).setRemoveWhenFarAway(false);
        golem.setCustomNameVisible(true);
        golem.setPersistent();
        golem.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(value2);
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

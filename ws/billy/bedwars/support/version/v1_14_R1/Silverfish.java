

package ws.billy.bedwars.support.version.v1_14_R1;

import net.minecraft.server.v1_14_R1.DamageSource;
import ws.billy.bedwars.support.version.common.VersionCommon;
import net.minecraft.server.v1_14_R1.WorldServer;
import org.apache.commons.lang.StringUtils;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.language.Language;
import net.minecraft.server.v1_14_R1.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import net.minecraft.server.v1_14_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_14_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_14_R1.EntityCreature;
import net.minecraft.server.v1_14_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_14_R1.PathfinderGoal;
import net.minecraft.server.v1_14_R1.EntityInsentient;
import net.minecraft.server.v1_14_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_14_R1.World;
import net.minecraft.server.v1_14_R1.EntityTypes;
import ws.billy.bedwars.api.arena.team.ITeam;
import net.minecraft.server.v1_14_R1.EntitySilverfish;

public class Silverfish extends EntitySilverfish
{
    private ITeam team;
    
    private Silverfish(final EntityTypes<? extends EntitySilverfish> entityTypes, final World world, final ITeam team) {
        super((EntityTypes)entityTypes, world);
        this.team = team;
    }
    
    public Silverfish(final EntityTypes entityTypes, final World world) {
        super(entityTypes, world);
    }
    
    protected void initPathfinder() {
        this.goalSelector.a(1, (PathfinderGoal)new PathfinderGoalFloat((EntityInsentient)this));
        this.goalSelector.a(2, (PathfinderGoal)new PathfinderGoalMeleeAttack((EntityCreature)this, 1.9, false));
        this.targetSelector.a(1, (PathfinderGoal)new PathfinderGoalHurtByTarget((EntityCreature)this, new Class[0]));
        this.goalSelector.a(3, (PathfinderGoal)new PathfinderGoalRandomStroll((EntityCreature)this, 2.0));
        this.targetSelector.a(2, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityInsentient)this, (Class)EntityHuman.class, 20, true, false, entityHuman -> entityHuman.isAlive() && !this.team.wasMember(entityHuman.getUniqueID()) && !this.team.getArena().isReSpawning(entityHuman.getUniqueID()) && !this.team.getArena().isSpectator(entityHuman.getUniqueID())));
        this.targetSelector.a(3, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityInsentient)this, (Class)IGolem.class, 20, true, false, golem -> golem.getTeam() != this.team));
        this.targetSelector.a(4, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityInsentient)this, (Class)Silverfish.class, 20, true, false, silverfish -> silverfish.getTeam() != this.team));
    }
    
    public ITeam getTeam() {
        return this.team;
    }
    
    public static LivingEntity spawn(final Location location, final ITeam team, final double value, final double value2, final int i, final double value3) {
        final WorldServer handle = ((CraftWorld)location.getWorld()).getHandle();
        final Silverfish silverfish = new Silverfish((EntityTypes<? extends EntitySilverfish>)EntityTypes.SILVERFISH, (World)handle, team);
        silverfish.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        silverfish.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(value2);
        silverfish.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(value);
        silverfish.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(value3);
        silverfish.setPersistent();
        ((CraftLivingEntity)silverfish.getBukkitEntity()).setRemoveWhenFarAway(false);
        silverfish.setCustomNameVisible(true);
        handle.addEntity((Entity)silverfish, CreatureSpawnEvent.SpawnReason.CUSTOM);
        silverfish.getBukkitEntity().setCustomName(Language.getDefaultLanguage().m(Messages.SHOP_UTILITY_NPC_IRON_GOLEM_NAME).replace("{despawn}", String.valueOf(i).replace("{health}", StringUtils.repeat(Language.getDefaultLanguage().m(Messages.FORMATTING_DESPAWNABLE_UTILITY_NPC_HEALTH) + " ", 10)).replace("{TeamColor}", team.getColor().chat().toString())));
        return (LivingEntity)silverfish.getBukkitEntity();
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



package ws.billy.bedwars.support.version.v1_12_R1;

import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.IBlockState;
import org.bukkit.craftbukkit.v1_12_R1.event.CraftEventFactory;
import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.Blocks;
import java.util.Random;
import net.minecraft.server.v1_12_R1.BlockMonsterEggs;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.EnumDirection;
import net.minecraft.server.v1_12_R1.DamageSource;
import ws.billy.bedwars.support.version.common.VersionCommon;
import net.minecraft.server.v1_12_R1.WorldServer;
import net.minecraft.server.v1_12_R1.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.apache.commons.lang.StringUtils;
import ws.billy.bedwars.api.language.Messages;
import ws.billy.bedwars.api.language.Language;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import net.minecraft.server.v1_12_R1.GenericAttributes;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import java.lang.reflect.Field;
import net.minecraft.server.v1_12_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_12_R1.EntityHuman;
import net.minecraft.server.v1_12_R1.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_12_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_12_R1.EntityCreature;
import net.minecraft.server.v1_12_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_12_R1.PathfinderGoal;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.PathfinderGoalFloat;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_12_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_12_R1.World;
import ws.billy.bedwars.api.arena.team.ITeam;
import net.minecraft.server.v1_12_R1.EntitySilverfish;

public class Silverfish extends EntitySilverfish
{
    private ITeam team;
    
    public Silverfish(final World world, final ITeam team) {
        super(world);
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
        this.team = team;
        this.goalSelector.a(1, (PathfinderGoal)new PathfinderGoalFloat((EntityInsentient)this));
        this.goalSelector.a(2, (PathfinderGoal)new PathfinderGoalMeleeAttack((EntityCreature)this, 1.9, false));
        this.targetSelector.a(1, (PathfinderGoal)new PathfinderGoalHurtByTarget((EntityCreature)this, true, new Class[0]));
        this.goalSelector.a(3, (PathfinderGoal)new PathfinderGoalRandomStroll((EntityCreature)this, 2.0));
        this.targetSelector.a(2, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)EntityHuman.class, 20, true, false, o -> ((EntityHuman)o).isAlive() && !team.wasMember(((EntityHuman)o).getUniqueID()) && !team.getArena().isReSpawning(((EntityHuman)o).getUniqueID()) && !team.getArena().isSpectator(((EntityHuman)o).getUniqueID())));
        this.targetSelector.a(3, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)IGolem.class, 20, true, false, o -> ((IGolem)o).getTeam() != team));
        this.targetSelector.a(4, (PathfinderGoal)new PathfinderGoalNearestAttackableTarget((EntityCreature)this, (Class)Silverfish.class, 20, true, false, o -> ((Silverfish)o).getTeam() != team));
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
    
    static class PathfinderGoalSilverfishHideInBlock extends PathfinderGoalRandomStroll
    {
        private EnumDirection h;
        private boolean i;
        
        public PathfinderGoalSilverfishHideInBlock(final EntitySilverfish entitySilverfish) {
            super((EntityCreature)entitySilverfish, 1.0, 10);
            this.a(1);
        }
        
        public boolean a() {
            if (this.a.getGoalTarget() != null) {
                return false;
            }
            if (!this.a.getNavigation().o()) {
                return false;
            }
            final Random random = this.a.getRandom();
            if (this.a.world.getGameRules().getBoolean("mobGriefing") && random.nextInt(10) == 0) {
                this.h = EnumDirection.a(random);
                if (BlockMonsterEggs.x(this.a.world.getType(new BlockPosition(this.a.locX, this.a.locY + 0.5, this.a.locZ).shift(this.h)))) {
                    return this.i = true;
                }
            }
            this.i = false;
            return super.a();
        }
        
        public boolean b() {
            return !this.i && super.b();
        }
        
        public void c() {
            if (!this.i) {
                super.c();
            }
            else {
                final World world = this.a.world;
                final BlockPosition shift = new BlockPosition(this.a.locX, this.a.locY + 0.5, this.a.locZ).shift(this.h);
                final IBlockData type = world.getType(shift);
                if (BlockMonsterEggs.x(type)) {
                    if (CraftEventFactory.callEntityChangeBlockEvent((Entity)this.a, shift, Blocks.MONSTER_EGG, Block.getId(BlockMonsterEggs.getById(type.getBlock().toLegacyData(type)))).isCancelled()) {
                        return;
                    }
                    world.setTypeAndData(shift, Blocks.MONSTER_EGG.getBlockData().set((IBlockState)BlockMonsterEggs.VARIANT, (Comparable)BlockMonsterEggs.EnumMonsterEggVarient.a(type)), 3);
                    this.a.doSpawnEffect();
                    this.a.die();
                }
            }
        }
    }
}

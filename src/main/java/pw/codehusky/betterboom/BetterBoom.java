package pw.codehusky.betterboom;

import com.flowpowered.math.vector.Vector3d;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.FallingBlockData;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.FallingBlock;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lokio on 12/17/2016.
 */
@Plugin(id="betterboom", name="Better Boom", version = "1.0-beta", description = "Makes more realistic explosions!")
public class BetterBoom {
    @Inject
    private Logger logger;

    @Inject
    private PluginContainer pC;
    private Cause genericCause;
    @Listener
    public void onServerStart(GameStartedServerEvent event){
        logger.info("Started!");
        genericCause = Cause.of(NamedCause.of("PluginContainer",pC));
    }
    private float randRange(float range){
        return (float)((Math.random() * -1) + Math.random())*range;
    }

    @Listener
    public void onExplosion(ExplosionEvent.Detonate event){
        BlockType[] a = {
                BlockTypes.TALLGRASS,
                BlockTypes.CHORUS_FLOWER,
                BlockTypes.RED_FLOWER,
                BlockTypes.YELLOW_FLOWER,
                BlockTypes.BROWN_MUSHROOM,
                BlockTypes.RED_MUSHROOM,
                BlockTypes.POTATOES,
                BlockTypes.TNT,
                BlockTypes.DOUBLE_PLANT,
                BlockTypes.SNOW_LAYER,
                BlockTypes.SNOW,
                BlockTypes.LEAVES,
                BlockTypes.LEAVES2
        };
        ArrayList<BlockType> filtered =  new ArrayList<BlockType>(Arrays.asList(a));
        List<Location<World>> b1 = event.getAffectedLocations();
        for(Location<World> b : b1){
            BlockState exploded = b.getBlock();
            /*if(exploded.getType() == BlockTypes.COAL_ORE || exploded.getType() == BlockTypes.COAL_BLOCK) {

                b.removeBlock(genericCause);
                Explosion ourBoom = Explosion.builder().location(b).radius(6.9f).shouldBreakBlocks(true).shouldDamageEntities(true).build();
                event.getTargetWorld().triggerExplosion(ourBoom,genericCause);
            }else */if(!filtered.contains(exploded.getType()) && Math.random() * 100 > 25) {
                if(exploded.getType() == BlockTypes.GRASS||exploded.getType() == BlockTypes.GRASS_PATH||exploded.getType() == BlockTypes.MYCELIUM){
                    exploded = BlockState.builder().blockType(BlockTypes.DIRT).build();
                }
                if(exploded.getType() == BlockTypes.STONE && Math.random() * 100 > 40){
                    exploded = BlockState.builder().blockType(BlockTypes.COBBLESTONE).build();
                }
                World extent = b.getExtent();
                FallingBlock toFly = (FallingBlock) extent.createEntity(EntityTypes.FALLING_BLOCK, b.getPosition());
                toFly.setVelocity(new Vector3d(randRange(0.5f), 0.25 + Math.random() * 1, randRange(0.5f)));
                FallingBlockData fbd = toFly.getFallingBlockData();
                fbd.set(Keys.FALLING_BLOCK_STATE, exploded);
                fbd.set(Keys.FALL_TIME, 1);
                fbd.set(Keys.CAN_DROP_AS_ITEM,false);
                toFly.offer(fbd);

                extent.spawnEntity(toFly, genericCause);
            }
        }
    }
}

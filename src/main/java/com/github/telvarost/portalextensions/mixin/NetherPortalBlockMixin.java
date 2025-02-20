package com.github.telvarost.portalextensions.mixin;

import com.github.telvarost.portalextensions.Config;
import net.minecraft.block.Block;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin extends TranslucentBlock {

    public NetherPortalBlockMixin(int id, int textureId) {
        super(id, textureId, Material.NETHER_PORTAL, false);
    }

    @Override
    public Block setHardness(float hardness) {
        if (Config.config.portalBlocksRemainAndActLikeGlass) {
            hardness = 0.5F;
        } else {
            hardness = -1.0F;
        }

        this.hardness = hardness;
        if (this.resistance < hardness * 5.0F) {
            this.resistance = hardness * 5.0F;
        }

        return this;
    }

    @Override
    public void onPlaced(World world, int x, int y, int z) {
        if (Config.config.portalBlocksRemainAndActLikeGlass) {
            if (this.hardness != 0.5F) {
                this.hardness = 0.5F;
            }
        } else if (this.hardness != -1.0F) {
            this.hardness = -1.0F;
        }
    }

    @Inject(
            method = "create",
            at = @At("RETURN"),
            cancellable = true
    )
    public void create(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (  (  (Config.config.allowModernNetherPortalSizes)
              //|| (Config.config.allowSmallerNetherPortalSizes)
              )
           && (world.getBlockId(x, y, z) != this.id)
        ) {
            boolean isFrameComplete = false;
            boolean isAxisX = true;
            boolean isObsidianPositiveX = false;
            boolean isObsidianNegativeX = false;
            boolean isObsidianPositiveZ = false;
            boolean isObsidianNegativeZ = false;

            /** - Step 1: Find potential obsidian portal frame */

            int emptyBlockPositiveX = x;
            for(int offsetX = 1; offsetX <= 21; offsetX++) {
                emptyBlockPositiveX++;
                if (  (0             != world.getBlockId(x + offsetX, y, z))
                   && (Block.FIRE.id != world.getBlockId(x + offsetX, y, z))
                ) {
                    isObsidianPositiveX = Block.OBSIDIAN.id == world.getBlockId(emptyBlockPositiveX, y, z);
                    break;
                }
            }

            int emptyBlockNegativeX = x;
            for(int offsetX = 1; offsetX <= 21; offsetX++) {
                emptyBlockNegativeX--;
                if (  (0             != world.getBlockId(x - offsetX, y, z))
                   && (Block.FIRE.id != world.getBlockId(x - offsetX, y, z))
                ) {
                    isObsidianNegativeX = Block.OBSIDIAN.id == world.getBlockId(emptyBlockNegativeX, y, z);
                    break;
                }
            }

            int emptyBlockPositiveY = y;
            for(int offsetY = 1; offsetY <= 21; offsetY++) {
                emptyBlockPositiveY++;
                if (  (0             != world.getBlockId(x, y + offsetY, z))
                   && (Block.FIRE.id != world.getBlockId(x, y + offsetY, z))
                ) {
                    break;
                }
            }

            int emptyBlockNegativeY = y;
            for(int offsetY = 1; offsetY <= 21; offsetY++) {
                emptyBlockNegativeY--;
                if (  (0             != world.getBlockId(x, y - offsetY, z))
                   && (Block.FIRE.id != world.getBlockId(x, y - offsetY, z))
                ) {
                    break;
                }
            }

            int emptyBlockPositiveZ = z;
            for(int offsetZ = 1; offsetZ <= 21; offsetZ++) {
                emptyBlockPositiveZ++;
                if (  (0             != world.getBlockId(x, y, z + offsetZ))
                   && (Block.FIRE.id != world.getBlockId(x, y, z + offsetZ))
                ) {
                    isObsidianPositiveZ = Block.OBSIDIAN.id == world.getBlockId(x, y, emptyBlockPositiveZ);
                    break;
                }
            }

            int emptyBlockNegativeZ = z;
            for(int offsetZ = 1; offsetZ <= 21; offsetZ++) {
                emptyBlockNegativeZ--;
                if (  (0             != world.getBlockId(x, y, z - offsetZ))
                   && (Block.FIRE.id != world.getBlockId(x, y, z - offsetZ))
                ) {
                    isObsidianNegativeZ = Block.OBSIDIAN.id == world.getBlockId(x, y, emptyBlockNegativeZ);
                    break;
                }
            }

            /** - Step 2: Confirm obsidian portal frame is complete */

            int frameHeight = Math.abs(emptyBlockPositiveY - emptyBlockNegativeY) - 1;
            int frameWidth = 0;

            if (  isObsidianPositiveX
               && isObsidianNegativeX
               && isObsidianPositiveZ
               && isObsidianNegativeZ
            ) {
                isFrameComplete = true;

                for (int indexY = 0; indexY < frameHeight; ++indexY) {
                    /** - Check frame sides */
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(emptyBlockPositiveX, y + indexY, z));
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(emptyBlockNegativeX, y + indexY, z));
                }
                frameWidth = Math.abs(emptyBlockPositiveX - emptyBlockNegativeX) - 1;
                for (int indexX = 1; indexX < frameWidth; ++indexX) {
                    /** - Check frame top and bottom */
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(emptyBlockNegativeX + indexX, emptyBlockPositiveY, z));
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(emptyBlockNegativeX + indexX, emptyBlockNegativeY, z));
                }

                if (false == isFrameComplete) {
                    isFrameComplete = true;
                    isAxisX = false;

                    for (int indexY = 0; indexY < frameHeight; ++indexY) {
                        /** - Check frame sides */
                        isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(x, y + indexY, emptyBlockPositiveZ));
                        isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(x, y + indexY, emptyBlockNegativeZ));
                    }
                    frameWidth = Math.abs(emptyBlockPositiveZ - emptyBlockNegativeZ) - 1;
                    for (int indexZ = 1; indexZ < frameWidth; ++indexZ) {
                        /** - Check frame top and bottom */
                        isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(x, emptyBlockPositiveY, emptyBlockNegativeZ + indexZ));
                        isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(x, emptyBlockNegativeY, emptyBlockNegativeZ + indexZ));
                    }
                }
            } else if (  isObsidianPositiveX
                      && isObsidianNegativeX
            ) {
                isFrameComplete = true;

                for (int indexY = 0; indexY < frameHeight; ++indexY) {
                    /** - Check frame sides */
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(emptyBlockPositiveX, y + indexY, z));
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(emptyBlockNegativeX, y + indexY, z));
                }
                frameWidth = Math.abs(emptyBlockPositiveX - emptyBlockNegativeX) - 1;
                for (int indexX = 1; indexX < frameWidth; ++indexX) {
                    /** - Check frame top and bottom */
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(emptyBlockNegativeX + indexX, emptyBlockPositiveY, z));
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(emptyBlockNegativeX + indexX, emptyBlockNegativeY, z));
                }
            } else if (  isObsidianPositiveZ
                      && isObsidianNegativeZ
            ) {
                isFrameComplete = true;
                isAxisX = false;

                for (int indexY = 0; indexY < frameHeight; ++indexY) {
                    /** - Check frame sides */
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(x, y + indexY, emptyBlockPositiveZ));
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(x, y + indexY, emptyBlockNegativeZ));
                }
                frameWidth = Math.abs(emptyBlockPositiveZ - emptyBlockNegativeZ) - 1;
                for (int indexZ = 1; indexZ < frameWidth; ++indexZ) {
                    /** - Check frame top and bottom */
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(x, emptyBlockPositiveY, emptyBlockNegativeZ + indexZ));
                    isFrameComplete = isFrameComplete && (Block.OBSIDIAN.id == world.getBlockId(x, emptyBlockNegativeY, emptyBlockNegativeZ + indexZ));
                }
            }

            /** - Step 3: Build nether portal if frame eligible for it */

            if (isFrameComplete)
            {
                /** - Check minimum portal size */
                //if (false == Config.config.allowSmallerNetherPortalSizes) {
                    if ((frameWidth < 2) || (frameHeight < 3))
                    {
                        return;
                    }
                //}

                /** - Check maximum portal size */
                if (Config.config.allowModernNetherPortalSizes) {
                    if ((frameWidth > 21) || (frameHeight > 21))
                    {
                        return;
                    }
                } else {
                    if ((frameWidth > 2) || (frameHeight > 3))
                    {
                        return;
                    }
                }


                /** - Build portal */
                world.pauseTicking = true;
                for(int widthIndex = 1; widthIndex < (frameWidth + 1); ++widthIndex) {
                    for(int heightIndex = 0; heightIndex < frameHeight; ++heightIndex) {
                        if (isAxisX) {
                            int blockId = world.getBlockId(emptyBlockNegativeX + widthIndex, y + heightIndex, z);
                            if (  (0             == blockId)
                               || (Block.FIRE.id == blockId)
                            ) {
                                world.setBlock(emptyBlockNegativeX + widthIndex, y + heightIndex, z, Block.NETHER_PORTAL.id);
                            } else {
                                world.pauseTicking = false;
                                world.notifyNeighbors(emptyBlockNegativeX + widthIndex, y + heightIndex, z, blockId);
                                return;
                            }
                        } else {
                            int blockId = world.getBlockId(x, y + heightIndex, emptyBlockNegativeZ + widthIndex);
                            if (  (0             == blockId)
                               || (Block.FIRE.id == blockId)
                            ) {
                                world.setBlock(x, y + heightIndex, emptyBlockNegativeZ + widthIndex, Block.NETHER_PORTAL.id);
                            } else {
                                world.pauseTicking = false;
                                world.notifyNeighbors(x, y + heightIndex, emptyBlockNegativeZ + widthIndex, blockId);
                                return;
                            }
                        }
                    }
                }
                world.pauseTicking = false;

                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = "neighborUpdate",
            at = @At("HEAD"),
            cancellable = true
    )
    public void neighborUpdate(World world, int x, int y, int z, int id, CallbackInfo ci) {
        if (Config.config.portalBlocksRemainAndActLikeGlass) {
            ci.cancel();
            return;
        }

        if (  (Config.config.allowModernNetherPortalSizes)
           //|| (Config.config.allowSmallerNetherPortalSizes)
        ) {
            int maxPortalSize = 5;

            if (Config.config.allowModernNetherPortalSizes) {
                maxPortalSize = 23;
            }

            int lowestPortalBlockY;
            for(lowestPortalBlockY = y; world.getBlockId(x, lowestPortalBlockY - 1, z) == this.id; --lowestPortalBlockY) {
                /** - Find the lowest consecutive portal block along the Y axis */
            }

            if (world.getBlockId(x, lowestPortalBlockY - 1, z) != Block.OBSIDIAN.id) {
                /** - If the block below the lowest consecutive portal is not obsidian remove the portal block */
                world.setBlock(x, y, z, 0);
            } else {
                int highestPortalOffsetY;
                for(highestPortalOffsetY = 1;
                    highestPortalOffsetY < maxPortalSize
                            && world.getBlockId(x, lowestPortalBlockY + highestPortalOffsetY, z) == this.id;
                    ++highestPortalOffsetY
                ) {
                    /** - Find the offset of the highest consecutive portal block along the Y axis */
                }

                if (world.getBlockId(x, lowestPortalBlockY + highestPortalOffsetY, z) == Block.OBSIDIAN.id) {
                    boolean xAxisHasPortal = world.getBlockId(x - 1, y, z) == this.id
                            || world.getBlockId(x + 1, y, z) == this.id;

                    boolean zAxisHasPortal = world.getBlockId(x, y, z - 1) == this.id
                            || world.getBlockId(x, y, z + 1) == this.id;

                    if (xAxisHasPortal && zAxisHasPortal) {
                        /** - Portal axis interference, delete conflicting portal */
                        world.setBlock(x, y, z, 0);
                    } else {
                        /** - Check if portal is missing horizontal frame */
                        if (xAxisHasPortal) {
                            int lowestPortalBlockX;
                            for(lowestPortalBlockX = x; world.getBlockId(lowestPortalBlockX - 1, y, z) == this.id; --lowestPortalBlockX) {
                                /** - Find the lowest consecutive portal block along the X axis */
                            }

                            if (world.getBlockId(lowestPortalBlockX - 1, y, z) != Block.OBSIDIAN.id) {
                                /** - If the block below the lowest consecutive portal is not obsidian remove the portal block */
                                world.setBlock(x, y, z, 0);
                            } else {
                                int highestPortalOffsetX;
                                for(highestPortalOffsetX = 1;
                                    highestPortalOffsetX < maxPortalSize
                                            && world.getBlockId(lowestPortalBlockX + highestPortalOffsetX, y, z) == this.id;
                                    ++highestPortalOffsetX
                                ) {
                                    /** - Find the offset of the highest consecutive portal block along the X axis */
                                }

                                if (world.getBlockId(lowestPortalBlockX + highestPortalOffsetX, y, z) != Block.OBSIDIAN.id) {
                                    world.setBlock(x, y, z, 0);
                                }
                            }
                        } else if (zAxisHasPortal) {
                            int lowestPortalBlockZ;
                            for(lowestPortalBlockZ = z; world.getBlockId(x, y, lowestPortalBlockZ - 1) == this.id; --lowestPortalBlockZ) {
                                /** - Find the lowest consecutive portal block along the X axis */
                            }

                            if (world.getBlockId(x, y, lowestPortalBlockZ - 1) != Block.OBSIDIAN.id) {
                                /** - If the block below the lowest consecutive portal is not obsidian remove the portal block */
                                world.setBlock(x, y, z, 0);
                            } else {
                                int highestPortalOffsetZ;
                                for(highestPortalOffsetZ = 1;
                                    highestPortalOffsetZ < maxPortalSize
                                            && world.getBlockId(x, y, lowestPortalBlockZ + highestPortalOffsetZ) == this.id;
                                    ++highestPortalOffsetZ
                                ) {
                                    /** - Find the offset of the highest consecutive portal block along the X axis */
                                }

                                if (world.getBlockId(x, y, lowestPortalBlockZ + highestPortalOffsetZ) != Block.OBSIDIAN.id) {
                                    world.setBlock(x, y, z, 0);
                                }
                            }
                        } else {
                            boolean xAxisHasObsidian = world.getBlockId(x - 1, y, z) == Block.OBSIDIAN.id
                                    && world.getBlockId(x + 1, y, z) == Block.OBSIDIAN.id;

                            boolean zAxisHasObsidian = world.getBlockId(x, y, z - 1) == Block.OBSIDIAN.id
                                    && world.getBlockId(x, y, z + 1) == Block.OBSIDIAN.id;

                            if (!xAxisHasObsidian && !zAxisHasObsidian) {
                                /** - Portal of width 1, does not have obsidian on the sides anywhere */
                                world.setBlock(x, y, z, 0);
                            }
                        }
                    }
                } else {
                    /** - Distance between lowest and highest consecutive portal blocks is not 3
                     *    Or Obsidian is missing from the block above the highest consecutive portal block
                     */
                    world.setBlock(x, y, z, 0);
                }
            }
            ci.cancel();
        }
    }
}

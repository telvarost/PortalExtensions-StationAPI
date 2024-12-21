package com.github.telvarost.portalextensions.events.init;

import com.github.telvarost.portalextensions.Config;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.modificationstation.stationapi.api.event.recipe.RecipeRegisterEvent;
import net.modificationstation.stationapi.api.recipe.CraftingRegistry;
import net.modificationstation.stationapi.api.recipe.SmeltingRegistry;
import net.modificationstation.stationapi.api.util.Identifier;

public class RecipeListener {

    @EventListener
    public void registerRecipes(RecipeRegisterEvent event) {
        Identifier type = event.recipeId;

        if (type == RecipeRegisterEvent.Vanilla.CRAFTING_SHAPED.type()) {
            if (Config.config.enableNetherPortalRecipe) {
                CraftingRegistry.addShapedRecipe(new ItemStack(Block.NETHER_PORTAL.asItem(), 1), "XXX", "XYX", "XXX", 'X', Block.OBSIDIAN, 'Y', Block.FIRE);
            }
        }

        if (type == RecipeRegisterEvent.Vanilla.CRAFTING_SHAPELESS.type()) {
            if (Config.config.enableFireRecipe) {
                CraftingRegistry.addShapelessRecipe(new ItemStack(Block.FIRE.asItem(), 3), Item.FLINT_AND_STEEL);
            }
       }
    }
}
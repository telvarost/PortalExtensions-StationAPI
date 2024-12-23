package com.github.telvarost.portalextensions;

import net.glasslauncher.mods.gcapi3.api.*;

public class Config {

    @ConfigRoot(value = "config", visibleName = "PortalExtensions")
    public static ConfigFields config = new ConfigFields();

    public static class ConfigFields {

        @ConfigEntry(
                name = "Allow Modern Nether Portal Sizes",
                description = "Allows bigger nether portals than normal",
                multiplayerSynced = true
        )
        public Boolean allowModernNetherPortalSizes = true;

//        @ConfigEntry(
//                name = "Allow Smaller Nether Portal Sizes",
//                description = "Small nether portals can be created with fire",
//                multiplayerSynced = true
//        )
//        public Boolean allowSmallerNetherPortalSizes = false;

        @ConfigEntry(
                name = "Disable Nether Portal Disappearance",
                description = "Nether portals will act like glass",
                multiplayerSynced = true
        )
        public Boolean disableNetherPortalDisappearance = false;

        @ConfigEntry(
                name = "Enable Fire Crafting Recipe",
                description = "Restart required for changes to take effect",
                multiplayerSynced = true
        )
        public Boolean enableFireRecipe = false;

        @ConfigEntry(
                name = "Enable Nether Portal Crafting Recipe",
                description = "Restart required for changes to take effect",
                multiplayerSynced = true
        )
        public Boolean enableNetherPortalRecipe = false;
    }
}

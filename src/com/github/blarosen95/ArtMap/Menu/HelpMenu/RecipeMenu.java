package com.github.blarosen95.ArtMap.Menu.HelpMenu;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.Config.Lang;
import com.github.blarosen95.ArtMap.Menu.API.BasicMenu;
import com.github.blarosen95.ArtMap.Menu.API.ChildMenu;
import com.github.blarosen95.ArtMap.Menu.Button.Button;
import com.github.blarosen95.ArtMap.Menu.Button.CloseButton;
import com.github.blarosen95.ArtMap.Menu.Button.StaticButton;
import com.github.blarosen95.ArtMap.Menu.Handler.CacheableMenu;
import com.github.blarosen95.ArtMap.Recipe.ArtMaterial;
import com.github.blarosen95.ArtMap.Utils.ItemUtils;
import com.github.blarosen95.ArtMap.Utils.VersionHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RecipeMenu extends BasicMenu implements ChildMenu {

    private boolean adminMenu;
    private boolean version_1_12 = ArtMap.getBukkitVersion().getVersion().isEqualTo(VersionHandler.BukkitVersion.v1_12);

    public RecipeMenu(boolean adminMenu) {
        super(ChatColor.DARK_BLUE + Lang.MENU_RECIPE.get(), InventoryType.HOPPER);
        this.adminMenu = adminMenu;
    }

    @Override
    public Button[] getButtons() {
        return new Button[]{
                new StaticButton(Material.SIGN, Lang.Array.INFO_RECIPES.get()),
                new RecipeButton(ArtMaterial.EASEL),
                new RecipeButton(ArtMaterial.CANVAS),
                new RecipeButton(ArtMaterial.PAINT_BUCKET),
                new CloseButton()
        };
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.getMenuHandler().MENU.HELP.get(viewer);
    }


    private class RecipeButton extends Button {

        final ArtMaterial recipe;

        public RecipeButton(ArtMaterial material) {
            super(material.getType());
            this.recipe = material;
            ItemMeta meta = material.getItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.set(lore.size() - 1, ChatColor.GREEN + Lang.RECIPE_BUTTON.get());
            if (adminMenu) lore.add(lore.size(), ChatColor.GOLD + Lang.ADMIN_RECIPE.get());
            meta.setLore(lore);
            setItemMeta(meta);
        }

        @Override
        public void onClick(Player player, ClickType clickType) {
            if (adminMenu) {
                if (clickType == ClickType.LEFT) {
                    openRecipePreview(player);
                } else if (clickType == ClickType.RIGHT) {
                    ArtMap.getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, recipe.getItem()));
                }
            } else {
                openRecipePreview(player);
            }
        }

        private void openRecipePreview(Player player) {
            if (version_1_12) {
                ArtMap.getMenuHandler().openMenu(player, new RecipePreview_1_12(recipe));
            } else {
                ArtMap.getMenuHandler().openMenu(player, new RecipePreview(recipe));
            }
        }
    }
}

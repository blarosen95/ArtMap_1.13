package com.github.blarosen95.ArtMap;

import com.github.blarosen95.ArtMap.Colour.BasicPalette;
import com.github.blarosen95.ArtMap.Colour.Palette;
import com.github.blarosen95.ArtMap.Command.CommandHandler;
import com.github.blarosen95.ArtMap.Compatability.CompatibilityManager;
import com.github.blarosen95.ArtMap.Config.Configuration;
import com.github.blarosen95.ArtMap.Config.Lang;
import com.github.blarosen95.ArtMap.Easel.EaselMap;
import com.github.blarosen95.ArtMap.IO.Database.Database;
import com.github.blarosen95.ArtMap.IO.ErrorLogger;
import com.github.blarosen95.ArtMap.IO.Legacy.OldDatabaseConverter;
import com.github.blarosen95.ArtMap.IO.PixelTableManager;
import com.github.blarosen95.ArtMap.IO.Protocol.Channel.ChannelCacheManager;
import com.github.blarosen95.ArtMap.IO.Protocol.ProtocolHandler;
import com.github.blarosen95.ArtMap.Listeners.EventManager;
import com.github.blarosen95.ArtMap.Menu.Handler.MenuHandler;
import com.github.blarosen95.ArtMap.Painting.ArtistHandler;
import com.github.blarosen95.ArtMap.Preview.PreviewManager;
import com.github.blarosen95.ArtMap.Recipe.RecipeLoader;
import com.github.blarosen95.ArtMap.Utils.Scheduler;
import com.github.blarosen95.ArtMap.Utils.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ArtMap extends JavaPlugin {

    private static SoftReference<ArtMap> pluginInstance = null;
    private MenuHandler menuHandler;
    private ArtistHandler artistHandler;
    private VersionHandler bukkitVersion;
    private Scheduler scheduler;
    private Database database;
    private ChannelCacheManager cacheManager;
    private RecipeLoader recipeLoader;
    private CompatibilityManager compatManager;
    private ProtocolHandler protocolHandler;
    private PixelTableManager pixelTable;
    private Configuration config;
    private EventManager eventManager;
    private PreviewManager previewManager;
    private EaselMap easels;
    private Palette dyePalette;
    private boolean recipesLoaded = false;
    private boolean disabled;

    public static Database getArtDatabase() {
        return instance().database;
    }

    public static ArtMap instance() {
        if (pluginInstance == null || pluginInstance.get() == null) {
            pluginInstance = new SoftReference<>((ArtMap) Bukkit.getPluginManager().getPlugin("ArtMap"));
        }
        return pluginInstance.get();
    }

    public static Scheduler getScheduler() {
        return instance().scheduler;
    }

    public static ArtistHandler getArtistHandler() {
        return instance().artistHandler;
    }

    public static VersionHandler getBukkitVersion() {
        return instance().bukkitVersion;
    }

    public static ChannelCacheManager getCacheManager() {
        return instance().cacheManager;
    }

    public static RecipeLoader getRecipeLoader() {
        return instance().recipeLoader;
    }

    public static CompatibilityManager getCompatManager() {
        return instance().compatManager;
    }

    public static MenuHandler getMenuHandler() {
        return instance().menuHandler;
    }

    public static Configuration getConfiguration() {
        return instance().config;
    }

    public static ProtocolHandler getProtocolManager() {
        return instance().protocolHandler;
    }

    public static Palette getDyePalette() {
        return instance().dyePalette;
    }

    public static PreviewManager getPreviewManager() {
        return instance().previewManager;
    }

    public static EaselMap getEasels() {
        return instance().easels;
    }

    public static PixelTableManager getPixelTable() {
        return instance().pixelTable;
    }

    public static boolean isDisabled() {
        return instance().disabled;
    }

    public void setColourPalette(Palette palette) {
        this.dyePalette = palette;
    }

    @Override
    public void onEnable() {
        pluginInstance = new SoftReference<>(this);
        saveDefaultConfig();
        compatManager = new CompatibilityManager(this);
        config = new Configuration(this, compatManager);
        scheduler = new Scheduler(this);
        bukkitVersion = new VersionHandler();
        protocolHandler = new ProtocolHandler();
        artistHandler = new ArtistHandler();
        cacheManager = new ChannelCacheManager();
        Lang.load(this, config);
        dyePalette = new BasicPalette();
        if ((database = Database.build(this)) == null) {
            getPluginLoader().disablePlugin(this);
            return;
        }
        new OldDatabaseConverter(this).convertDatabase();
        if ((pixelTable = PixelTableManager.buildTables(this)) == null) {
            getLogger().warning(Lang.INVALID_DATA_TABLES.get());
            getPluginLoader().disablePlugin(this);
            return;
        }
        if (!recipesLoaded) {
            recipeLoader = new RecipeLoader(this, config);
            recipeLoader.loadRecipes();
            recipesLoaded = true;
        }
        easels = new EaselMap();
        eventManager = new EventManager(this, bukkitVersion);
        previewManager = new PreviewManager();
        menuHandler = new MenuHandler(this);
        getCommand("artmap").setExecutor(new CommandHandler());
        disabled = false;
    }

    @Override
    public void onDisable() {
        disabled = true;
        previewManager.endAllPreviews();
        artistHandler.stop();
        menuHandler.closeAll();
        eventManager.unregisterAll();
        database.close();
//        recipeLoader.unloadRecipes();
        reloadConfig();
        pluginInstance = null;
    }

    public boolean writeResource(String resourcePath, File destination) {
        String writeError = String.format("Cannot write resource '%s' to destination '%s'.",
                resourcePath, destination.getAbsolutePath());
        if (!destination.exists()) try {
            if (destination.createNewFile()) {
                Files.copy(getResource(resourcePath), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else {
                getLogger().warning(writeError + " Error: Destination cannot be created.");
            }
        } catch (IOException e) {
            ErrorLogger.log(e, writeError);
            return false;
        }
        return true;
    }

    public Reader getTextResourceFile(String fileName) {
        return getTextResource(fileName);
    }
}
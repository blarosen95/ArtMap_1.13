package com.github.blarosen95.ArtMap.IO.Database;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.IO.CompressedMap;
import com.github.blarosen95.ArtMap.IO.ErrorLogger;
import com.github.blarosen95.ArtMap.Painting.GenericMapRenderer;
import com.github.blarosen95.ArtMap.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class Map {

    public static final byte[] BLANK_MAP = getBlankMap();

    private final short mapId;
    private MapView mapView;

    public Map(short mapId) {
        this.mapId = mapId;
        this.mapView = null;
    }

    public Map(MapView mapView) {
        this.mapId = mapView.getId();
        this.mapView = mapView;
    }

    private static byte[] getBlankMap() {
        byte[] mapOutput = new byte[Size.MAX.value];
        Arrays.fill(mapOutput, ArtMap.getDyePalette().getDefaultColour().getColour());
        return mapOutput;
    }

    public static File getMapDataFolder() {
        String pluginDir = ArtMap.instance().getDataFolder().getParentFile().getAbsolutePath();
        String rootDir = pluginDir.substring(0, pluginDir.lastIndexOf(File.separator));
        return new File(rootDir + File.separator + ArtMap.getConfiguration().WORLD + File.separator + "data");                               //Navigate to this world's data folder);
    }

    public static short getNextMapId() {
        short nextMapId = -1;
        File file = new File(getMapDataFolder(), "idcounts.dat");
        if (file.exists()) {
            byte[] data = new byte[((int) file.length())];
            try {
                FileInputStream inputStream = new FileInputStream(file);
                inputStream.read(data);
                inputStream.close();
            } catch (IOException e) {
                ErrorLogger.log(e, "Error reading idcounts.dat.");
                return -1;
            }
            nextMapId = (short) (data[9] << 8 | data[10] & 0xFF);//The short is stored at index 9-10
        }
        return nextMapId;
    }

    public CompressedMap compress() {
        return CompressedMap.compress(getMap());
    }

    public byte[] readData() {
        return Reflection.getMap(getMap());
    }

    public void setRenderer(MapRenderer renderer) {
        MapView mapView = getMap();
        mapView.getRenderers().forEach(mapView::removeRenderer);
        if (renderer != null) mapView.addRenderer(renderer);
    }

    public Map cloneMap() {
        MapView newMapView = Bukkit.getServer().createMap(Bukkit.getWorld(ArtMap.getConfiguration().WORLD));
        Map newMap = new Map(newMapView);
        byte[] mapData = readData();
        newMap.setMap(mapData);
        ArtMap.getArtDatabase().cacheMap(newMap, mapData);
        return newMap;
    }

    public MapView getMap() {
        return Bukkit.getMap(mapId);
    }

    public void setMap(byte[] map) {
        setMap(map, true);
    }

    public void setMap(byte[] map, boolean updateRenderer) {
        MapView mapView = getMapView();
        Reflection.setWorldMap(mapView, map);
        if (updateRenderer) setRenderer(new GenericMapRenderer(map));//todo sync?
    }

    public boolean exists() {
        return getMap() != null;
    }

    public File getDataFile() {
        return new File(getMapDataFolder(), "map_" + mapId + ".dat");
    }

    public void update(Player player) {
        ArtMap.getScheduler().runSafely(() -> player.sendMap(getMap()));
    }

    public short getMapId() {
        return mapId;
    }

    private MapView getMapView() {
        //todo We probably don't need sophisticated mapView caching right now
        return (mapView != null) ? mapView :
                (mapView = ArtMap.getScheduler().callSync(() -> Bukkit.getMap(mapId)));
    }

    public enum Size {
        MAX(128 * 128), STANDARD(32 * 32);
        public final int value;

        Size(int length) {
            this.value = length;
        }

        public int size() {
            return value;
        }
    }
}

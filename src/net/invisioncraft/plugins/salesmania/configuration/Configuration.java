/*
This file is part of Salesmania.

    Salesmania is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Salesmania is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Salesmania.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.invisioncraft.plugins.salesmania.configuration;

import net.invisioncraft.plugins.salesmania.Salesmania;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.logging.Level;

public class Configuration {
    protected FileConfiguration config;
    private File customConfigFile;
    private Salesmania plugin;
    private String filename;
    private HashSet<ConfigurationHandler> handlers;

    public Configuration(Salesmania plugin, String filename) {
        this.plugin = plugin;
        this.filename = filename;
        handlers = new HashSet<ConfigurationHandler>();
        plugin.registerConfig(this);
        reload();
        save();
    }

    public String getFilename() {
        return filename;
    }

    public void reload() {
        if(customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), filename);
        }
        config = YamlConfiguration.loadConfiguration(customConfigFile);

        InputStream defaultConfigStream = plugin.getResource(filename);
        if(defaultConfigStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
            config.setDefaults(defaultConfig);
            config.options().copyDefaults(true);
        }
        for(ConfigurationHandler handler : handlers) {
            handler.update();
        }
    }

    public FileConfiguration getConfig() {
        if(config == null) reload();
        return config;
    }

    public void save() {
        if (config == null || customConfigFile == null) {
            return;
        }
        try {
            config.save(customConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }

    public void registerHandler(ConfigurationHandler handler) {
        handlers.add(handler);
    }

    public Salesmania getPlugin() {
        return plugin;
    }

}

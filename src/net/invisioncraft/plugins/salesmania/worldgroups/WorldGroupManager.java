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

package net.invisioncraft.plugins.salesmania.worldgroups;

import net.invisioncraft.plugins.salesmania.Salesmania;
import net.invisioncraft.plugins.salesmania.configuration.ConfigurationHandler;
import net.invisioncraft.plugins.salesmania.configuration.GroupCache;
import net.invisioncraft.plugins.salesmania.configuration.WorldGroupSettings;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class WorldGroupManager implements ConfigurationHandler {
    private WorldGroupSettings worldGroupSettings;
    private Salesmania plugin;
    private ArrayList<WorldGroup> worldGroups;
    private GroupCache cache;

    public WorldGroupManager(Salesmania plugin) {
        this.plugin = plugin;
        cache = new GroupCache(plugin);
        plugin.getSettings().registerHandler(this);
        worldGroupSettings = plugin.getSettings().getWorldGroupSettings();
        worldGroups = worldGroupSettings.parseGroups(); // TODO <---
        update();
    }

    // TODO more elegant reloading of world groups
    @Override
    public void update() {
        for(WorldGroup worldGroup : worldGroups) {
            plugin.getSettings().getAuctionQueueSettings().loadQueue(worldGroup.getAuctionQueue(), worldGroup);
        }
    }

    public ArrayList<WorldGroup> getWorldGroups() {
        return worldGroups;
    }

    public WorldGroup getGroup(Player player) {
        for(WorldGroup worldGroup : worldGroups) {
            if(worldGroup.hasPlayer(player)) return worldGroup;
        }
        return null;
    }

    public WorldGroup getGroup(OfflinePlayer player) {
        if(player.isOnline()) return getGroup(player.getPlayer());
        World world = cache.loadPlayer(player);
        for(WorldGroup worldGroup : worldGroups) {
           if(worldGroup.getWorlds().contains(world)) return worldGroup;
        }
        return null;
    }

    public GroupCache getCache() {
        return cache;
    }
}

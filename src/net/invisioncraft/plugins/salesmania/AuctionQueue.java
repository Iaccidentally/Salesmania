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

package net.invisioncraft.plugins.salesmania;

import net.invisioncraft.plugins.salesmania.configuration.AuctionQueueSettings;
import net.invisioncraft.plugins.salesmania.configuration.AuctionSettings;
import net.invisioncraft.plugins.salesmania.configuration.ConfigurationHandler;
import net.invisioncraft.plugins.salesmania.event.AuctionEvent;
import net.invisioncraft.plugins.salesmania.worldgroups.WorldGroup;
import org.bukkit.entity.Player;

import java.util.*;

public class AuctionQueue extends LinkedList<Auction> {
    private Salesmania plugin;
    private AuctionQueueSettings queueConfig;

    private boolean isRunning = false;
    private boolean isCooldown = false;
    private long cooldownRemaining;

    private Auction currentAuction = null;
    private static long TICKS_PER_SECOND = 20;
    private Integer timerID;

    AuctionSettings auctionConfig;

    public AuctionQueue(Salesmania plugin, WorldGroup worldGroup) {
        this.plugin = plugin;
        queueConfig = plugin.getSettings().getAuctionQueueSettings();
        auctionConfig = plugin.getSettings().getAuctionSettings();
        queueConfig.loadQueue(this, worldGroup);
        start();
    }

    // TODO queues can be slightly optimized by ticking once instead of once per world group
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(currentAuction == null) {
                if(size() == 0) stop();
            }
            else {
                if(isCooldown) {
                    if(cooldownRemaining > 0) {
                        cooldownRemaining--;
                    } else {
                        isCooldown = false;
                    }
                }

                else if(currentAuction.isRunning()) {
                    currentAuction.timerTick();
                } else {
                    nextAuction();
                }
            }
        }
    };

    public int playerSize(Player player) {
        int count = 0;
        for(Auction auction : this) {
            if(auction.getOwner().equals(player)) count++;
        }
        return count;
    }

    public void update() {
        queueConfig.update(currentAuction, currentAuction.getWorldGroup());
    }

    public boolean nextAuction() {
        if(size() != 0) {
            currentAuction = peek();
            if(!isCooldown) currentAuction.start();
        } else {
            currentAuction = null;
            return false;
        }
        return true;
    }

    public void startCooldown() {
        cooldownRemaining = plugin.getSettings().getAuctionSettings().getCooldown();
        isCooldown = true;
    }

    public boolean isCooldown() {
        return isCooldown;
    }

    public void start() {
        if(!isRunning) {
            if(size() != 0) {
                isRunning = true;
                plugin.getServer().getPluginManager().callEvent(new AuctionEvent(null, AuctionEvent.EventType.QUEUE_STARTED));
                currentAuction = peek();
                currentAuction.start();
                timerID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, timerRunnable, TICKS_PER_SECOND, TICKS_PER_SECOND);
            }
        }
    }

    public void stop() {
        if(isRunning) {
            plugin.getServer().getScheduler().cancelTask(timerID);
            plugin.getServer().getPluginManager().callEvent(new AuctionEvent(null, AuctionEvent.EventType.QUEUE_STOPPED));
            isRunning = false;
        }
    }

    public Auction getCurrentAuction() {
        return currentAuction;
    }

    @Override
    public boolean add(Auction auction) {
        if(super.add(auction)) {
            queueConfig.saveAuction(auction, auction.getWorldGroup());
            plugin.getServer().getPluginManager().callEvent(new AuctionEvent(auction, AuctionEvent.EventType.QUEUED));
            if(auctionConfig.getEnabled() && !isRunning) {
                start();
            }
            return true;
        }
        return false;
    }

    // TODO for now, it's only possible to remove the first auction in the queue
    @Override
    public Auction remove() {
        Auction auction = super.remove();
        queueConfig.removeAuction(0, auction.getWorldGroup());
        return auction;
    }
}

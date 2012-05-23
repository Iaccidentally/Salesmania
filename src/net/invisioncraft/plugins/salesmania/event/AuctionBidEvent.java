package net.invisioncraft.plugins.salesmania.event;

import net.invisioncraft.plugins.salesmania.Auction;
import org.bukkit.entity.Player;

/**
 * Owner: Justin
 * Date: 5/23/12
 * Time: 2:29 AM
 */
public class AuctionBidEvent {
    Player lastWinner;
    long lastBid;
    Player winner;
    long bid;
    public AuctionBidEvent(Auction auction, Player winner, long bid) {
        lastWinner = auction.getWinner();
        lastBid = auction.getCurrentBid();
        this.winner = winner;
        this.bid = bid;
    }

    public Player getLastWinner() {
        return lastWinner;
    }

    public long getLastBid() {
        return lastBid;
    }

    public Player getWinner() {
        return winner;
    }

    public long getBid() {
        return bid;
    }
}

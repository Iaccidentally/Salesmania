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

package net.invisioncraft.plugins.salesmania.commands.salesmania;

import net.invisioncraft.plugins.salesmania.Salesmania;
import net.invisioncraft.plugins.salesmania.commands.CommandHandler;
import net.invisioncraft.plugins.salesmania.configuration.Locale;
import net.invisioncraft.plugins.salesmania.configuration.LocaleSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LocaleCommand extends CommandHandler {
    LocaleSettings localeSettings;
    enum LocaleCommands {
        LIST,
        SET
    }

    public LocaleCommand(Salesmania plugin) {
        super(plugin);
        localeSettings = plugin.getSettings().getLocaleSettings();
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        Locale locale = localeHandler.getLocale(sender);

        LocaleCommands localeCommand;
        if(!(sender instanceof Player)) {
            sender.sendMessage((locale.getMessage("Auction.Console.cantConsole")));
            return false;
        }
        // Syntax
        if(args.length < 2) {
            ArrayList<String> messageList = locale.getMessageList("Syntax.Salesmania.salesmania");
            sender.sendMessage(messageList.toArray(new String[messageList.size()]));
            return false;
        }
        try {
            localeCommand = LocaleCommands.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException ex) {
            ArrayList<String> messageList = locale.getMessageList("Syntax.Salesmania.salesmania");
            sender.sendMessage(messageList.toArray(new String[messageList.size()]));
            return false;
        }

        Player player = (Player) sender;
        switch(localeCommand) {
            case LIST:
                String localeList = "";
                for (String localeName : localeSettings.getLocales()) {
                    localeList = localeList.concat(localeName + " ");
                }
                sender.sendMessage(String.format(
                    locale.getMessage("Locale.available"),
                    localeList));
                break;
            case SET:
                if(args.length < 3) {
                    ArrayList<String> messageList = locale.getMessageList("Syntax.Salesmania.salesmania");
                    sender.sendMessage(messageList.toArray(new String[messageList.size()]));
                    return false;
                }
                if(localeHandler.setLocale(player, args[2])) {
                    locale = plugin.getLocaleHandler().getLocale(sender);
                    sender.sendMessage(String.format(
                            locale.getMessage("Locale.changed"),
                            locale.getName()));
                }
                else {
                    sender.sendMessage(String.format(
                            locale.getMessage("Locale.notFound"),
                            args[2]));
                }
                break;
            default:
                return false;
        }
        return true;
    }
}

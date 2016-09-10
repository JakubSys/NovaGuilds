/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.command.guild;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.command.abstractexecutor.AbstractCommandExecutor;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.manager.PlayerManager;
import co.marcin.novaguilds.util.TabUtils;
import co.marcin.novaguilds.util.TagUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class CommandGuildLeader extends AbstractCommandExecutor {
	private static final Command command = Command.GUILD_LEADER;

	public CommandGuildLeader() {
		super(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) throws Exception {
		if(args.length != 1) {
			Message.CHAT_PLAYER_ENTERNAME.send(sender);
			return;
		}

		NovaPlayer nPlayer = PlayerManager.getPlayer(sender);
		NovaPlayer newLeader = PlayerManager.getPlayer(args[0]);

		if(newLeader == null) {
			Message.CHAT_PLAYER_NOTEXISTS.send(sender);
			return;
		}

		if(!nPlayer.hasGuild()) {
			Message.CHAT_GUILD_NOTINGUILD.send(sender);
			return;
		}

		NovaGuild guild = nPlayer.getGuild();

		if(!nPlayer.isLeader()) {
			Message.CHAT_GUILD_NOTLEADER.send(sender);
			return;
		}

		if(newLeader.equals(nPlayer)) {
			Message.CHAT_GUILD_LEADER_SAMENICK.send(sender);
			return;
		}

		if(!newLeader.hasGuild() || !guild.isMember(newLeader)) {
			Message.CHAT_GUILD_LEADER_NOTSAMEGUILD.send(sender);
			return;
		}

		guild.getLeader().cancelToolProgress();

		//set guild leader
		guild.setLeader(newLeader);
		plugin.getGuildManager().save(guild);

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.PLAYERNAME, newLeader.getName());
		vars.put(VarKey.GUILDNAME, guild.getName());
		Message.CHAT_GUILD_LEADER_SUCCESS.vars(vars).send(sender);
		Message.BROADCAST_GUILD_SETLEADER.vars(vars).broadcast();

		//Tab and tags
		TagUtils.refresh();
		TabUtils.refresh();
	}
}

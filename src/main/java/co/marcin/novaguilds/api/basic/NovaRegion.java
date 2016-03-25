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

package co.marcin.novaguilds.api.basic;

import co.marcin.novaguilds.api.util.Addable;
import co.marcin.novaguilds.api.util.Changeable;
import org.bukkit.Location;
import org.bukkit.World;

public interface NovaRegion extends Changeable, Addable {
	/**
	 * Gets the world
	 *
	 * @return the world
	 */
	World getWorld();

	/**
	 * Gets the ID
	 *
	 * @return the ID
	 */
	int getId();

	/**
	 * Gets the guild
	 *
	 * @return the guild
	 */
	NovaGuild getGuild();

	/**
	 * Gets the location of one of the corners
	 *
	 * @param index corner index (0/1)
	 * @return the location
	 */
	Location getCorner(int index);

	/**
	 * Gets region's width
	 *
	 * @return size in blocks
	 */
	int getWidth();

	/**
	 * Gets region's height
	 *
	 * @return size in blocks
	 */
	int getHeight();

	/**
	 * Gets region's diagonal
	 *
	 * @return size in blocks
	 */
	int getDiagonal();

	/**
	 * Gets region's surface
	 *
	 * @return size in blocks
	 */
	int getSurface();

	/**
	 * Gets center location
	 *
	 * @return the location
	 */
	Location getCenter();

	/**
	 * Sets the world
	 *
	 * @param world the world
	 */
	void setWorld(World world);

	/**
	 * Sets the ID
	 *
	 * @param id the ID
	 */
	void setId(int id);

	/**
	 * Sets the guild
	 *
	 * @param guild the guild
	 */
	void setGuild(NovaGuild guild);

	/**
	 * Sets corner location
	 *
	 * @param index    corner index (0/1)
	 * @param location the location
	 */
	void setCorner(int index, Location location);
}

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

package co.marcin.novaguilds.api.util.exceptionparser;

import java.util.Collection;

public interface IError {
	/**
	 * Gets the signature of an error
	 *
	 * @return error signature
	 */
	ErrorSignature getSignature();

	/**
	 * Gets the exception that caused the error
	 *
	 * @return the exception
	 */
	Throwable getException();

	/**
	 * Gets causes of the exception
	 *
	 * @return sorted collection with causes
	 */
	Collection<Throwable> getCauses();

	/**
	 * Gets parsed console output
	 *
	 * @return console output string
	 */
	Collection<String> getConsoleOutput();
}

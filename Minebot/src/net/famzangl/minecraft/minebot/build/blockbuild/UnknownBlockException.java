/*******************************************************************************
 * This file is part of Minebot.
 *
 * Minebot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Minebot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Minebot.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.famzangl.minecraft.minebot.build.blockbuild;

public class UnknownBlockException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3534869401465171196L;

	public UnknownBlockException() {
		super();
	}

	public UnknownBlockException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownBlockException(String message) {
		super(message);
	}

	public UnknownBlockException(Throwable cause) {
		super(cause);
	}

}

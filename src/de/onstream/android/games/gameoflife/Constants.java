/***
* This program implements a very basic version of John Horton
* Conway's cellular automaton called "game of life" also known
* as "Conway's Game of Life". If you want to know more about it
* you should check out the Wikipedia page:
*
* http://en.wikipedia.org/wiki/Conway's_Game_of_Life
*
* The basic rules are as follows (quoted from above's article):
*
* 1. Any live cell with fewer than two live neighbours dies, as if caused by underpopulation.
* 2. Any live cell with more than three live neighbours dies, as if by overcrowding.
* 3. Any live cell with two or three live neighbours lives on to the next generation.
* 4. Any dead cell with exactly three live neighbours becomes a live cell.
*
* Have fun with the code.
*
* Daniel Gollub, 2010-06-11
*
* Copyright (C) 2010 Daniel Gollub, daniel.gollub@onstreamtv.de
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
*/

package de.onstream.android.games.gameoflife;



public class Constants
{
    public static final String NAME = "gameoflife";
}

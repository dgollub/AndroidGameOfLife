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

import android.os.Handler;
import android.os.Message;


//Custom EventHandler ... not the best design
public class GameViewEventHandler extends Handler {

    public static final int GAME_STOPPED    = 0;
    public static final int GAME_STARTED    = 1;
    public static final int GAME_LOOP       = 2;

    private GameView gv;

    public GameViewEventHandler(GameView gv) {
        this.gv = gv;
    }

    // Process the received messages
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
        case GAME_STOPPED:
            if (gv.getOnGameStoppedListener() != null)
                gv.getOnGameStoppedListener().onGameStopped(gv);
            break;
        case GAME_STARTED:
            if (gv.getOnGameStartedListener() != null)
                gv.getOnGameStartedListener().onGameStarted(gv);
            break;
        case GAME_LOOP:
            if (gv.getOnGameLoopListener() != null)
                gv.getOnGameLoopListener().onGameLoop(gv);
            break;
        default:
            break;
        }
    }

    public interface OnGameStoppedListener {
		void onGameStopped(GameView gv);
	}
    public interface OnGameStartedListener {
		void onGameStarted(GameView gv);
	}
    public interface OnGameLoopListener {
		void onGameLoop(GameView gv);
	}
}

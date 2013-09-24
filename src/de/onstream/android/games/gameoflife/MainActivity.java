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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
//import android.util.Log;

public class MainActivity extends Activity implements GameViewEventHandler.OnGameLoopListener,
                                                      GameViewEventHandler.OnGameStartedListener,
                                                      GameViewEventHandler.OnGameStoppedListener
{
    GameView gv;
    TextView lblInfo;

    private boolean done = false; //only init once

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);

        lblInfo = (TextView)findViewById(R.id.lblInfo);
        lblInfo.setText("Generation: 0");

        gv = new GameView(this);
        gv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        gv.setVisibility(View.VISIBLE);
        
        gv.setOnGameLoopListener(this);
        gv.setOnGameStartedListener(this);
        gv.setOnGameStoppedListener(this);

        final LinearLayout ll = (LinearLayout)findViewById(R.id.imageViewLayout);
        ll.addView(gv);

        final Button btnStart = (Button)findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                gv.gameStart();
            }
        });
        final Button btnStop = (Button)findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                gv.gameStop();
            }
        });
        final Button btnReset = (Button)findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                gv.gameReset();
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        int w = gv.getWidth();
        int h = gv.getHeight();

        if (hasFocus) { //This is the only event we can be sure that the GameView has it's final height & width assigned!
            if (!done) {
                done = true;
                gv.init(w, h);
            }
        }
    }

    public void onGameLoop(GameView gv) {
        lblInfo.setText(String.format("Generation: %d", gv.getGeneration()));
    }

    public void onGameStarted(GameView gv) {
        lblInfo.setText("Generation: 0");
    }

    public void onGameStopped(GameView gv) {
        lblInfo.setText(String.format("Generation: %d - Game stopped", gv.getGeneration()));
    }
}

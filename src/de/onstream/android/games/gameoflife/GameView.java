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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.os.Message;

import de.onstream.android.games.gameoflife.GameViewEventHandler.OnGameLoopListener;
import de.onstream.android.games.gameoflife.GameViewEventHandler.OnGameStartedListener;
import de.onstream.android.games.gameoflife.GameViewEventHandler.OnGameStoppedListener;

public class GameView extends ImageView
{

    //width, height
    private static final int MAX_WIDTH  = 16;
    private static final int MAX_HEIGHT = 20;

    private int[][] stateA = new int[MAX_WIDTH][MAX_HEIGHT]; //Could we use byte instead of int to fasten things up?
    private int[][] stateB = new int[MAX_WIDTH][MAX_HEIGHT]; //buffer, in which we enter the new state of each cell each turn

    private Bitmap fieldBg;
    private Bitmap field;
    private Canvas canvas;

    private boolean running = false;
    private int generation = 0;
    private int wPxPerField = 0;
    private int hPxPerField = 0;

    Paint paint = new Paint();
    Paint fillAlive = new Paint();
    Paint fillDead  = new Paint();

    private static final int DEAD  = 0;
    private static final int ALIVE = 1;

    GameViewEventHandler eventHandler = new GameViewEventHandler(this);

	public GameView(Context context) {
		super(context);

        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.DKGRAY);
        fillAlive.setStyle(Paint.Style.FILL);
        fillAlive.setColor(Color.RED);
        fillDead.setStyle(Paint.Style.FILL);
        fillDead.setColor(Color.WHITE);

        setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();

	            switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN:
	                    touch_start(x, y);
	                    invalidate();
	                    break;
	                case MotionEvent.ACTION_MOVE:
	                    touch_move(x, y);
	                    invalidate();
	                    break;
	                case MotionEvent.ACTION_UP:
	                    touch_end(x, y);
	                    invalidate();
	                    break;
	            }
	            return true;
			}
        });
	}

    public int getGeneration() {
        return generation;
    }

    private void clearArray(int[][] ar) {
        for (int i = 0; i<MAX_WIDTH; i++)
            for (int j = 0; j<MAX_HEIGHT; j++)
                ar[i][j] = DEAD;
    }
    //create a glider
    private void createGilder(int[][] ar) {
        int x = 4; //glider start position
        int y = 5;
        ar[x][y]     = ALIVE;
        ar[x-1][y+2] = ALIVE;
        ar[x][y+2]   = ALIVE;
        ar[x+1][y+1] = ALIVE;
        ar[x+1][y+2] = ALIVE;
    }

    public void init(int w, int h) {
        //clear arrays and initalize them again
        clearArray(stateA);
        clearArray(stateB);
        createGilder(stateA);
        
        fieldBg = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        field   = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(field);

        wPxPerField = w / MAX_WIDTH;
        hPxPerField = h / MAX_HEIGHT;

        //draw the field background now
        Canvas c = new Canvas(fieldBg);
        for (int i = 0; i<MAX_WIDTH; i++)
            for (int j = 0; j<MAX_HEIGHT; j++)
            {
                int left    = i * wPxPerField;
                int top     = j * hPxPerField;
                int right   = (i+1) * wPxPerField;
                int bottom  = (j+1) * hPxPerField;

                //Log.d(Constants.NAME, String.format("l: %d, t: %d, r: %d, b: %d", left, top, right, bottom));
                Rect r = new Rect(left, top, right, bottom);
                
                c.drawRect(r, fillDead);
                c.drawRect(r, paint);
            }
    }

    public void gameStart() {
        Log.d(Constants.NAME, "gameStart");
        running = true;
        generation = 0;
        invalidate(); //force redraw
        sendMessage(GameViewEventHandler.GAME_STARTED);
    }
    public void gameReset() {
        Log.d(Constants.NAME, "gameReset");
        running = false;
        generation = 0;
        clearArray(stateA);
        clearArray(stateB);
        createGilder(stateA);
        sendMessage(GameViewEventHandler.GAME_STARTED);//should have it's own event handler
        invalidate();
    }
    public void gameStop() {
        Log.d(Constants.NAME, "gameStop");
        running = false;
        sendMessage(GameViewEventHandler.GAME_STOPPED);
    }

    private void touch_start(float x, float y) {
        //what to do?
    }
    private void touch_move(float x, float y) {
        //what to do?
    }
    private void touch_end(Float x, Float y) {

        int ix = x.intValue();
        int iy = y.intValue();

        //set cell at x,y coord to the opposite of it's current status
        for (int i = 0; i<MAX_WIDTH; i++)
            for (int j = 0; j<MAX_HEIGHT; j++)
            {
                int left    = i * wPxPerField;
                int top     = j * hPxPerField;
                int right   = (i+1) * wPxPerField;
                int bottom  = (j+1) * hPxPerField;

                //Log.d(Constants.NAME, String.format("x: %d, y:%d, l: %d, t: %d, r: %d, b: %d", ix, iy, left, top, right, bottom));

                if (ix>=left && ix<=right && iy>=top && iy<=bottom)
                {
                    //Log.d(Constants.NAME, "found touch inside cell");
                    if (stateA[i][j] == ALIVE)
                        stateA[i][j] = DEAD;
                    else
                        stateA[i][j] = ALIVE;
                    return;
                }
            }
        Log.d(Constants.NAME, "no cell for touch found");
    }

    @Override
	public void onDraw(Canvas canvas) //this is our game loop
	{
		//Log.d(Constants.NAME, "onDraw");
        if (field != null && fieldBg != null)
        {
            canvas.drawBitmap(fieldBg, 0, 0, null);
            //calculate new generation
            calculateGeneration();
            drawGeneration();
            //draw new generation
            canvas.drawBitmap(field, 0, 0, null);
        }
		super.onDraw(canvas);
        if (running)
            invalidate(); //force redraw, which basically means: call onDraw again => next cycle in our game loop!
	}

    /**
     * http://en.wikipedia.org/wiki/Conway's_Game_of_Life
     *
     * The basic rules are as follows (quoted from above's article):
     *
     * The universe of the Game of Life is an infinite two-dimensional orthogonal grid of square cells,
     * each of which is in one of two possible states, live or dead. Every cell interacts with its eight
     * neighbors, which are the cells that are directly horizontally, vertically, or diagonally adjacent.
     * At each step in time, the following transitions occur:
     * 
     * 1. Any live cell with fewer than two live neighbours dies, as if caused by underpopulation.
     * 2. Any live cell with more than three live neighbours dies, as if by overcrowding.
     * 3. Any live cell with two or three live neighbours lives on to the next generation.
     * 4. Any dead cell with exactly three live neighbours becomes a live cell.
     *
     * I use stateB as a buffer in which I write the new state for each cell.
     * When I'm finished with all the calculations I copy all information back at once to the
     * stateA array.
     *
     * This uses twice as much memory (cause we have 2 arrays of the same size) but on the
     * other hand it's pretty easy to implement and I was lazy. ;-)
     */
    private void calculateGeneration() {
        if (!running) return;
        //Log.d(Constants.NAME, "calculateGeneration");

        //Check if at least one cell is still alive, if not, stop the game because everybody is DEAD!
        boolean atLeastOneAlive = false;
        for (int i = 0; i<MAX_WIDTH; i++)
        {
            for (int j = 0; j<MAX_HEIGHT; j++)
                if (stateA[i][j] == ALIVE)
                {
                    atLeastOneAlive = true;
                    break;
                }
            if (atLeastOneAlive) break;
        }
        if (!atLeastOneAlive)
        {
            gameStop();
            return;
        }

        //if no cell changed we are stuck, so end the game
        boolean changed = false;

        //Log.d(Constants.NAME, "calculateGeneration at least one cell is alive");
        //now calculate the new states for each cell
        for (int i = 0; i<MAX_WIDTH; i++)
            for (int j = 0; j<MAX_HEIGHT; j++)
            {
                boolean cellAlive = stateA[i][j] == ALIVE;
                int aliveNeighbours = 0;
                //Count all alive neighbours
                //8 = upper left neighbour
                //7 = upper top neighbour
                //6 = upper right neigbhour
                //5 = left neighbour
                //4 = right neighbour
                //3 = lower left neighbour
                //2 = lower bottom neighbour
                //1 = lower right neighbour
                for (int neighbour = 8; neighbour>0; neighbour--)
                {
                    boolean leftNeighbour   = neighbour == 8 || neighbour == 5 || neighbour == 3;
                    boolean rightNeighbour  = neighbour == 7 || neighbour == 4 || neighbour == 1;
                    boolean topNeighbour    = neighbour == 6 || neighbour == 7 || neighbour == 8;
                    boolean bottomNeighbour = neighbour == 1 || neighbour == 2 || neighbour == 3;

                    if (i == 0 && leftNeighbour) //there can't be anyone to the left
                        continue;
                    if (i == MAX_WIDTH-1 && rightNeighbour) //there can't be anyone to the right
                        continue;
                    if (j == 0 && topNeighbour) //there can't be anyone on top of us
                        continue;
                    if (j == MAX_HEIGHT-1 && bottomNeighbour) //there can't be anyone below us
                        continue;

                    int ni = i;
                    int nj = j;

                    if (rightNeighbour)  ni++;
                    if (leftNeighbour)   ni--;
                    if (topNeighbour)    nj++;
                    if (bottomNeighbour) nj--;

                    if (ni < 0 || ni >=MAX_WIDTH)  ni = i;
                    if (nj < 0 || nj >=MAX_HEIGHT) nj = j;

                    if (ni == i && nj == j) continue;

                    if (stateA[ni][nj] == ALIVE) aliveNeighbours++;
                }
                //no log here, cause it's slows down the process hardcore!
                //Log.d(Constants.NAME, String.format("Found %d alive neighbours for %s cell at %d/%d", aliveNeighbours, (cellAlive ? "alive" : "dead"), i, j));
                //1. Any live cell with fewer than two live neighbours dies, as if caused by underpopulation.
                //2. Any live cell with more than three live neighbours dies, as if by overcrowding.
                //3. Any live cell with two or three live neighbours lives on to the next generation.
                //4. Any dead cell with exactly three live neighbours becomes a live cell.
                if ((cellAlive && aliveNeighbours<2) || (cellAlive && aliveNeighbours>3))
                {
                    stateB[i][j] = DEAD; //rules 1 & 2
                    changed = true;
                }
                else if (!cellAlive && aliveNeighbours==3)
                {
                    stateB[i][j] = ALIVE; //rule 4
                    changed = true;
                }
                //else if (cellAlive && aliveNeighbours>=2 && aliveNeighbours<=3)
                //    stateB[i][j] = ALIVE; //rule 3
                else //just use the old state ==> this is rule 3
                    stateB[i][j] = stateA[i][j];
            }

        //copy new states
        for (int i = 0; i<MAX_WIDTH; i++)
            System.arraycopy(stateB[i], 0, stateA[i], 0, MAX_HEIGHT);

        if (!changed)
        {
            gameStop();
            return;
        }

        sendMessage(GameViewEventHandler.GAME_LOOP);
        generation++;
    }

    private void drawGeneration() {
        for (int i = 0; i<MAX_WIDTH; i++)
            for (int j = 0; j<MAX_HEIGHT; j++)
            {
                int left    = i * wPxPerField;
                int top     = j * hPxPerField;
                int right   = (i+1) * wPxPerField;
                int bottom  = (j+1) * hPxPerField;

                //Log.d(Constants.NAME, String.format("l: %d, t: %d, r: %d, b: %d", left, top, right, bottom));
                Rect r = new Rect(left, top, right, bottom);

                canvas.drawRect(r, (stateA[i][j] == ALIVE) ? fillAlive : fillDead);
                canvas.drawRect(r, paint);
            }
    }

    //EVENT STUFF ... not the best design
	private OnGameStoppedListener onGameStoppedListener;
	public void setOnGameStoppedListener(OnGameStoppedListener listener) {
		onGameStoppedListener = listener;
	}
    protected OnGameStoppedListener getOnGameStoppedListener() {
        return onGameStoppedListener;
    }

    private OnGameStartedListener onGameStartedListener;
	public void setOnGameStartedListener(OnGameStartedListener listener) {
		onGameStartedListener = listener;
	}
    protected OnGameStartedListener getOnGameStartedListener() {
        return onGameStartedListener;
    }

    private OnGameLoopListener onGameLoopListener;
	public void setOnGameLoopListener(OnGameLoopListener listener) {
		onGameLoopListener = listener;
	}
    protected OnGameLoopListener getOnGameLoopListener() {
        return onGameLoopListener;
    }

    // Send a message to a Handler
	private void sendMessage(int what, Object obj) {
		// Needs a message sent to the Handler
		Message msg = eventHandler.obtainMessage(what, obj);
		// Send message
		eventHandler.sendMessage(msg);
	}

	private void sendMessage(int what) {
		Message msg = eventHandler.obtainMessage(what);
		eventHandler.sendMessage(msg);
	}

	private void sendMessage(int what, int arg1, int arg2) {
		Message msg = eventHandler.obtainMessage(what, arg1, arg2);
		eventHandler.sendMessage(msg);
	}

}

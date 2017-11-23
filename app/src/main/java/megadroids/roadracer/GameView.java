package megadroids.roadracer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Kiruthiga on 11/22/2017.
 */

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;

    private Enemy[] enemies;
    //Adding 3 enemies you may increase the size
    private int enemyCount = 3;


    private Thread gameThread = null;

    private Player player;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    //defining a boom object to display blast
    private Boom boom;

    private ArrayList<Star> stars = new ArrayList<Star>();

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        player = new Player(context,screenX,screenY);
        surfaceHolder = getHolder();
        paint = new Paint();

        int starNums = 100;
        for(int i=0;i<starNums;i++){
        Star s = new Star(screenX,screenY);
        stars.add(s);

            //initializing enemy object array
            enemies = new Enemy[enemyCount];
            for(int j=0; j<enemyCount; j++) {
                enemies[j] = new Enemy(context, screenX, screenY);
            }

            }

        //initializing boom object
        boom = new Boom(context);

    }

    @Override
    public void run(){
        while(playing){
            update();
            draw();
            control();
        }
    }

    private void draw() {
        if(surfaceHolder.getSurface().isValid()){
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);
            for(Star s : stars){
                paint.setStrokeWidth(s.getStarWidth());
                canvas.drawPoint(s.getX(),s.getY(),paint);
            }

            //drawing the enemies
            for (int i = 0; i < enemyCount; i++) {
                canvas.drawBitmap(
                        enemies[i].getBitmap(),
                        enemies[i].getX(),
                        enemies[i].getY(),
                        paint
                );
            }

            //drawing boom image
            canvas.drawBitmap(
                    boom.getBitmap(),
                    boom.getX(),
                    boom.getY(),
                    paint
            );


            canvas.drawBitmap(player.getBitmap(),player.getX(),player.getY(),paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    }

//https://www.simplifiedcoding.net/android-game-development-tutorial-1/

    private void update() {


        player.update();

        //setting boom outside the screen
        boom.setX(-250);
        boom.setY(-250);


        for(Star s : stars){
            s.update(player.getSpeed());
        }

        //updating the enemy coordinate with respect to player speed
        for(int i=0; i<enemyCount; i++){
            enemies[i].update(player.getSpeed());

            //if collision occurrs with player
            if (Rect.intersects(player.getDetectCollision(), enemies[i].getDetectCollision())) {

                //displaying boom at that location
                boom.setX(enemies[i].getX());
                boom.setY(enemies[i].getY());

                //moving enemy outside the left edge
                enemies[i].setX(-200);
            }

        }

    }

    private void control() {

        try{
            gameThread.sleep(17);
        }catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }

    }

    public void pause(){
        playing=false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        playing=true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK){

            case MotionEvent.ACTION_UP:
                    player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                    break;

        }
        return true;
    }
}

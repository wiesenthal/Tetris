package com.tetris.miles.tetris;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TableLayout myTable;
    Board theBoard;
    LinearLayout[][] temp = new LinearLayout[18][10];
    private Handler mHandler = new Handler();
    private int score = 1;
    public int highScore = 0;
    private double speed = 1;
    private boolean speededUp = false;
    private double oldSpeed;
    private int currentOrientation = 0;
    private String currentBlock;

    TextView scoreDisplay;
    TextView startup;
    TextView endgame;
    TextView top;
    TextView bot;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startup = (TextView)findViewById(R.id.start);
        endgame = (TextView)findViewById(R.id.end);
        myTable = (TableLayout)findViewById(R.id.table);
        top = (TextView)findViewById(R.id.top);
        bot = (TextView)findViewById(R.id.bottom);

        top.setText("TOP");
        bot.setText("BOTTOM");
        top.setClickable(false);
        bot.setClickable(false);
        top.setBackgroundColor(Color.parseColor("#0186AD"));
        bot.setBackgroundColor(Color.parseColor("#0186AD"));
        SharedPreferences prefs = getSharedPreferences("dog", MODE_PRIVATE);
        highScore = prefs.getInt("high", 0);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        int uiBottom = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setSystemUiVisibility(uiBottom);
    }
    public void restart(View v){
        speed = 1;
        score = 0;
        theBoard.reset();
        endgame.setVisibility(View.INVISIBLE);
    }
    public void gameStart(View v){
        top.setText("");
        bot.setText("");
        top.setClickable(true);
        bot.setClickable(true);
        top.setBackgroundColor(Color.parseColor("#3202A4D3"));
        bot.setBackgroundColor(Color.parseColor("#006060D0"));
        startup.setVisibility(View.INVISIBLE);
        for (int i = 0; i < temp.length; i ++) {
            for (int j = 0; j < temp[i].length; j++) {
                TableRow dad = (TableRow) myTable.getChildAt(i);
                LinearLayout child = (LinearLayout) dad.getChildAt(j);
                temp[i][j] = child;
            }
        }

        scoreDisplay = (TextView)findViewById(R.id.scoreD);

        theBoard = new Board(temp);
        theBoard.update();
        fall();
        tick();
    }
    public void fall(){
        mHandler.postDelayed(new Runnable() {
            public void run() {
                theBoard.fall();
                speed += 0.003;
                score ++;
                scoreDisplay.setText("Score \n" + score);
                score+= theBoard.scorePlus;
                theBoard.scorePlus = 0;
                fall();
            }
        }, (long)(500.0/speed));
    }
    public void tick(){
        mHandler.postDelayed(new Runnable() {
            public void run() {
                boolean falling = false;
                for (int i = 0; i < theBoard.fallingBlocks.length; i ++){
                    if (theBoard.fallingBlocks[i][0] != -1){
                        falling = true;
                    }
                }
                if (!falling){
                    //make rotate block
                    //then multible colors possibly
                    createShape("R");
                    if (speededUp){
                        System.out.println(speed);
                        speed = oldSpeed;
                        speededUp = false;
                    }
                }
                theBoard.checkFull();
                if (theBoard.checkLoss()) lose();
                tick();
            }
        }, 250);
    }
    public void createShape(String type){
        if (type == "R") { //random
            String[] poss = {"L", "J", "I", "O", "T", "S", "Z"};
            Random rng = new Random();
            createShape(poss[rng.nextInt(7)]);
        }
        else {
            currentBlock = type;
        }
        if (type == "L") {
            currentOrientation = 3;
            makeBlock(1,3);
            makeBlock(0,3);
            makeBlock(0,4);
            makeBlock(0,5);
        }
        if (type == "J") {
            currentOrientation = 3;
            makeBlock(1,5);
            makeBlock(0,3);
            makeBlock(0,4);
            makeBlock(0,5);
        }
        if (type == "I"){
            currentOrientation = 1;
            makeBlock(0,3);
            makeBlock(0,4);
            makeBlock(0,5);
            makeBlock(0,6);
        }
        if (type == "O"){
            makeBlock(1,4);
            makeBlock(1,5);
            makeBlock(0,4);
            makeBlock(0,5);
        }
        if (type == "T"){
            currentOrientation = 3;
            makeBlock(1,5);
            makeBlock(0,5);
            makeBlock(0,4);
            makeBlock(0,6);

        }
        if (type == "S"){
            currentOrientation = 1;
            makeBlock(1,4);
            makeBlock(1,5);
            makeBlock(0,6);
            makeBlock(0,5);
        }
        if (type == "Z"){
            currentOrientation = 1;
            makeBlock(1,6);
            makeBlock(1,5);
            makeBlock(0,4);
            makeBlock(0,5);
        }
        if (type != "R") {
            System.out.println("oggsa");
            rotate(this.findViewById(android.R.id.content));
        }
    }
    public void lose(){
        if(score > highScore) highScore = score;
        String msg = ("Final Score: " + score + "\n High Score: " + highScore);
        endgame.setText(msg);
        SharedPreferences.Editor editor = getSharedPreferences("dog", MODE_PRIVATE).edit();
        editor.putInt("high", highScore);
        editor.commit();
        speed = 1;
        score = 0;
        endgame.setVisibility(View.VISIBLE);
        theBoard.reset();
    }

    public void makeBlock(int row, int col){
        theBoard.setBoard(row, col, true);
        theBoard.createFalling(row, col);
    }

    //clicks
    public void left(View v){
        ArrayList<Integer> nowX = new ArrayList();
        ArrayList<Integer> nowY = new ArrayList();
        boolean onWall = false;
        for (int i = 0; i < theBoard.fallingBlocks.length; i ++){

            if (theBoard.fallingBlocks[i][1] <= 0){
                onWall = true;
            }
            else if(theBoard.getBoard(theBoard.fallingBlocks[i][0],theBoard.fallingBlocks[i][1]-1) && !theBoard.isFalling(theBoard.fallingBlocks[i][0],theBoard.fallingBlocks[i][1]-1)){
                onWall = true;
            }
        }
        for (int i = 0; i < theBoard.fallingBlocks.length; i ++){
            if (!onWall){
                nowY.add(theBoard.fallingBlocks[i][0]);
                nowX.add(theBoard.fallingBlocks[i][1]);
                theBoard.fallingBlocks[i][0] = -1;
                theBoard.fallingBlocks[i][1] = -1;
                theBoard.setBoard(nowY.get(nowY.size()-1), nowX.get(nowX.size()-1), false);
            }
        }
        for(int i = 0; i < nowX.size(); i++){
            if (!onWall) {
                makeBlock(nowY.get(i), nowX.get(i)-1);
            }
        }
    }
    public void right(View v){
        ArrayList<Integer> nowX = new ArrayList();
        ArrayList<Integer> nowY = new ArrayList();
        boolean onWall = false;
        for (int i = 0; i < theBoard.fallingBlocks.length; i ++){
            if (theBoard.fallingBlocks[i][1] >= 9 || theBoard.fallingBlocks[i][1] < 0){
                onWall = true;
            }
            else if(theBoard.getBoard(theBoard.fallingBlocks[i][0],theBoard.fallingBlocks[i][1]+1) && !theBoard.isFalling(theBoard.fallingBlocks[i][0],theBoard.fallingBlocks[i][1]+1)){
                onWall = true;
            }
        }
        for (int i = 0; i < theBoard.fallingBlocks.length; i ++){
            if (!onWall){
                nowY.add(theBoard.fallingBlocks[i][0]);
                nowX.add(theBoard.fallingBlocks[i][1]);
                theBoard.fallingBlocks[i][0] = -1;
                theBoard.fallingBlocks[i][1] = -1;
                theBoard.setBoard(nowY.get(nowY.size()-1), nowX.get(nowX.size()-1), false);
            }
        }
        for(int i = 0; i < nowX.size(); i++){
            if (!onWall) {
                makeBlock(nowY.get(i), nowX.get(i)+1);
            }
        }
    }
    public void speedUp(View v){
        if(!speededUp){
            oldSpeed = speed;
        }
        speededUp = true;
        speed = speed*3;
        System.out.println("tes" + speed);
    }
    public void rotate(View v){
        if(theBoard.fallingBlocks[0][0] == -1){
            return;
        }
        final int[] baseline = {-1,-1};
        baseline[0] = theBoard.fallingBlocks[1][0];
        baseline[1] = theBoard.fallingBlocks[1][1];
        int[][] temp = new int[4][2];
        for (int i = 0; i < theBoard.fallingBlocks.length; i ++){
            temp[i][0] = theBoard.fallingBlocks[i][0];
            temp[i][1] = theBoard.fallingBlocks[i][1];
        }

        for (int[] set : theBoard.fallingBlocks){ // clear fallingBlocks
            if(set[0] != -1){
                theBoard.setBoard(set[0],set[1], false);
            }
            set[0] = -1;
            set[1] = -1;
        }
        if (currentBlock == "I"){
            currentOrientation = (currentOrientation+1)%2;
            if (currentOrientation == 0){
                //create shape around baseline block (the second block in the shape)
                //actually, change this later to make a method that checks if theres something in the way, and also clears the blocks(not sure what i meant by this). This is bad, change very much
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0],baseline[1]-1};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]+1};
                int[] d = {baseline[0],baseline[1]+2};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else{
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+2,baseline[1]};
                int[] b = {baseline[0]+1,baseline[1]};
                int[] c = {baseline[0],baseline[1]};
                int[] d = {baseline[0]-1,baseline[1]};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
        }
        if (currentBlock == "Z"){
            currentOrientation = (currentOrientation+1)%2;
            if (currentOrientation == 0){
                //create shape around baseline block (the second block in the shape)
                //actually, change this later to make a method that checks if theres something in the way, and also clears the blocks(not sure what i meant by this). This is bad, change very much
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0],baseline[1]+1};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0]-1,baseline[1]};
                int[] d = {baseline[0]-1,baseline[1]-1};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else{
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]+1};
                int[] d = {baseline[0]-1,baseline[1]+1};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
        }
        if (currentBlock == "S"){
            currentOrientation = (currentOrientation+1)%2;
            if (currentOrientation == 0){
                //create shape around baseline block (the second block in the shape)
                //actually, change this later to make a method that checks if theres something in the way, and also clears the blocks(not sure what i meant by this). This is bad, change very much
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0],baseline[1]-1};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0]-1,baseline[1]};
                int[] d = {baseline[0]-1,baseline[1]+1};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else{
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]-1};
                int[] d = {baseline[0]-1,baseline[1]-1};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
        }
        if (currentBlock == "T"){
            currentOrientation = (currentOrientation+1)%4;
            if (currentOrientation == 0){
                //create shape around baseline block (the second block in the shape)
                //actually, change this later to make a method that checks if theres something in the way, and also clears the blocks(not sure what i meant by this). This is bad, change very much
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]-1};
                int[] d = {baseline[0],baseline[1]+1};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else if (currentOrientation == 1){
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]-1};
                int[] d = {baseline[0]-1,baseline[1]};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else if (currentOrientation == 2){
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0],baseline[1]-1};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]+1};
                int[] d = {baseline[0]-1,baseline[1]};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else{
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]+1};
                int[] d = {baseline[0]-1,baseline[1]};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
        }
        if (currentBlock == "L"){
            currentOrientation = (currentOrientation+1)%4;
            System.out.println(currentOrientation);
            if (currentOrientation == 0){
                //create shape around baseline block (the second block in the shape)
                //actually, change this later to make a method that checks if theres something in the way, and also clears the blocks(not sure what i meant by this). This is bad, change very much
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]-1};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]-1};
                int[] d = {baseline[0],baseline[1]+1};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else if (currentOrientation == 1){
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0]-1,baseline[1]};
                int[] d = {baseline[0]-1,baseline[1]-1};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else if (currentOrientation == 2){
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0],baseline[1]-1};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]+1};
                int[] d = {baseline[0]-1,baseline[1]+1};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else{
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0]+1,baseline[1]+1};
                int[] d = {baseline[0]-1,baseline[1]};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
        }

        if (currentBlock == "J"){
            currentOrientation = (currentOrientation+1)%4;
            System.out.println(currentOrientation);
            if (currentOrientation == 0){
                //create shape around baseline block (the second block in the shape)
                //actually, change this later to make a method that checks if theres something in the way, and also clears the blocks(not sure what i meant by this). This is bad, change very much
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]+1};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]-1};
                int[] d = {baseline[0],baseline[1]+1};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else if (currentOrientation == 1){
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0]+1,baseline[1]-1};
                int[] d = {baseline[0]-1,baseline[1]};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else if (currentOrientation == 2){
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0],baseline[1]-1};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0],baseline[1]+1};
                int[] d = {baseline[0]-1,baseline[1]-1};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
            else{
                ArrayList<int[]> tempBlocks = new ArrayList();
                int[] a = {baseline[0]+1,baseline[1]};
                int[] b = {baseline[0],baseline[1]};
                int[] c = {baseline[0]-1,baseline[1]+1};
                int[] d = {baseline[0]-1,baseline[1]};
                tempBlocks.add(a);
                tempBlocks.add(b);
                tempBlocks.add(c);
                tempBlocks.add(d);
                attemptBlock(new ArrayList<int[]>(tempBlocks));
            }
        }
        if (theBoard.fallingBlocks[0][0] == -1){
            // did not get replaced
            for (int i = 0; i < theBoard.fallingBlocks.length; i ++){
                theBoard.fallingBlocks[i][0] = temp[i][0];
                theBoard.fallingBlocks[i][1] = temp[i][1];
                theBoard.setBoard(temp[i][0], temp[i][1], true);
            }
        }
    }
    public void attemptBlock(ArrayList<int[]> blocks){
        for(int i = 0; i < blocks.size(); i ++){
            if(blocks.get(i)[0] < 0 || blocks.get(i)[1] < 0 || blocks.get(i)[0] > 17 || blocks.get(i)[1] >9 || (theBoard.getBoard(blocks.get(i)[0], blocks.get(i)[1]) && !theBoard.isFalling(blocks.get(i)[0], blocks.get(i)[1]))){
                return;
            }
        }
        for(int i = 0; i < blocks.size(); i ++){
            makeBlock(blocks.get(i)[0],blocks.get(i)[1]);
        }
    }

}

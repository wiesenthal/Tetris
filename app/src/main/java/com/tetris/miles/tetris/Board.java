package com.tetris.miles.tetris;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Miles on 9/25/2017.
 */

public class Board {
    //18 rows, 10 columns
    //false is nothing, true is
    private boolean[][] board = new boolean[18][10];
    public int scorePlus = 0;
    private LinearLayout[][] boardActual = new LinearLayout[18][10];
    public int[][] fallingBlocks = {{-1,-1},{-1,-1},{-1,-1},{-1,-1}};
    //Test falling blocks, not sure if done


    public Board(LinearLayout[][] input) {
        boardActual = input;
    }
    public void setBoard(int row, int col, boolean state){
        //sets a board position
        board[row][col] = state;
        update();
    }
    public boolean getBoard(int row, int col){
        return board[row][col];
    }
    public void clearRow(int row){
        for (int i = 0; i < board[row].length; i++){
            board[row][i] = false;
        }
        moveDown(row);
        update();
    }
    public void moveDown(int bottomRow){
        for (int i = bottomRow-1; i > 0; i --){
            for (int j = 0; j < board[i].length; j ++){
                if (!isFalling(i, j)){
                    board[i+1][j] = board[i][j];
                }

            }
        }
        for (int i = 0; i < board[0].length; i ++){
            board[0][i] = false;
        }
        update();
    }
    public void checkFull(){
        for (int i = 0; i < board.length; i++){
            boolean full = true;
            for (int j = 0; j < board[i].length; j++){
                if (!board[i][j] || isFalling(i,j)){
                    full = false;
                }
            }
            if (full){
                scorePlus += 250;
                clearRow(i);
            }
        }
        update();
    }
    public void fall(){
        for (int i = 0; i < fallingBlocks.length; i ++){
            if (fallingBlocks[i][0] != -1){ // if it exists
                if (fallingBlocks[i][0] == 17 || board[fallingBlocks[i][0]+1][fallingBlocks[i][1]] && !isFalling(fallingBlocks[i][0]+1,fallingBlocks[i][1])){ // if its on the ground
                    for (int j = 0; j < fallingBlocks.length; j ++){
                        fallingBlocks[j] = new int[]{-1,-1};
                    }
                }

            }
        }
        for (int i = 0; i < fallingBlocks.length; i ++){
            if (fallingBlocks[i][0] != -1){
                board[fallingBlocks[i][0]][fallingBlocks[i][1]] = false;
                fallingBlocks[i][0] +=1; //change y by 1
                board[fallingBlocks[i][0]][fallingBlocks[i][1]] = true;
                update();
            }
        }
    }
    public boolean isFalling(int row, int col){
        for (int i = 0; i < fallingBlocks.length; i++){
            if (fallingBlocks[i][0] == row && fallingBlocks[i][1] == col){
                return true;
            }
        }
        return false;
    }
    public void createFalling(int row, int col){
        boolean done = false;
        for (int i = 0; i < fallingBlocks.length; i++){
            if (fallingBlocks[i][0] == -1 && !done){
                fallingBlocks[i][0] = row;
                fallingBlocks[i][1] = col;
                done = true;
            }
        }
    }
    public boolean checkLoss(){
        for (int i = 0; i < board[1].length; i++){
            if (board[1][i] && !isFalling(1,i)){
                return true;
            }
        }
        return false;
    }
    public void reset(){
        for (int i = 0; i < fallingBlocks.length; i++){
            fallingBlocks[i][0] = -1;
            fallingBlocks[i][1] = -1;
        }
        for (int i = 0; i < board.length; i ++){
            for (int j = 0; j < board[i].length; j++){
                board[i][j] = false;
            }
        }
    }

    public void update() {
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++){
                if(board[i][j]){
                    boardActual[i][j].setVisibility(View.VISIBLE);
                }
                else {
                    boardActual[i][j].setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}

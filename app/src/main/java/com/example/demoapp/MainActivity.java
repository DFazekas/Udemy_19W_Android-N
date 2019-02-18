package com.example.demoapp;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int yellow = 0, red = 1;
    int activePlayer = yellow;
    int[] score = {0, 0};
    boolean gameStatus = true;

    // 2 = Empty zone.
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    // Possible winning zone positions.
    int[][] winningPositions = {
            {0,1,2}, {3,4,5}, {6,7,8},
            {0,3,6}, {1,4,7}, {2,5,8},
            {0,4,8}, {2,4,6}};

    // Score labels.
    TextView redScoreTxt;
    TextView yellowScoreTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.redScoreTxt = findViewById(R.id.redScoreTextView);
        this.yellowScoreTxt = findViewById(R.id.yellowScoreTextView);
    }

    public void dropIn(View view) {
        if (!gameStatus) {
            // Game is over - do nothing.
            return;
        }

        ImageView zone = (ImageView)view;

        // Detect which zone was selected.
        int tappedZone = Integer.parseInt(zone.getTag().toString()) - 1;
        Log.i("TappedZone", String.valueOf(tappedZone));

        if (gameState[tappedZone] != 2) {
            // Already selected - do nothing.
            return;
        }

        // Update GameState with ActivePlayer's ID.
        gameState[tappedZone] = activePlayer;
        zone.setTranslationY(-1000f);
        // Detect which color to placed.
        int color = (activePlayer == yellow) ? R.drawable.yellow : R.drawable.red;
        zone.setImageResource(color);

        // Switch ActivePlayer.
        activePlayer = (activePlayer == yellow) ? red : yellow;

        // Fill & animate zone.
        zone.animate().translationYBy(1000f).setDuration(400);

        int winningToken = -1;
        if ((winningToken = whichPlayerWon()) != -1) {
            // Disable future moves.
            this.gameStatus = false;
            this.updateScore(winningToken);
            String winner = (winningToken == yellow) ? "Yellow" : "Red";
            Toast.makeText(this, "GAME OVER! "+winner+" won!", Toast.LENGTH_SHORT).show();
            Button resetBtn = findViewById(R.id.resetBtn);
        }
    }

    private int whichPlayerWon() {
        // Checks if 3 token positions match any of the possible winning positions.
        for (int[] winningPosition : winningPositions) {
            // Check each winning combination.
            if (gameState[winningPosition[0]] != 2 &&
                    gameState[winningPosition[0]] == gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]] == gameState[winningPosition[2]]) {
                // Return token of winning player (0 = Yellow, 1 = Red, 2 = No one).
                return gameState[winningPosition[0]];
            }
        }
        // No winner.
        return -1;
    }

    public void newGame(View view) {
        Toast.makeText(this, "Resetting", Toast.LENGTH_SHORT).show();

        // Reset game status.
        this.gameStatus = true;

        // Reset active player.
        activePlayer = yellow;

        // Reset game state.
        for (int index = 0; index < gameState.length; index++) {
            gameState[index] = 2;
        }

        ConstraintLayout layout = findViewById(R.id.myConstraintLayout);
        int count = layout.getChildCount();
        for (int index = 0; index < count; index++) {
            View subView = layout.getChildAt(index);

            if (subView instanceof ImageView) {
                ImageView img = (ImageView) subView;
                img.setImageResource(0);
            }
        }
        ImageView bg = findViewById(R.id.bgImageView);
        bg.setImageResource(R.drawable.board);
    }

    private void updateScore(int player) {
        score[player]++;
        String value = String.valueOf(score[player]);

        if (player == yellow) {
            this.yellowScoreTxt.setText(value);
        } else {
            this.redScoreTxt.setText(value);
        }
    }
}

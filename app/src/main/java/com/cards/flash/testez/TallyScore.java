package com.cards.flash.testez;

/**
 * Created by Vincent on 2/26/2016.
 */
public final class TallyScore {

    public static int score = 0;

    public TallyScore(){};

    public static void resetScore(){
        score = 0;
    }

    public static void increaseScore(){
        score++;
    }

    public static int getScore() {
        return score;
    }
}

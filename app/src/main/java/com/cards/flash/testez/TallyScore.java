package com.cards.flash.testez;

/**
 * Created by Vincent on 2/26/2016.
 */
public final class TallyScore {

    public static int score = 0;
    public static int incompleteCards = 0;

    public TallyScore(){};

    public static void resetScore(){
        score = 0;
    }

    public static void addNewCard(){
        ++incompleteCards;
    }
    public static void resetCardCount(){
        incompleteCards = 0;
    }
    public static int getIncompleteCards(){
        return incompleteCards;
    }
    public static void cardFinished(){
        --incompleteCards;
    }
    public static void increaseScore(){
        score++;
    }

    public static int getScore() {
        return score;
    }
}

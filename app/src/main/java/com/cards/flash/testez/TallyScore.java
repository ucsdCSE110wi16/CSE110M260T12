package com.cards.flash.testez;

/**
 * Created by Vincent on 2/26/2016.
 */
public class TallyScore {

    private  int score = 0;
    private  int incompleteCards = 0;

    public TallyScore(){};

    public  void resetScore(){
        score = 0;
    }

    public  void addNewCard(){
        ++incompleteCards;
    }
    public  void resetCardCount(){
        incompleteCards = 0;
    }
    public  int getIncompleteCards(){
        return incompleteCards;
    }
    public  void cardFinished(){
        --incompleteCards;
    }
    public  void increaseScore(){
        score++;
    }

    public  int getScore() {
        return score;
    }
}

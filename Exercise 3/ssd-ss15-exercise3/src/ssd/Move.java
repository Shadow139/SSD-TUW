package ssd;

import java.util.ArrayList;

/**
 * Created by Shadow on 13/05/15.
 */
public class Move {
    private String player;
    private String session;
    private int question;
    private ArrayList<String> answerList;

    public Move() {
        this.answerList = new ArrayList<String>();
    }

    public Move(String player,int question,ArrayList<String> answerList) {
        this.player = player;
        this.question = question;
        this.answerList = answerList;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getQuestion() {
        return question;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public ArrayList<String> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(ArrayList<String> answerList) {
        this.answerList = answerList;
    }

    public String getPlayer() {

        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void addAnswer(String answer){
            this.answerList.add(answer);
    }

    @Override
    public String toString() {
        return "Move{" +
                "player='" + player + '\'' +
                ", session='" + session + '\'' +
                ", question=" + question +
                ", answerList=" + answerList +
                '}';
    }
}

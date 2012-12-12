package com.daedalusgames.shotgun_blob;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.util.HashSet;

public class Action
{
    public enum ActionType
    {
        WAIT,

        MOVE,

        TALK
    };
    private ActionType type;

    private Actor subject;

    private int amount;

    private boolean restrains;

    private SpeechBubble bubble;


    public Action(ActionType type, Actor subject, int amountFrames)
    {
        this.type = type;

        if (type == ActionType.TALK)
        {
            throw new IllegalStateException("An action of type 'talk' needs to be supplied a string. Use the other constructor instead.");
        }

        this.subject = subject;
        this.amount = amountFrames;
        this.restrains = true;
    }

    public Action(Actor subject, String speech, int amountFrames, boolean restrainActor, GameWorld gameWorld)
    {
        this.type = ActionType.TALK;
        this.subject = subject;
        this.amount = amountFrames;
        this.restrains = restrainActor;

        this.bubble = new SpeechBubble(gameWorld, subject, speech);
    }

    public Actor getSubject()
    {
        return subject;
    }

    public void setSubject(Actor subject)
    {
        this.subject = subject;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public boolean restrains()
    {
        return restrains;
    }

    public ActionType getType()
    {
        return type;
    }

    public SpeechBubble getSpeechBubble()
    {
        return bubble;
    }
}

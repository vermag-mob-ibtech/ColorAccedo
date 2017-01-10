package com.accedo.gameModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Vishal Nigam on 09-10-2016.
 */
public class CardState  extends RealmObject {
    @PrimaryKey
    private int position;
    private int content;
    private boolean gone;
    private boolean open;

    public void setPosition(int index) {
        this.position = index;
    }
    public int getPosition() {
        return position;
    }

    public void setContent(int content){
        this.content = content;
    }
    public int getContent() {
        return content;
    }

    public void setGone(boolean gone) {
        this.gone = gone;
    }
    public boolean getGone() {
        return gone;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
    public boolean getOpen() {
        return open;
    }
}

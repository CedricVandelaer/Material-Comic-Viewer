package com.comicviewer.cedric.comicviewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CV on 29/04/2015.
 * helper class for filedialog
 */
public class ListenerList<L> {
    private List<L> mListenerList = new ArrayList<L>();

    public interface FireHandler<L> {
        void fireEvent(L listener);
    }

    public void add(L listener) {
        mListenerList.add(listener);
    }

    public void fireEvent(FireHandler<L> fireHandler) {
        List<L> copy = new ArrayList<L>(mListenerList);
        for (L l : copy) {
            fireHandler.fireEvent(l);
        }
    }

    public void remove(L listener) {
        mListenerList.remove(listener);
    }

    public List<L> getListenerList() {
        return mListenerList;
    }
}
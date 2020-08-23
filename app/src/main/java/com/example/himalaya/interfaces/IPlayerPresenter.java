package com.example.himalaya.interfaces;

import com.example.himalaya.base.BasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayerPresenter extends BasePresenter<IPlayerViewCallback> {
    void start();

    void pause();

    void stop();

    void playPre();

    void playNext();

    void switchPlayMode(XmPlayListControl.PlayMode mode);

    void seekTo(int position);

    void getList();

    void playByIndex(int index);



}

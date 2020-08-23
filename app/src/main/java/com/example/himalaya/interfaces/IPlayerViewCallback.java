package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerViewCallback {

    void onPlayStart();

    void onPlayStop();

    void onPlayPause();

    void onChangePre();

    void onChangeNext();

    void onSwitchPlayMode(XmPlayListControl.PlayMode mode);

    void onListLoaded(List<Track> trackList);

    void onTrackUpdate(Track track,int index);

    void onProgressChange(int currPos, int duration);


}

package com.example.myapplication2.app;

import com.orm.SugarRecord;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListEntity extends SugarRecord<HitListEntity>
{
    private String hash;
    private String artist;
    private String track;
    private String label;
    private boolean newHitListEntity;
    private boolean removedFromHitList;

    public HitListEntity()
    {}

    public HitListEntity(String artist, String track, String label)
    {
        this.artist = artist;
        this.track = track;
        this.label = label;

        this.newHitListEntity = true;
        this.hash = artist.concat(track).concat(label).toUpperCase();
    }

    public String getHash()
    {
        return hash;
    }

    public String getArtist()
    {
        return artist;
    }

    public String getTrack()
    {
        return track;
    }

    public String getLabel()
    {
        return label;
    }

    public boolean isNewHitListEntity()
    {
        return newHitListEntity;
    }

    public void setNewHitListEntity(boolean newHitListEntity)
    {
        this.newHitListEntity = newHitListEntity;
    }

    public boolean isRemovedFromHitList()
    {
        return removedFromHitList;
    }

    public void setRemovedFromHitList(boolean removedFromHitList)
    {
        this.removedFromHitList = removedFromHitList;
    }
}

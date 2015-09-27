package io.policarp.triplejhitlistapp.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListDaoManager
{
    public void clearAllHitListEntities()
    {
        HitListEntity.deleteAll(HitListEntity.class);
    }

    public List<HitListEntity> getActiveHitList()
    {
        List<HitListEntity> hitListEntities = new ArrayList<>();
        for (HitListEntity entity : HitListEntity.listAll(HitListEntity.class))
        {
            if (!entity.isRemovedFromHitList())
            {
                hitListEntities.add(entity);
            }
        }
        return hitListEntities;
    }

    public List<HitListEntity> getArchivedHitList()
    {
        List<HitListEntity> hitListEntities = new ArrayList<>();
        for (HitListEntity entity : HitListEntity.listAll(HitListEntity.class))
        {
            if (entity.isRemovedFromHitList())
            {
                hitListEntities.add(entity);
            }
        }
        return hitListEntities;
    }

    public void updateHitListEntities(List<HitListEntity> newHitListEntities)
    {
        final Map<String, HitListEntity> newHitListEntityMap = new LinkedHashMap<>(newHitListEntities.size());
        for (HitListEntity newEntity : newHitListEntities) newHitListEntityMap.put(newEntity.getHash(), newEntity);

        // update current hit list entities
        final List<HitListEntity> activeEntities = getActiveHitList();

        for (HitListEntity activeEntity : activeEntities)
        {
            if (newHitListEntityMap.keySet().contains(activeEntity.getHash()))
            {
                // delete the dupe so that new entry can take it's place, possibly with new list rank
                newHitListEntityMap.get(activeEntity.getHash()).setNewHitListEntity(false);
                activeEntity.delete();

            } else {

                activeEntity.setNewHitListEntity(false);
                activeEntity.setRemovedFromHitList(true);
                activeEntity.save();
            }
        }

        HitListEntity.saveInTx(newHitListEntityMap.values());
    }

}

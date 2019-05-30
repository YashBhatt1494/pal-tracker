package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    Long userID = 0L;
    Map<Long, TimeEntry> map = new HashMap<>();


    public TimeEntry create(TimeEntry timeEntry) {
        userID = userID+1;
        timeEntry.setId(userID);
        map.put(userID, timeEntry);
        return timeEntry;
    }

    public TimeEntry find(Long id) {
        if(map.containsKey(id))
            return map.get(id);
        else return null;
    }

    public List<TimeEntry> list() {
        List<TimeEntry> lst = new ArrayList<>();
        for(Long l : map.keySet()){
            lst.add(map.get(l));
        }
        return lst;

        //return new ArrayList<TimeEntry>(map.values());
    }
    public TimeEntry update(long id, TimeEntry timeEntry) throws NullPointerException {
        if(map.containsKey(id)){
            //timeEntryHashMap.remove(id);
            timeEntry.setId(userID);
            map.put(id, timeEntry);
            return map.get(id);
        }

        return null;
    }

//    public TimeEntry update(long id, TimeEntry timeEntry) {
//        if(map.containsKey(id)) {
//            timeEntry.setId(id);
//            return map.put(timeEntry.getId(), timeEntry);
//        }
//        else return null;
//    }

    public void delete(Long id) {
        map.remove(id);
    }
    public TimeEntry find(long timeEntryId){
        if(map.containsKey(timeEntryId))
            return map.get(timeEntryId);
        else return null;

    }
}

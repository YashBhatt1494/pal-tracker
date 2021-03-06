//package io.pivotal.pal.tracker;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/time-entries")
//public class TimeEntryController {
//
//
//
//    TimeEntryRepository timeEntryRepository ;
//    public TimeEntryController(TimeEntryRepository timeEntryRepository){
//        this.timeEntryRepository = timeEntryRepository;
//    }
//
//    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {
//        return new ResponseEntity(timeEntryRepository.create(timeEntryToCreate), HttpStatus.CREATED);
//    }
//
//    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, value="/{id}")
//    public ResponseEntity<TimeEntry> read(@PathVariable("id") long nonExistentTimeEntryId) {
//
//        TimeEntry obj = timeEntryRepository.find(nonExistentTimeEntryId);
//        if(obj != null){
//            return new ResponseEntity<TimeEntry>(obj, HttpStatus.OK);
//        }
//        else
//            return new ResponseEntity<TimeEntry>(HttpStatus.NOT_FOUND);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<TimeEntry>> list() {
//            return new ResponseEntity<List<TimeEntry>>(timeEntryRepository.list(), HttpStatus.OK);
//    }
//
//    @PutMapping(value="/{id}")
//    public ResponseEntity update(@PathVariable("id") long timeEntryId, @RequestBody TimeEntry expected) {
//        TimeEntry obj =timeEntryRepository.update(timeEntryId,expected);
//        if( null != obj){
//            return new ResponseEntity(obj,HttpStatus.OK);
//        }
//        else {
//            return new ResponseEntity(HttpStatus.NOT_FOUND) ;
//        }
//    }
//
//    @DeleteMapping(value = "/{id}")
//    public ResponseEntity<TimeEntry> delete(@PathVariable("id") long timeEntryId) {
//        timeEntryRepository.delete(timeEntryId);
//        return new ResponseEntity<TimeEntry>(HttpStatus.NO_CONTENT);
//    }
//
//}
//

package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntriesRepo;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;

    public TimeEntryController(
            TimeEntryRepository timeEntriesRepo,
            MeterRegistry meterRegistry
    ) {
        this.timeEntriesRepo = timeEntriesRepo;

        timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
        TimeEntry createdTimeEntry = timeEntriesRepo.create(timeEntry);
        actionCounter.increment();
        timeEntrySummary.record(timeEntriesRepo.list().size());

        return new ResponseEntity<>(createdTimeEntry, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable Long id) {
        TimeEntry timeEntry = timeEntriesRepo.find(id);
        if (timeEntry != null) {
            actionCounter.increment();
            return new ResponseEntity<>(timeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        actionCounter.increment();
        return new ResponseEntity<>(timeEntriesRepo.list(), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable Long id, @RequestBody TimeEntry timeEntry) {
        TimeEntry updatedTimeEntry = timeEntriesRepo.update(id, timeEntry);
        if (updatedTimeEntry != null) {
            actionCounter.increment();
            return new ResponseEntity<>(updatedTimeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable Long id) {
        timeEntriesRepo.delete(id);
        actionCounter.increment();
        timeEntrySummary.record(timeEntriesRepo.list().size());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

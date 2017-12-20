package main.java.com.couchbase.jts.worker;

import com.couchbase.client.java.search.result.SearchQueryResult;
import main.java.com.couchbase.jts.drivers.Client;
import main.java.com.couchbase.jts.logger.ThroughputLogger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by oleksandr.gyryk on 12/7/17.
 */
public class KVWorker extends Worker {

    private int totalWorkers;
    private int throughputGoal;

    public KVWorker(Client client, int workerId, int totalWorkers, int throughputGoal) {
        super(client);
        this.totalWorkers = totalWorkers;
        this.throughputGoal = throughputGoal;
    }

    public void runAction(){
        try {
            long st = System.nanoTime();
            clientDB.mutateRandomDoc();
            long en = System.nanoTime();
            long latency =  (en - st) / 1000000;
            if (throughputGoal > 0) {
                float expectedDelayMC = (totalWorkers / (float) throughputGoal) * 1000;
                if (expectedDelayMC > latency) {
                    long delayMS = (long) expectedDelayMC - latency;
                    TimeUnit.MILLISECONDS.sleep(delayMS);
                }
            }

        } catch (Exception ex) {
            return;
        }
    }

    public void shutDown(){
        try {
            throughputLogger.dumpThroughput();
        } catch (IOException e) {
            System.err.println("ERROR Failed to dump kv throughput stats to disk: " + e.getMessage());
        }

    }

}



package com.zhaizq.aio.common;

import com.zhaizq.aio.common.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class SequenceMaker {
    private final SequenceBuilder sequenceBuilder;
    
    private final Map<String, Sequence> sequenceCacheMap = new ConcurrentHashMap<>(32);

    public String makeNumber(String name, String prefix, int length, int cache) {
        long next = this.getNext(name, cache);
        return prefix + StringUtil.leftPad(String.valueOf(next % (int) Math.pow(10, length)), length, "0");
    }

    protected synchronized long getNext(String name, int cache) {
        Sequence sequence = sequenceCacheMap.computeIfAbsent(name, v -> {
            sequenceBuilder.initSequence(v);
            return sequenceBuilder.nextSequence(v, 1);
        });

        long current = sequence.getCurrent() + sequence.getInc();
        if (current > sequence.getMax()) {
            sequence = sequenceBuilder.nextSequence(name, cache);
            sequenceCacheMap.put(name, sequence);
            return getNext(name, cache);
        }

        return sequence.current = current;
    }

    @Getter
    public static class Sequence {
        private long current;
        private final int inc;
        private final long max;

        public Sequence(long current, int inc, int cache) {
            this.current = current;
            this.inc = inc;
            this.max = current + inc * cache;
        }
    }
    
    public interface SequenceBuilder {
        /**
         * Propagation.NOT_SUPPORTED
         *
         * insert ignore into sequence_table (name, current, inc, cache) values (<name>, 0, 1, 5)
         */
        void initSequence(String name);

        /**
         * Propagation.REQUIRES_NEW
         *
         * select current, inc, cache from sequence_table where name = <name> for update
         * update sequence_table set current = <max> where name = <name>
         */
        Sequence nextSequence(String name, int cache);
    }
}
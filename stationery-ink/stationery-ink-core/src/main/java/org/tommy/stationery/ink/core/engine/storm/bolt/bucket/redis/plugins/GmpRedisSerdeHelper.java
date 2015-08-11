package org.tommy.stationery.ink.core.engine.storm.bolt.bucket.redis.plugins;

import backtype.storm.tuple.Tuple;
import org.tommy.stationery.ink.core.engine.utils.TupleUtil;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kun7788 on 15. 8. 7..
 */
public class GmpRedisSerdeHelper {

    Map<String, Integer> sessionHistoryIndexs = new HashMap<String, Integer>();

    public ShardedJedis getJedisResource(ShardedJedisPool shardedJedisPool) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = shardedJedisPool.getResource();
        } catch (JedisConnectionException ex) {
            if (shardedJedis != null) {
                shardedJedisPool.returnBrokenResource(shardedJedis);
                shardedJedis = shardedJedisPool.getResource();
            }
        } catch (Exception ex) {
            if (shardedJedisPool != null) {
                shardedJedisPool.returnResource(shardedJedis);
            }
        }
        return shardedJedis;
    }

    public GmpRedisSerdeHelper() {
        sessionHistoryIndexs.put("date", 0);
        sessionHistoryIndexs.put("sum", 1);
        sessionHistoryIndexs.put("count", 2);
    }

    public String getAgeGroup(String ageBand) {
        String ageGroup = "U";
        if (ageBand == null || ageBand.length() < 1) {
            return ageGroup;
        }

        String age = ageBand.substring(0, 1);
        if ("1".equals(age) || "2".equals(age) || "3".equals(age) || "4".equals(age) ||"5".equals(age) || "6".equals(age)) {
            return age;
        } else {
            return ageGroup;
        }
    }

    private String MapToString(NavigableMap<Long, Map<String, Long>> map, Long date, Long sum, Long cnt) {
        boolean isFirstOp = false;
        if (map.containsKey(date) == false) {
            isFirstOp = true;
            Map<String, Long> subMap = new HashMap<String, Long>();
            subMap.put("sum", sum);
            subMap.put("count", cnt);
            map.put(date, subMap);
        }

        final boolean finalIsFirstOp = isFirstOp;
        String str = map.entrySet().stream().map(v -> {
            if (finalIsFirstOp == false && v.getKey().equals(date)) {
                v.getValue().put("sum", v.getValue().get("sum") + sum);
                v.getValue().put("count", v.getValue().get("count") + cnt);
            }
            return v;
        }).map(v -> {
            return v.getKey() + "," + v.getValue().get("sum") + "," + v.getValue().get("count");
        }).collect(Collectors.joining("|"));
        return str;
    }


    private NavigableMap<Long, Map<String, Long>> StringToMap(String str, int keyNameIndex, Map<String, Integer> indexs, int limitSize) {
        NavigableMap<Long, Map<String, Long>> map = new TreeMap<Long, Map<String, Long>>();

        List<String> strLists = Arrays.asList(str.split("\\|"));
        if (strLists.size() > limitSize) {
            strLists = strLists.subList(0, limitSize-1);
        }

        strLists.stream().map(k -> {
            String[] vals = k.split("\\,");
            return vals;
        }).forEach(k -> {
            Map<String, Long> data = new HashMap<String, Long>();
            indexs.entrySet().forEach(e -> {
                data.put(e.getKey(), Long.valueOf(k[e.getValue()]));
            });
            map.put(Long.valueOf(k[keyNameIndex]), data);
        });
        return map.descendingMap();
    }


    public String genAccFlat(String sessionAcc, Tuple tuple, String tsField, String sumField, String cntField) {
        //first,last,sum,count|,,,,
        String accFlat = null;

        if (TupleUtil.getStringValue(tuple, tsField) == null || TupleUtil.getLongValue(tuple, sumField) == null || TupleUtil.getLongValue(tuple, cntField) == null) {
            return accFlat;
        }

        if (sessionAcc != null) {
            String[] sessionAccArr = sessionAcc.split("\\,");
            accFlat = sessionAccArr[0] + "," + tuple.getStringByField(tsField) + "," + (Long.valueOf(sessionAccArr[2]) + TupleUtil.getLongValue(tuple, sumField)) + "," + (Long.valueOf(sessionAccArr[3]) + TupleUtil.getLongValue(tuple, cntField));
        } else {
            accFlat = tuple.getStringByField(tsField) + "," + tuple.getStringByField(tsField) + "," + TupleUtil.getLongValue(tuple, sumField) + "," + TupleUtil.getLongValue(tuple, cntField);
        }
        return accFlat;
    }

    public String genHistoryFlat(String sessionHistory, Tuple tuple, String tsField, String sumField, String cntField, int limitSize) {
        String historyFlat = null;

        if (TupleUtil.getStringValue(tuple, tsField) == null || TupleUtil.getLongValue(tuple, sumField) == null  || TupleUtil.getLongValue(tuple, cntField) == null) {
            return historyFlat;
        }

        if (sessionHistory != null) {
            NavigableMap<Long, Map<String, Long>> map = StringToMap(sessionHistory, 0, sessionHistoryIndexs, limitSize);
            historyFlat = MapToString(map, Long.valueOf(tuple.getStringByField(tsField)), TupleUtil.getLongValue(tuple, sumField), TupleUtil.getLongValue(tuple, cntField));
        } else {
            historyFlat = tuple.getStringByField(tsField) + "," + TupleUtil.getLongValue(tuple, sumField) + "," + TupleUtil.getLongValue(tuple, cntField);
        }
        return historyFlat;
    }
}

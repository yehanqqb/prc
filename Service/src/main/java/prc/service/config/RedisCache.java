package prc.service.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 *
 * @author cen
 **/
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
public class RedisCache {
    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 左边弹出
     *
     * @param key
     * @return
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return 缓存的对象
     */
    public <T> ValueOperations<String, T> setCacheObject(String key, T value) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        operation.set(key, value);
        return operation;
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     * @return 缓存的对象
     */
    public <T> ValueOperations<String, T> setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        operation.set(key, value, timeout, timeUnit);
        return operation;
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public void deleteObject(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除redis中的全部数据库
     *
     * @param key
     */
    public void deleteAllListData(String key) {
        //保留集合中索引0，0之间的值，其余全部删除  所以list只有有一个值存在
        redisTemplate.opsForList().trim(key, 0, 0);
        //将list中的剩余的一个值也删除
        redisTemplate.opsForList().leftPop(key);
    }


    /**
     * 删除集合对象
     *
     * @param collection
     */
    public void deleteObject(Collection collection) {
        redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> ListOperations<String, T> setCacheList(String key, List<T> dataList) {
        ListOperations listOperation = redisTemplate.opsForList();

        if (null != dataList) {
            int size = dataList.size();
            long oldSize = listOperation.size(key);
            if (oldSize > 400) {
                return listOperation;
            }
            for (int i = 0; i < size; i++) {
                listOperation.leftPush(key, dataList.get(i));

            }
        }
        return listOperation;
    }

    public <T> ListOperations<String, T> setCacheList(String key, List<T> dataList, Long time, TimeUnit unit) {
        ListOperations listOperation = redisTemplate.opsForList();

        if (null != dataList) {
            int size = dataList.size();
            long oldSize = listOperation.size(key);
            if (oldSize > 400) {
                return listOperation;
            }
            for (int i = 0; i < size; i++) {
                listOperation.leftPush(key, dataList.get(i));
                redisTemplate.expire(key, time, unit);
            }
        }
        return listOperation;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(String key) {
        List<T> dataList = new ArrayList<T>();
        ListOperations<String, T> listOperation = redisTemplate.opsForList();
        Long size = listOperation.size(key);

        for (int i = 0; i < size; i++) {
            dataList.add(listOperation.index(key, i));
        }
        return dataList;
    }

    public <T> T getCacheListKeyRandom(String key) {
        ListOperations<String, T> listOperation = redisTemplate.opsForList();
        Long size = listOperation.size(key);
        Random random = new Random();
        return listOperation.index(key, random.nextInt(size.intValue()));
    }

    /**
     * 从左边追加元素
     *
     * @param key
     * @param appendData
     * @param <T>
     */
    public <T> void appendCacheList(String key, List<T> appendData) {
        redisTemplate.opsForList().leftPushAll(key, appendData);
    }

    /**
     * 从左边添加一个可用的元素
     *
     * @param key
     * @param oneValue
     * @param <T>
     */
    public <T> void appendCacheListOneValue(String key, T oneValue) {
        redisTemplate.opsForList().leftPush(key, oneValue);
    }

    /**
     * 获取元素后进行放入
     *
     * @param key
     */
    public Object getCacheAndPushValue(String key) {
        Object obj = redisTemplate.opsForList().rightPop(key);
        return obj;
    }

    public Object getCacheAndLeftPop(String key) {
        Object obj = redisTemplate.opsForList().leftPop(key);
        return obj;
    }

    public Object getCacheRandom(String key, Integer index) {
        Object obj = redisTemplate.opsForList().index(key, index);
        return obj;
    }

    public Object getCacheAndPushLeftValue(String key) {
        Object obj = redisTemplate.opsForList().rightPopAndLeftPush(key, key);
        return obj;
    }


    /**
     * 删除所有的元素保护的 appendDat
     *
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void deleteCacheValue(String key, T value) {
        redisTemplate.opsForList().remove(key, 0, value);
    }


    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }


    public <T> Set<T> getCacheZSet(String key) {
        return redisTemplate.opsForZSet().range(key, 0, -1);
    }


    public Long getZsize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     * @return
     */
    public <T> HashOperations<String, String, T> setCacheMap(String key, Map<String, T> dataMap) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        if (null != dataMap) {
            for (Map.Entry<String, T> entry : dataMap.entrySet()) {
                hashOperations.put(key, entry.getKey(), entry.getValue());
            }
        }
        return hashOperations;
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(String key) {
        Map<String, T> map = redisTemplate.opsForHash().entries(key);
        return map;
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }


    /**
     * set add key
     *
     * @param key
     * @param obj
     * @param score
     */
    public void zAdd(String key, Object obj, Long score) {
        redisTemplate.opsForZSet().add(key, obj, score);
    }


    /**
     * 左边推入队列
     *
     * @param key
     * @param o
     * @return
     */
    public Long lPush(String key, Object o) {
        return redisTemplate.opsForList().leftPush(key, o);
    }

    public Long rPush(String key, Object o) {
        return redisTemplate.opsForList().rightPush(key, o);
    }


    public void removeList(String key, Object o) {
        redisTemplate.opsForList().remove(key, 1, o);
    }

    public void removeSet(String key, Object o) {
        redisTemplate.opsForZSet().remove(key, 1, o);
    }


    //移除所有的等于value的数据
    public void removeListAll(String key, Object o) {
        redisTemplate.opsForList().remove(key, 0, o);
    }


    public Long size(String key) {
        return redisTemplate.opsForList().size(key);
    }


    public Set<Object> zDelKey(String key, Long min, Long max) {
        //遍历出到期的单
        Set<Object> set = redisTemplate.opsForZSet().rangeByScore(key, min, max);
        //删除到期单
        redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
        return set;
    }

    public Set<JSONObject> zDelJSON(String key, Long min, Long max) {
        //遍历出到期的单
        Set<JSONObject> set = redisTemplate.opsForZSet().rangeByScore(key, min, max);
        //删除到期单
        redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
        return set;
    }

}


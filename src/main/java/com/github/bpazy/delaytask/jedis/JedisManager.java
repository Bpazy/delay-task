package com.github.bpazy.delaytask.jedis;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author ziyuan
 * created on 2020/1/14
 */
public class JedisManager {
    private JedisPool jedisPool;

    public JedisManager(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public Jedis getJedis() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Jedis.class);
        Callback callback = new JedisInvocationHandler(jedisPool);
        enhancer.setCallback(callback);

        return (Jedis) enhancer.create();
    }
}

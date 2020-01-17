package com.github.bpazy.delaytask.jedis;

import net.sf.cglib.proxy.InvocationHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;

/**
 * @author ziyuan
 * created on 2020/1/14
 */
public class JedisInvocationHandler implements InvocationHandler {
    private JedisPool jedisPool;

    public JedisInvocationHandler(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Jedis jedis = jedisPool.getResource();
        Object ret;
        try {
            ret = method.invoke(jedis, args);
        } finally {
            jedis.close();
        }
        return ret;
    }
}

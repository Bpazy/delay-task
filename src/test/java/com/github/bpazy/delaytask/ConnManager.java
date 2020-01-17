package com.github.bpazy.delaytask;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @author ziyuan
 * created on 2020/1/17
 */
@Slf4j
public class ConnManager<T> {
    private Class<T> mapper;
    private SqlSessionFactory sqlSessionFactory;

    public ConnManager(Class<T> mapper, SqlSessionFactory sqlSessionFactory) {
        this.mapper = mapper;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public T getMapper() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(mapper);
        Callback callback = (InvocationHandler) (proxy, method, args) -> {
            Object invoke;
            try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                try {
                    invoke = method.invoke(sqlSession.getMapper(ConnManager.this.mapper), args);
                } catch (Throwable e) {
                    log.error("SQL执行错误", e);
                    throw e.getCause();
                }
            }
            return invoke;
        };
        enhancer.setCallback(callback);

        //noinspection unchecked
        return (T) enhancer.create();
    }
}

package com.github.bpazy.delaytask;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author ziyuan
 * created on 2020/1/15
 */
public class TestDelayTaskManager implements DelayTaskManager {
    private DelayTaskMapper delayTaskMapper;

    public TestDelayTaskManager() {
        Configuration configuration = new Configuration(new Environment("development", new JdbcTransactionFactory(), getDataSource()));
        configuration.setLogImpl(Slf4jImpl.class);
        configuration.addMapper(DelayTaskMapper.class);
        configuration.setMapUnderscoreToCamelCase(true);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

        delayTaskMapper = new ConnManager<>(DelayTaskMapper.class, sqlSessionFactory).getMapper();
    }

    private DataSource getDataSource() {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL("");
        ds.setUser("");
        ds.setPassword("");
        return ds;
    }

    @Override
    public List<DelayTask> queryDelayTasks(List<String> taskIds) {
        return delayTaskMapper.queryDelayTasks(taskIds);
    }

    @Override
    public void addDelayTask(DelayTask delayTask) {
        delayTaskMapper.addDelayTask(delayTask);
    }

    @Override
    public void updateDelayTask(DelayTask delayTask) {
        delayTaskMapper.updateDelayTask(delayTask);
    }
}

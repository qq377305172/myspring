package com.jing.util;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;

import javax.jms.ConnectionFactory;

/**
 * @author Admin
 * @title: ActiveMQUtil
 * @projectName demo
 * @description: TODO
 * @date 2020/3/26 20:55
 */
public class ActiveMQUtil {
    PooledConnectionFactory pooledConnectionFactory = null;

    public ConnectionFactory init(String brokerUrl) {

        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        //加入连接池
        pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(factory);
        //出现异常时重新连接
        pooledConnectionFactory.setReconnectOnException(true);
        //
        pooledConnectionFactory.setMaxConnections(5);
        pooledConnectionFactory.setExpiryTimeout(10000);
        return pooledConnectionFactory;
    }

    public ConnectionFactory getConnectionFactory() {
        return pooledConnectionFactory;
    }
}


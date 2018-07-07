//package com.lanjoys.framework.common.util;
//
//import com.lanjoys.boss.App;
//import lombok.Getter;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//import redis.clients.jedis.JedisPoolConfig;
//import redis.clients.jedis.Protocol;
//
//import javax.annotation.PreDestroy;
//
//@Configuration
//@EnableConfigurationProperties(JedisProperty.class)
//public class JedisUtils {
//    @Getter
//    private final JedisPool pool;
//    public JedisUtils(JedisProperty config) {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(config.getPool().getMaxTotal());
//        poolConfig.setMaxIdle(config.getPool().getMaxIdle());
//        poolConfig.setMinIdle(config.getPool().getMinIdle());
//        poolConfig.setMaxWaitMillis(config.getPool().getMaxWaitMillis());
//        poolConfig.setTestOnBorrow(config.getPool().isTestOnBorrow());
//        poolConfig.setTestOnCreate(config.getPool().isTestOnCreate());
//        poolConfig.setTestOnReturn(config.getPool().isTestOnReturn());
//        pool = new JedisPool(poolConfig, config.getHost(), config.getPort(), Protocol.DEFAULT_TIMEOUT, config.getPassword());
//    }
//
//    public static Object execute(Command command) {
//        JedisUtils jedisUtils = App.context().getBean(JedisUtils.class);
//        try (Jedis jedis = jedisUtils.getPool().getResource()) {
//            return command.execute(jedis);
//        }
//    }
//
//    public static void execute(VoidCommand command) {
//        JedisUtils jedisUtils = App.context().getBean(JedisUtils.class);
//        try (Jedis jedis = jedisUtils.getPool().getResource()) {
//            command.execute(jedis);
//        }
//    }
//
//    @PreDestroy
//    public void destroy() {
//        if (pool != null)
//            pool.destroy();
//    }
//
//
//    @FunctionalInterface
//    public interface Command {
//        Object execute(Jedis jedis);
//    }
//
//    @FunctionalInterface
//    public interface VoidCommand {
//        void execute(Jedis jedis);
//    }
//
//}

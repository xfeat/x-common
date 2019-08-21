package cn.ocoop.framework.common.util;//package com.lanjoys.framework.common.util;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//@ConfigurationProperties(prefix = JedisProperty.PREFIX)
//@Getter
//@Setter
//public class JedisProperty {
//    public static final String PREFIX = "jedis";
//    private String host = "127.0.0.1";
//    private int port = 6379;
//    private String password;
//    private Pool pool = new Pool();
//
//    @Getter
//    @Setter
//    public static class Pool {
//        private int maxTotal = GenericObjectPoolConfig.DEFAULT_MAX_TOTAL;
//        private int maxIdle = GenericObjectPoolConfig.DEFAULT_MAX_IDLE;
//        private int minIdle = GenericObjectPoolConfig.DEFAULT_MIN_IDLE;
//        private long maxWaitMillis = GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS;
//        private boolean testOnBorrow = GenericObjectPoolConfig.DEFAULT_TEST_ON_BORROW;
//        private boolean testOnReturn = GenericObjectPoolConfig.DEFAULT_TEST_ON_RETURN;
//        private boolean testOnCreate = GenericObjectPoolConfig.DEFAULT_TEST_ON_CREATE;
//    }
//}

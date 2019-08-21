package cn.ocoop.framework.common.util;//package com.lanjoys.framework.common.util;
//
//import com.lanjoys.boss.App;
//import org.springframework.data.redis.core.StringRedisTemplate;
//
//public class SnowFlakeV1 {
//    private static final String ID_WORK_INDEX = "id_work:index";
//
//    /**
//     * 起始的时间戳
//     */
//    private final static long START_STMP = 1527572507115L;
//
//    /**
//     * 每一部分占用的位数
//     */
//    private final static long SEQUENCE_BIT = 12; //序列号占用的位数
//    private final static long MACHINE_BIT = 5;   //机器标识占用的位数
//    private final static long DATACENTER_BIT = 5;//数据中心占用的位数
//
//    /**
//     * 每一部分的最大值
//     */
//    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
//    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
//    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
//
//    /**
//     * 每一部分向左的位移
//     */
//    private final static long MACHINE_LEFT = SEQUENCE_BIT;
//    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
//    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
//
//    private long datacenterId;  //数据中心
//    private long machineId;     //机器标识
//    private long sequence = 0L; //序列号
//    private long lastStmp = -1L;//上一次时间戳
//    private static final SnowFlakeV1 SNOW_FLAKE_V_1;
//    static {
//        Long index = App.context().getBean(StringRedisTemplate.class).opsForValue().increment(ID_WORK_INDEX, 1);
//        SNOW_FLAKE_V_1 = new SnowFlakeV1(index,index);
//    }
//
//    public static long next() {
//        return SNOW_FLAKE_V_1.nextValue();
//    }
//
//    public SnowFlakeV1(long datacenterId, long machineId) {
//        if (datacenterId < 0) {
//            throw new IllegalArgumentException("datacenterId can't be or less than 0");
//        }
//        if (datacenterId > MAX_DATACENTER_NUM) {
//            datacenterId = datacenterId % MAX_DATACENTER_NUM;
//        }
//
//        if (machineId < 0) {
//            throw new IllegalArgumentException("machineId can't be less than 0");
//        }
//        if (machineId > MAX_MACHINE_NUM) {
//            machineId = machineId % MAX_MACHINE_NUM;
//        }
//        this.datacenterId = datacenterId;
//        this.machineId = machineId;
//    }
//
//    /**
//     * 产生下一个ID
//     *
//     * @return
//     */
//    public synchronized long nextValue() {
//        long currStmp = getNewstmp();
//        if (currStmp < lastStmp) {
//            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
//        }
//
//        if (currStmp == lastStmp) {
//            //相同毫秒内，序列号自增
//            sequence = (sequence + 1) & MAX_SEQUENCE;
//            //同一毫秒的序列数已经达到最大
//            if (sequence == 0L) {
//                currStmp = getNextMill();
//            }
//        } else {
//            //不同毫秒内，序列号置为0
//            sequence = 0L;
//        }
//
//        lastStmp = currStmp;
//
//        return (currStmp - START_STMP) << TIMESTMP_LEFT //时间戳部分
//                | datacenterId << DATACENTER_LEFT       //数据中心部分
//                | machineId << MACHINE_LEFT             //机器标识部分
//                | sequence;                             //序列号部分
//    }
//
//    private long getNextMill() {
//        long mill = getNewstmp();
//        while (mill <= lastStmp) {
//            mill = getNewstmp();
//        }
//        return mill;
//    }
//
//    private long getNewstmp() {
//        return System.currentTimeMillis();
//    }
//
//
//
//}

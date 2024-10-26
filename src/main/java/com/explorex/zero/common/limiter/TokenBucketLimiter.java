package com.explorex.zero.common.limiter;

import com.explorex.zero.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketLimiter {
    private final long capacity; // 桶的最大容量
    private final double refillRate; // 每秒填充令牌的速度
    private final AtomicLong tokens; // 当前令牌数量 由于令牌是原子类，所以扣减和增加都是线程安全的
    private final AtomicLong lastRefillTime; // 上次填充令牌的时间戳（毫秒）


    public TokenBucketLimiter(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = new AtomicLong(capacity);
        this.lastRefillTime = new AtomicLong(System.currentTimeMillis());
    }

    /**
     * 尝试获取一个令牌。
     *
     * @return 如果成功获取令牌返回 true，否则返回 false。
     */
    public synchronized boolean tryAcquire() {
        refillTokens();
        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    /**
     * 填充令牌。
     */
    private void refillTokens() {
        long now = System.currentTimeMillis();
        long timeSinceLastRefill = now - lastRefillTime.get();
        long tokensToAdd = (long) (timeSinceLastRefill / 1000.0 * refillRate);

        if (tokensToAdd > 0) {
            long oldTokenCount = tokens.get();
            long newTokenCount = Math.min(tokens.get() + tokensToAdd, capacity);
            //动态令牌桶：这里通过mapper扣减库表的令牌数量 tokens.get()-oldTokenCount，然后往redis和caffeine里增加令牌tokens.get()-oldTokenCount
            //静态令牌桶：直接放完整的令牌就行，
            tokens.set(newTokenCount);
            lastRefillTime.set(now);
        }
    }

    /**
     * 获取当前令牌数量。
     *
     * @return 当前令牌数量。
     */
    public long getAvailableTokens() {
        refillTokens();
        return tokens.get();
    }
}
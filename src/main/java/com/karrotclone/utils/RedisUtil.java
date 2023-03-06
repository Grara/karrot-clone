package com.karrotclone.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    private final String REDIS_KEY_PREFIX = "LOGOUT_";

    private final String EXPIRED_DURATION = "EXPIRE_DURATION";

    public String getData(String key){
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setData(String key, String value){
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key, value);
    }// 만료 기간을 가지지 않은 데이터를 생성 또는 설정하는 메소드(아직 안씀)

    public void setDataExpire(String key, String value, long duration){
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuation = Duration.ofMillis(duration);
        valueOperations.set(key, value, duration);
    }// 이미 생성되어 있거나 생성되어 있지 않은 데이터를 만료기간까지 설정해 저장하는 메소드

    public void deleteData(String key){
        stringRedisTemplate.delete(key);
    } //로그아웃시에 리프레쉬 토큰을 삭제하기 위한 메소드
    public void setBlackList(String key, Object o, Long second) {
        stringRedisTemplate.opsForValue().set(REDIS_KEY_PREFIX + key, o.toString(), Duration.ofMillis(second));
    } //로그아웃시에 기존의 액세스 토큰을 로그아웃된 토큰으로 만료되었음을 표시하게 하기위한 메소드
    public boolean hasKeyBlackList(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(REDIS_KEY_PREFIX + key));
    } //토큰이 탈취되어 잘못된 인증을 시도하는 접근이 있는지 확인하는 메소드
}
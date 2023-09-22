package com.zihuv.userservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.DistributedCache;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.designpattern.chain.AbstractChainContext;
import com.zihuv.index12306.frameworks.starter.user.core.UserContext;
import com.zihuv.userservice.common.enums.UserChainMarkEnum;
import com.zihuv.userservice.mapper.UserInfoMapper;
import com.zihuv.userservice.model.entity.UserInfo;
import com.zihuv.userservice.model.param.UserDeletionParam;
import com.zihuv.userservice.model.param.UserRegisterParam;
import com.zihuv.userservice.model.param.UserUpdateParam;
import com.zihuv.userservice.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.zihuv.userservice.common.constant.RedisKeyConstant.USER_REGISTER_REUSE_SHARDING;
import static com.zihuv.userservice.utils.UserReuseUtil.hashShardingIdx;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    private final DistributedCache distributedCache;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AbstractChainContext<UserRegisterParam> abstractChainContext;
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(UserRegisterParam userRegisterParam) {
        // 责任链校验参数：1.各参数不能为空 2.该用户名没有被注册过
        abstractChainContext.handler(UserChainMarkEnum.CHECK_USER_PARAM_FILTER.name(), userRegisterParam);

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userRegisterParam, userInfo);

        this.save(userInfo);

        String username = userInfo.getUsername();
        // 将用户名添加至布隆过滤器
        userRegisterCachePenetrationBloomFilter.add(username);
        // 将用户名分片，存储到 redis 当中
        redisTemplate.opsForSet().add(USER_REGISTER_REUSE_SHARDING + hashShardingIdx(username), username);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUser(UserUpdateParam userUpdateParam) {

    }

    @Override
    public UserInfo queryUserByUserId(String userId) {
        UserInfo userInfo = this.getById(userId);
        if (Objects.isNull(userInfo)) {
            throw new ClientException("该用户不存在");
        }
        return userInfo;
    }

    @Override
    public UserInfo queryUserByUsername(String username) {
        LambdaQueryWrapper<UserInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInfo::getUsername, username);
        UserInfo userInfo = this.getOne(lqw);
        if (Objects.isNull(userInfo)) {
            throw new ClientException("该用户不存在");
        }
        return userInfo;
    }

    @Override
    public void update(UserInfo requestParam) {

    }

    @Override
    public Boolean hasUsername(String username) {
        // 使用布隆过滤器对判断用户名是否存在
        boolean hasUsername = userRegisterCachePenetrationBloomFilter.contains(username);
        if (hasUsername) {
            // 布隆过滤器返回 true，代表这个用户名可能存在。从缓存中查询用户名。
            return redisTemplate.opsForSet().isMember(USER_REGISTER_REUSE_SHARDING + hashShardingIdx(username), username);
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletion(UserDeletionParam userDeletionParam) {
        String username = UserContext.getUsername();
        if (!Objects.equals(username, userDeletionParam.getUsername())) {
            throw new ClientException("登录用户和注销用户不一致");
        }
        this.removeById(UserContext.getUserId());
        redisTemplate.opsForSet().remove(USER_REGISTER_REUSE_SHARDING + hashShardingIdx(username), username);
    }
}
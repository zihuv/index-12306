local tokenBucketHashKey = tostring(KEYS[1])
local bucketMap = cjson.decode(KEYS[2])

-- 查询 token 是否充足
-- eg: <北京_德州_0，2 张票>
for key, value in pairs(bucketMap) do
    local routeToken = tonumber(redis.call('hget', tokenBucketHashKey, key))
    -- token 不足，返回 -1
    if (routeToken - value) < 0 then
        return -1
    end
end

-- 开始真正扣除 token
for key, value in pairs(bucketMap) do
    redis.call('hincrby', tokenBucketHashKey, key, -value)
end

-- 执行一切正常，返回 1
return 1

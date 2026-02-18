local stock = tonumber(redis.call("GET", KEYS[1]))
local reserved = tonumber(redis.call("GET", KEYS[2]))
local qty = tonumber(ARGV[1])
local orderId = ARGV[2]

if stock == nil or reserved == nil then
  return -1
end

if reserved < qty then
  return -2
end

redis.call("DECRBY", KEYS[2], qty)
redis.call("INCRBY", KEYS[1], qty)
redis.call("SREM", KEYS[3], orderId)

return stock + qty

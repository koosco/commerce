local stock = tonumber(redis.call("GET", KEYS[1]))
local reserved = tonumber(redis.call("GET", KEYS[2])) or 0
local qty = tonumber(ARGV[1])
local orderId = ARGV[2]

if stock == nil then
  return -1 -- stock key not found
end

if stock < qty then
  return -2 -- not enough stock
end

redis.call("DECRBY", KEYS[1], qty)
redis.call("INCRBY", KEYS[2], qty)
redis.call("SADD", KEYS[3], orderId)

return stock - qty

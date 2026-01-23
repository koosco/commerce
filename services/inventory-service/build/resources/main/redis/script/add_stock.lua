local stock = tonumber(redis.call("GET", KEYS[1]))
local increase = tonumber(ARGV[1])

if stock == nil then
  return -1
end

redis.call("INCRBY", KEYS[1], increase)
return stock + increase

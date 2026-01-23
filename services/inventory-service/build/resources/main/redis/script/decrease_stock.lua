local stock = tonumber(redis.call("GET", KEYS[1]))
local decrease = tonumber(ARGV[1])

if stock == nil then
  return -1
end

if stock < decrease then
  return -2
end

redis.call("DECRBY", KEYS[1], decrease)
return stock - decrease

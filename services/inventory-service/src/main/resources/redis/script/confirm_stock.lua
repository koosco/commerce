local reserved = tonumber(redis.call("GET", KEYS[1]))
local qty = tonumber(ARGV[1])

if reserved == nil then
  return -1 -- reserved key not found
end

if reserved < qty then
  return -2 -- invalid confirm
end

redis.call("DECRBY", KEYS[1], qty)
return reserved - qty

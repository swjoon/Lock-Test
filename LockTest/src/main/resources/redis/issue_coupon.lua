-- KEYS[1] = stock_key     : coupon:42:stock
-- ARGV[1] = issueAmount   : 1

local stock = redis.call('GET', KEYS[1])
if not stock then
  return -2               -- 재고키 없음
end
stock = tonumber(stock)
if stock < tonumber(ARGV[1]) then
  return -1               -- 품절
end
return redis.call('DECRBY', KEYS[1], ARGV[1]) -- 차감 후 남은 재고 반환
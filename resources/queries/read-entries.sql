SELECT
  e.id,
  e.decreasing_account_id,
  da.category AS decreasing_account_category,
  da.name AS decreasing_account_name,
  e.increasing_account_id,
  ia.category AS increasing_account_category,
  ia.name AS increasing_account_name,
  e.description,
  e.value,
  e.entry_date,
  e.post_date
FROM entry AS e
LEFT JOIN account AS da ON e.decreasing_account_id = da.id
LEFT JOIN account AS ia ON e.increasing_account_id = ia.id
ORDER BY post_date DESC, e.id DESC

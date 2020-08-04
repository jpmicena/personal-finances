SELECT
  e.id,
  da.category || ":" || da.name AS decreasing_account,
  ia.category || ":" || ia.name AS increasing_account,
  e.description,
  e.value,
  e.post_date,
  e.planned_date
FROM entry AS e
LEFT JOIN account AS da ON e.decreasing_account_id = da.id
LEFT JOIN account AS ia ON e.increasing_account_id = ia.id
ORDER BY post_date DESC
LIMIT ?

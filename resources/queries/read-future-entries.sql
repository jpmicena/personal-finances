SELECT
  e.id,
  da.category || ":" || da.name AS decreasing_account,
  ia.category || ":" || ia.name AS increasing_account,
  e.description,
  e.value,
  e.post_date,
  e.due_date
FROM entry AS e
LEFT JOIN account AS da ON e.decreasing_account_id = da.id
LEFT JOIN account AS ia ON e.increasing_account_id = ia.id
WHERE e.post_date IS NULL
ORDER BY due_date DESC, e.id DESC

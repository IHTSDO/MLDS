create table mlds_889_usage_count_backup
as 
select * from commercial_usage_count;

create table mlds_889_usage_count_deleted
as 
SELECT DISTINCT b.*
FROM commercial_usage_count a, commercial_usage_count b
WHERE a.country_iso_code_2 = b.country_iso_code_2
AND a.commercial_usage_id = b.commercial_usage_id
AND a.commercial_usage_count_id > b.commercial_usage_count_id
ORDER BY b.commercial_usage_id, b.commercial_usage_count_id, b.country_iso_code_2;

delete from commercial_usage_count
where commercial_usage_count_id in (select commercial_usage_count_id from mlds_889_usage_count_deleted);



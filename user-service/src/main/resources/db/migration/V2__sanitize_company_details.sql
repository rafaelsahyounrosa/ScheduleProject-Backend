-- VXX__sanitize_company_details.sql
UPDATE companies
SET details = JSON_OBJECT()
WHERE details IS NULL OR JSON_VALID(details) = 0;

ALTER TABLE companies
    MODIFY details JSON;

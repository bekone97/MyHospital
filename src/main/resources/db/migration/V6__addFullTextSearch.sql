ALTER TABLE my_hospital.persons
    ADD FULLTEXT INDEX full_text_search_idx (first_name, surname, phone_number, patronymic) VISIBLE;

ALTER TABLE history_of_completing_processes MODIFY
    COLUMN date_of_completing DATETIME not null ;
ALTER TABLE history_of_completing_processes ADD COLUMN result_of_completing varchar(528);
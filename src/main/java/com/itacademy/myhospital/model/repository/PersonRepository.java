package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person,Integer> {

    Person findByUser(User user);

    @Transactional
    @Query(value = "SELECT * FROM persons WHERE MATCH(first_name, surname, phone_number, patronymic) AGAINST(?1)"
            ,nativeQuery = true)
    List<Person> search(String keyword);
    @Transactional
    @Query(value = "SELECT * FROM persons p JOIN users u ON(p.user_id=u.id) where u.username= ?1"
            ,nativeQuery = true)
    Person findPersonByUsernameOfUser(String username);

    Person findByKeyForUser(String key);
    @Transactional
    @Query(value = "select  * from persons p "+
            "JOIN users u on (p.user_id=u.id)"+
            "JOIN roles_users ru on (ru.user_id=u.id)"+
            "JOIN roles r on (ru.role_id=r.id)"+
            "where r.id=(?1) ORDER BY p.id LIMIT ?2 OFFSET ?3",nativeQuery = true)
    List<Person> findLimitAndSortedPersonWithRoleId(Integer roleId,long limit ,long offset);

    @Transactional
    @Query(value = "select count(*) from persons p " +
            "JOIN users u on (p.user_id=u.id)" +
            "JOIN roles_users ru ON (ru.user_id = u.id)"+
            "JOIN roles r ON (ru.role_id=r.id)" +
            "where r.id=(?1)",nativeQuery = true)
    int findNumberOfPersonWithRoleId(Integer roleId);

    @Transactional
    @Query(value ="select * from persons p " +
            "JOIN users u on (p.user_id=u.id)" +
            "JOIN roles_users ru ON (ru.user_id = u.id)"+
            "JOIN roles r ON (ru.role_id=r.id)" +
            "where p.id =(?1) AND r.id=(?2)",nativeQuery = true )
   Person findPersonByIdAndRoleId(Integer personId, Integer roleId);

    @Transactional
    @Query(value ="select * from persons p " +
            "JOIN users u on (p.user_id=u.id)" +
            "JOIN roles_users ru ON (ru.user_id = u.id)"+
            "JOIN roles r ON (ru.role_id=r.id)" +
            "where  r.id=(?1)",nativeQuery = true )
    List<Person> findPersonsByRoleId(Integer roleId);
}

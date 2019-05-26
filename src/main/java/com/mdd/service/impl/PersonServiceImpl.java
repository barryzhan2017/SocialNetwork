package com.mdd.service.impl;

import com.mdd.dao.PersonDao;
import com.mdd.entity.Person;
import com.mdd.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

    private PersonDao personDao;

    @Autowired
    public PersonServiceImpl(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public Person findPersonById(long id) {
        return personDao.findById(id).orElse(null);
    }
}

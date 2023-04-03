package com.psajd.quizletBot.services;

import com.psajd.quizletBot.entities.Person;

public interface PersonService {

    Person addPerson(Person person);

    Person updatePerson(Person person);

    void deletePerson(Person person);

    Person getPersonById(Long personId);

}

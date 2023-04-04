package com.psajd.quizletBot.services;

import com.psajd.quizletBot.entities.Person;

import java.util.List;

public interface PersonService {

    Person addPerson(Person person);

    Person updatePerson(Person person);

    void deletePerson(Person person);

    Person getPersonById(Long personId);

    List<Person> getAll();
}

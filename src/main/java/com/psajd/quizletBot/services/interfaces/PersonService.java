package com.psajd.quizletBot.services.interfaces;

import com.psajd.quizletBot.entities.Person;

import java.util.List;

public interface PersonService {

    Person addPerson(Person person);

    Person getPersonById(Long personId);

}

package com.psajd.quizletBot.services;

import com.psajd.quizletBot.entities.Person;
import com.psajd.quizletBot.repositories.PersonRepository;
import com.psajd.quizletBot.services.interfaces.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public Person addPerson(Person person) {
        personRepository.save(person);
        return person;
    }

    @Override
    public Person getPersonById(Long personId) {
        return personRepository.findById(personId).orElse(null);
    }
}

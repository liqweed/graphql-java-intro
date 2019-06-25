package org.liqweed.graphql.persistence

import org.liqweed.graphql.model.Person
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class InMemoryPersonRepository : PersonRepository {

    private val people : MutableList<Person> = mutableListOf()

    override fun getPerson(personId: UUID): Person? {
        return people.find { it.id == personId }
    }

    override fun getActorByName(name: String): Person? {
        return people.filter { it.name == name }.find { it.rolesActed.isNotEmpty() }
    }

    override fun getDirectorByName(name: String): Person? {
        return people.filter { it.name == name }.find { it.directed.isNotEmpty() }
    }

    override fun getPeople(limit: Int, offset: Long): List<Person> {
        return people.drop(offset.toInt()).take(limit)
    }

    override fun add(name: String): Person {
        return Person(name).also {
            people.add(it)
        }
    }
}
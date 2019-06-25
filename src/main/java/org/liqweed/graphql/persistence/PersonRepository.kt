package org.liqweed.graphql.persistence

import org.liqweed.graphql.model.Person
import java.util.UUID

interface PersonRepository {

    fun getPerson(personId: UUID): Person?

    fun getActorByName(name: String): Person?

    fun getDirectorByName(name: String): Person?

    fun getPeople(limit: Int = 10, offset: Long = 0): List<Person>

    fun add(name: String): Person
}
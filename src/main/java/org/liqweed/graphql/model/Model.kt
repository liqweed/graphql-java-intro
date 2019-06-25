package org.liqweed.graphql.model

import java.util.UUID

data class Movie(
    val title: String,
    val release: Int,
    val id: UUID = UUID.randomUUID(),
    var director: Person? = null,
    val cast: MutableList<Role> = mutableListOf()
)

data class Person(
    val name: String,
    val id: UUID = UUID.randomUUID(),
    val rolesActed: MutableList<Role> = mutableListOf(),
    var directed: MutableList<UUID> = mutableListOf()
) {
    fun isDirector(): Boolean = directed.isNotEmpty()
}

data class Role(val movieId: UUID, val name: String, val actorId: UUID)

data class InputRole(val movieId: UUID, val actorId: UUID, val name: String)
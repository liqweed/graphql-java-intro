package org.liqweed.graphql.persistence

import org.liqweed.graphql.model.Movie
import org.liqweed.graphql.model.Role
import java.util.UUID

interface RoleRepository {

    fun setDirector(movieId: UUID, personId: UUID): Movie?

    fun addRole(movieId: UUID, name: String, actorId: UUID): Role?
}

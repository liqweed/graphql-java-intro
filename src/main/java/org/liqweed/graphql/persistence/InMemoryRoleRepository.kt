package org.liqweed.graphql.persistence

import org.liqweed.graphql.model.Movie
import org.liqweed.graphql.model.Role
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class InMemoryRoleRepository(
    private val personRepository: PersonRepository,
    private val movieRepository: MovieRepository,
    private val changeTracking: MovieChangeTracking
) : RoleRepository {

    override fun setDirector(movieId: UUID, personId: UUID): Movie? {
        val movie = movieRepository.getMovie(movieId)
        val person = personRepository.getPerson(personId)
        if (movie != null && person != null) {
            movie.director = person
            person.directed.add(movie.id)
            changeTracking.movieChanged(movie)
        }
        return movie
    }

    override fun addRole(movieId: UUID, name: String, actorId: UUID): Role? {
        val movie = movieRepository.getMovie(movieId)
        val person = personRepository.getPerson(actorId)
        return if (movie != null && person != null) {
            Role(movieId, name, actorId).also {
                movie.cast.add(it)
                person.rolesActed.add(it)
                changeTracking.movieChanged(movie)
            }
        } else null
    }
}
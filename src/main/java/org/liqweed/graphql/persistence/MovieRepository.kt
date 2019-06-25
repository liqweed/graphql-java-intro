package org.liqweed.graphql.persistence

import org.liqweed.graphql.model.Movie
import java.util.UUID

interface MovieRepository {

    fun getMovies(limit: Int = 10, offset: Long = 0, orderBy: MovieOrderBy = MovieOrderBy.TITLE): List<Movie>

    fun getMovie(movieId: UUID): Movie?

    fun getMovieByTitle(title: String): Movie?

    fun addMovie(title: String, release: Int): Movie
}

enum class MovieOrderBy {
    TITLE, RELEASE
}
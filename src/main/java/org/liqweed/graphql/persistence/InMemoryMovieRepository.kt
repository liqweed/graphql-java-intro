package org.liqweed.graphql.persistence

import org.liqweed.graphql.model.Movie
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class InMemoryMovieRepository(private val changeTracking: MovieChangeTracking) : MovieRepository {

    private val movies: MutableList<Movie> = mutableListOf()

    override fun getMovie(movieId: UUID): Movie? {
        return movies.find { it.id == movieId }
    }

    override fun getMovies(limit: Int, offset: Long, orderBy: MovieOrderBy): List<Movie> {
        val sortedMovies = when (orderBy) {
            MovieOrderBy.TITLE -> movies.sortedBy { it.title }
            MovieOrderBy.RELEASE -> movies.sortedBy { it.release }
        }

        return sortedMovies
            .drop(offset.toInt())
            .take(limit)
    }

    override fun getMovieByTitle(title: String): Movie? {
        return movies.find { it.title == title }
    }

    override fun addMovie(title: String, release: Int): Movie {
        return Movie(title, release).also {
            movies.add(it)
            changeTracking.movieChanged(it)
        }
    }
}

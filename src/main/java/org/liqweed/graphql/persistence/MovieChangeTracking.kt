package org.liqweed.graphql.persistence

import org.liqweed.graphql.model.Movie
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component
import reactor.core.publisher.EmitterProcessor

@Component
class MovieChangeTracking {
    private val processor: EmitterProcessor<Movie> = EmitterProcessor.create()

    val publisher: Publisher<Movie> = processor

    fun movieChanged(updatedMovie: Movie) {
        processor.onNext(updatedMovie)
    }
}
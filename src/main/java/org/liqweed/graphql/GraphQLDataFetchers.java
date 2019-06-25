package org.liqweed.graphql;

import graphql.schema.DataFetcher;
import org.liqweed.graphql.model.Movie;
import org.liqweed.graphql.model.Person;
import org.liqweed.graphql.model.Role;
import org.liqweed.graphql.persistence.MovieOrderBy;
import org.liqweed.graphql.persistence.MovieRepository;
import org.liqweed.graphql.persistence.PersonRepository;
import org.liqweed.graphql.persistence.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GraphQLDataFetchers {

    private final MovieRepository movieRepository;
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;

    public GraphQLDataFetchers(MovieRepository movieRepository, PersonRepository personRepository, RoleRepository roleRepository) {
        this.movieRepository = movieRepository;
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
    }

    public DataFetcher<List<Movie>> allMovies() {
        return environment -> {
            Integer limit = environment.getArgument("limit");
            Integer first = environment.getArgument("first");
            String orderByArgument = (String) Optional.ofNullable(environment.getArgument("orderBy")).orElse("TITLE");
            MovieOrderBy orderBy = MovieOrderBy.valueOf(orderByArgument);
            return movieRepository.getMovies(limit, first, orderBy);
        };
    }

    public DataFetcher<List<Person>> people() {
        return environment -> {
            Integer limit = environment.getArgument("limit");
            Integer first = environment.getArgument("first");
            return personRepository.getPeople(limit, first);
        };
    }

    public DataFetcher<Movie> movieByTitle() {
        return environment -> {
            String title = environment.getArgument("title");
            return movieRepository.getMovieByTitle(title);
        };
    }

    public DataFetcher<Person> actorByName() {
        return environment -> {
            String name = environment.getArgument("name");
            return personRepository.getActorByName(name);
        };
    }

    public DataFetcher<Person> directorByName() {
        return environment -> {
            String name = environment.getArgument("name");
            return personRepository.getDirectorByName(name);
        };
    }

    public DataFetcher<List<Movie>> actorMovies() {
        return environment -> {
            Person person = environment.getSource();
            return person.getRolesActed()
                    .stream()
                    .map(role -> movieRepository.getMovie(role.getMovieId()))
                    .collect(Collectors.toList());
        };
    }

    public DataFetcher<List<Movie>> directorMovies() {
        return environment -> {
            Person person = environment.getSource();
            return person.getDirected()
                    .stream()
                    .map(movieRepository::getMovie)
                    .distinct()
                    .collect(Collectors.toList());
        };
    }

    public DataFetcher<List<Person>> movieCast() {
        return environment -> {
            Movie movie = environment.getSource();
            if (movie == null) {
                return Collections.emptyList();
            }
            return movie.getCast()
                    .stream()
                    .map(role -> personRepository.getPerson(role.getActorId()))
                    .distinct()
                    .collect(Collectors.toList());
        };
    }

    public DataFetcher<List<Role>> movieRoles() {
        return environment -> {
            Movie movie = environment.getSource();
            return movie.getCast();
        };
    }

    public DataFetcher<Person> roleActor() {
        return environment -> {
            Role role = environment.getSource();
            return personRepository.getPerson(role.getActorId());
        };
    }

    public DataFetcher<Movie> roleMovie() {
        return environment -> {
            Role role = environment.getSource();
            return movieRepository.getMovie(role.getMovieId());
        };
    }
}

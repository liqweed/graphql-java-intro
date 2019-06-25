package org.liqweed.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.idl.TypeRuntimeWiring;
import org.liqweed.graphql.model.InputRole;
import org.liqweed.graphql.model.Movie;
import org.liqweed.graphql.model.Person;
import org.liqweed.graphql.model.Role;
import org.liqweed.graphql.persistence.MovieChangeTracking;
import org.liqweed.graphql.persistence.MovieRepository;
import org.liqweed.graphql.persistence.PersonRepository;
import org.liqweed.graphql.persistence.RoleRepository;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
public class GraphQLMutationFetchers {

    private final MovieRepository movieRepository;
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;
    private final MovieChangeTracking movieChangeTracking;

    public GraphQLMutationFetchers(MovieRepository movieRepository, PersonRepository personRepository, RoleRepository roleRepository, MovieChangeTracking movieChangeTracking) {
        this.movieRepository = movieRepository;
        this.personRepository = personRepository;
        this.roleRepository = roleRepository;
        this.movieChangeTracking = movieChangeTracking;
    }

    public TypeRuntimeWiring wiring() {
        return newTypeWiring("Mutation")
                .dataFetcher("addMovie", addMovie())
                .dataFetcher("addPerson", addPerson())
                .dataFetcher("directorForMovie", directorForMovie())
                .dataFetcher("castRole", castRole())
                .build();
    }

    private DataFetcher<Movie> addMovie() {
        return environment -> {
            String title = environment.getArgument("title");
            Integer release = environment.getArgument("release");
            return movieRepository.addMovie(title, release);
        };
    }

    private DataFetcher<Person> addPerson() {
        return environment -> {
            String name = environment.getArgument("name");
            return personRepository.add(name);
        };
    }

    private DataFetcher<Movie> directorForMovie() {
        return environment -> {
            UUID movieId = environment.getArgument("movieId");
            UUID directorId = environment.getArgument("directorId");
            return roleRepository.setDirector(movieId, directorId);
        };
    }

    private DataFetcher<Role> castRole() {
        return environment -> {
            InputRole inputRole = environment.getArgument("role");
            return roleRepository.addRole(inputRole.getMovieId(), inputRole.getName(), inputRole.getActorId());
        };
    }

    public DataFetcher<Publisher<Movie>> movieSubscription() {
        return environment -> movieChangeTracking.getPublisher();
    }
}

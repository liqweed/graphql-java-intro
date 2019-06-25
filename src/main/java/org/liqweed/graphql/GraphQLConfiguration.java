package org.liqweed.graphql;

import com.google.common.io.Resources;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.execution.SubscriptionExecutionStrategy;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.spring.web.servlet.GraphQLInvocation;
import kotlin.text.Charsets;
import org.liqweed.graphql.model.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.net.URL;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Configuration
public class GraphQLConfiguration {

    private final GraphQLDataFetchers queryFetchers;
    private final GraphQLMutationFetchers mutationFetchers;

    public GraphQLConfiguration(GraphQLDataFetchers queryFetchers, GraphQLMutationFetchers mutationFetchers) {
        this.queryFetchers = queryFetchers;
        this.mutationFetchers = mutationFetchers;
    }

    @Bean
    GraphQL graphQL() {
        return GraphQL.newGraphQL(graphQLSchema())
                .subscriptionExecutionStrategy(new SubscriptionExecutionStrategy())
                .build();
    }

    @Bean
    GraphQLSchema graphQLSchema() {
        String sdl;
        try {
            URL url = Resources.getResource("schema.graphqls");
            sdl = Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read GraphQL schema file", e);
        }
        return buildSchema(sdl);
    }

    private GraphQLSchema buildSchema(String schemaFile) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        RuntimeWiring.Builder wiring = RuntimeWiring.newRuntimeWiring();

        wiring.type(newTypeWiring("Query")
                .dataFetcher("movies", queryFetchers.allMovies())
                .dataFetcher("people", queryFetchers.people())
                .dataFetcher("movieByTitle", queryFetchers.movieByTitle())
                .dataFetcher("actorByName", queryFetchers.actorByName())
                .dataFetcher("directorByName", queryFetchers.directorByName())
        );

        wiring.type(newTypeWiring("Actor")
                .dataFetcher("movies", queryFetchers.actorMovies())
        );

        wiring.type(newTypeWiring("Director")
                .dataFetcher("directed", queryFetchers.directorMovies())
        );

        wiring.type(newTypeWiring("Movie")
                .dataFetcher("cast", queryFetchers.movieCast())
                .dataFetcher("roles", queryFetchers.movieRoles())
        );

        wiring.type(newTypeWiring("Role")
                .dataFetcher("actor", queryFetchers.roleActor())
                .dataFetcher("movie", queryFetchers.roleMovie())
        );

        wiring.type(mutationFetchers.wiring());

        wiring.type(newTypeWiring("Mutation")
                .dataFetcher("movieUpdate", mutationFetchers.movieSubscription()));

        wiring.type(newTypeWiring("Person")
                .typeResolver(env -> {
                    Object object = env.getObject();
                    if (!((Person) object).isDirector()) {
                        return env.getSchema().getObjectType("Director");
                    }
                    return env.getSchema().getObjectType("Actor");
                }));

        return wiring.build();
    }

    @Bean
    @Primary
    /**
     * A mechanism to customize requests execution and inject authentication and environment data.
     */
    public GraphQLInvocation invocation() {
        GraphQL graphQL = graphQL();
        return (invocationData, webRequest) -> {
            ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                    .query(invocationData.getQuery())
                    .operationName(invocationData.getOperationName())
                    .variables(invocationData.getVariables())
                    .context(builder -> builder.of("user", "authenticatedUser1"))
                    .build();
            return graphQL.executeAsync(executionInput);
        };
    }
}

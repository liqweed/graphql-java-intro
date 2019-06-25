package org.liqweed.graphql.persistence

import org.liqweed.graphql.model.Movie
import org.liqweed.graphql.model.Person
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class DemoDataPopulator(val movieRepository: MovieRepository, val personRepository: PersonRepository, val roleRepository: RoleRepository) {

    @PostConstruct
    fun init() {
        val tilda = personRepository.add("Tilda Swinton")
        val jake = personRepository.add("Jake Gyllenhaal")
        val benedict = personRepository.add("Benedict Cumberbatch")
        val bill = personRepository.add("Bill Paxton")

        movieRepository.addMovie("Okja", 2018)
            .director("Joon-ho Bong")
            .addRole(tilda, "Lucy Mirando")
            .addRole(tilda, "Nancy Mirando")
            .addRole(jake, "Johnny Wilcox")
            .addRole("Seo-hyun Ahn", "Mija")

        movieRepository.addMovie("Nightcrawler", 2014)
            .director("Dan Gilroy")
            .addRole(jake, "Louis Bloom")
            .addRole(bill, "Joe Loder")

        movieRepository.addMovie("Dr. Strange", 2016)
            .director("Anthony Russo")
            .addRole(benedict, "Dr. Stephen Strange")
            .addRole(tilda, "The Ancient One")
            .addRole("Mads Mikkelsen", "Kaecilius")

        movieRepository.addMovie("The Grand Budapest Hotel", 2014)
            .director("Wes Anderson")
            .addRole("Ralph Fiennes", "M. Gustave")
            .addRole("Jude Law", "Young Writer")
            .addRole("Harvey Keitel", "Ludwig")
            .addRole(tilda, "Madame D.")

        movieRepository.addMovie("12 Years a Slave ", 2013)
            .director("Steve McQueen")
            .addRole("Chiwetel Ejiofor", "Solomon Northup")
            .addRole(benedict, "Ford")
            .addRole("Michael Kenneth Williams", "Robert")
            .addRole("Michael Fassbender", "Edwin Epps")
            .addRole("Paul Giamatti", "Ludwig")

    }

    fun Movie.addRole(person: Person, roleName: String): Movie {
        roleRepository.addRole(id, roleName, person.id)
        return this
    }

    fun Movie.addRole(name: String, roleName: String): Movie {
        roleRepository.addRole(id, roleName, personRepository.add(name).id)
        return this
    }

    fun Movie.director(person: Person): Movie = roleRepository.setDirector(id, person.id)!!

    fun Movie.director(name: String): Movie = roleRepository.setDirector(id, personRepository.add(name).id)!!
}
package com.johnathanmarksmith.mongodb.example.repository;

import com.foursquare.fongo.Fongo;
import com.johnathanmarksmith.mongodb.example.MongoConfiguration;
import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.Mongo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MongoConfiguration.class})
public class PersonRepositoryTest {

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("demo-test");

    // nosql-unit requirement
    @Autowired private ApplicationContext applicationContext;

    @Autowired private PersonRepository personRepository;

    /**
     * Expected results are in "one-person.json" file
     */
    @Test
    @ShouldMatchDataSet(location = "/two-person.json")
    public void testInsertPersonWithNameJohnathanAndRandomAge() {
        this.personRepository.insertPersonWithNameJohnathan(35);
        this.personRepository.insertPersonWithNameJohnathan(67);
    }

    /**
     * Insert data from "five-person.json" and test countAllPersons method
     */
    @Test
    @UsingDataSet(locations = {"/five-person.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testCountAllPersons() {
        long total = this.personRepository.countAllPersons();

        assertThat(total).isEqualTo(5);
    }

    /**
     * Insert data from "five-person.json" and test countUnderAge method
     */
    @Test
    @UsingDataSet(locations = {"/five-person.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void testCountUnderAge() {
        long total = this.personRepository.countUnderAge();

        assertThat(total).isEqualTo(3);
    }

    @Configuration
    @EnableMongoRepositories
    @ComponentScan(basePackageClasses = {PersonRepository.class})
    // modified to not load configs from com.johnathanmarksmith.mongodb.example.MongoConfiguration
    @PropertySource("classpath:application.properties")
    static class PersonRepositoryTestConfiguration extends AbstractMongoConfiguration {

        @Override
        protected String getDatabaseName() {
            return "demo-test";
        }

        @Bean
        @Override
        public Mongo mongo() {
            // uses fongo for in-memory tests
            return new Fongo("mongo-test").getMongo();
        }

        @Override
        protected String getMappingBasePackage() {
            return "com.johnathanmarksmith.mongodb.example.domain";
        }

    }
}

package com.github.ralitsaZh.mfa;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class PostgresContainerTest {

	@Container
	public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("test_db")
			.withUsername("test_user")
			.withPassword("test_pass");

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@DynamicPropertySource
	static void configureDatasource(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgresContainer::getUsername);
		registry.add("spring.datasource.password", postgresContainer::getPassword);
	}

	@AfterAll
	static void tearDown() {
		postgresContainer.stop();
	}

	@Test
	public void testConnection() {
		Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
		assertThat(result).isEqualTo(1);
	}

	@Test
	public void testInsertAndFetchData() {
		jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS example (id SERIAL PRIMARY KEY, name VARCHAR(255))");
		jdbcTemplate.update("INSERT INTO example (id, name) VALUES (?, ?)", 1,"Sample Name");

		String name = jdbcTemplate.queryForObject("SELECT name FROM example WHERE id = 1", String.class);
		assertThat(name).isEqualTo("Sample Name");
	}
}
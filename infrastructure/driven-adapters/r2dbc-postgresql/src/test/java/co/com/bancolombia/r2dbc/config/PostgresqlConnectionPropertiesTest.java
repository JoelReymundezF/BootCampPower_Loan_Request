package co.com.bancolombia.r2dbc.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostgresqlConnectionPropertiesTest {

    @Test
    void testRecordValues() {
        PostgresqlConnectionProperties properties =
                new PostgresqlConnectionProperties("localhost", 5432, "testdb", "public", "user", "pass");

        assertNotNull(properties);
        assertEquals("localhost", properties.host());
        assertEquals(5432, properties.port());
        assertEquals("testdb", properties.database());
        assertEquals("public", properties.schema());
        assertEquals("user", properties.username());
        assertEquals("pass", properties.password());
    }
}
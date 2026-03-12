package br.com.api.mice.replicacao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "app.scheduling.enabled=false",
    "node.id=test-node",
    "node.host=localhost",
    "node.porta=18080",
    "django.api.base-url=http://localhost:8000/api"
})
class ApiMiceReplicacaoBackendApplicationTests {

    @Test
    void contextLoads() {
    }
}

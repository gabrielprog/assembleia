package br.com.assembleia.assembleia;

import br.com.assembleia.assembleia.configs.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestConfig.class)
class ApplicationContextSmokeTest {

    @Test
    void contextLoads() {
        // Se o contexto subir sem erros, o teste passa
        // Este é um teste básico para verificar se a aplicação está configurada corretamente
    }
}

package com.rest.api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Pedidos - Procesador Batch")
                .description("""
                    ## Microservicio para carga masiva de pedidos
                    
                    ### Características:
                    - Carga de pedidos desde archivo CSV
                    - Validación de datos con reglas de negocio
                    - Procesamiento batch optimizado
                    - Idempotencia mediante Idempotency-Key
                    - Logs estructurados con correlationId
                    
                    ### Endpoints disponibles:
                    - `POST /pedidos/cargar` - Carga archivo CSV de pedidos
                    
                    ### Validaciones implementadas:
                    - CLIENTE_NO_ENCONTRADO
                    - ZONA_INVALIDA
                    - FECHA_INVALIDA (zona horaria America/Lima)
                    - ESTADO_INVALIDO
                    - DUPLICADO
                    - CADENA_FRIO_NO_SOPORTADA
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Soporte Técnico")
                    .email("soporte@rest.com")
                    .url("https://github.com/rest/api"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("""
                        Autenticación mediante JWT.
                        Para pruebas locales, el security está deshabilitado.
                        """)));
    }

    @Bean
    public io.swagger.v3.oas.models.Paths customPaths() {
        return new io.swagger.v3.oas.models.Paths();
    }
}
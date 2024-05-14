package br.com.sysmap.bootcamp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocOpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User API")
                        .description("User API for Sysmap Bootcamp")
                        .version("v1")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                        .contact(new Contact().name("Joao Pedro Monteiro").email("jp@teste.com")))
                .components(new Components())
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> {
            ApiResponses apiResponses = operation.getResponses();
            if (apiResponses == null) {
                apiResponses = new ApiResponses();
            }
            apiResponses.addApiResponse("500", new ApiResponse().description("Internal server error"));
            operation.responses(apiResponses);
            return operation;
        };
    }
}

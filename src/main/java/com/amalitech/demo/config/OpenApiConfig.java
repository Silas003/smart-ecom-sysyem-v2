package com.amalitech.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Smart E-Commerce System API")
                        .version("v1.0.0")
                        .description("REST APIs for the Smart E-Commerce System (Users, Products, Categories, Reviews) and a GraphQL endpoint")
                        .contact(new Contact().name("Amalitech Team").email("support@amalitech.com"))
                );

        // Add a simple /graphql POST operation with schema example
        Schema<?> graphqlRequestSchema = new Schema<>().type("object").addProperties("query", new Schema<>().type("string"))
                .addProperties("variables", new Schema<>().type("object"));

        io.swagger.v3.oas.models.Operation graphqlOperation = new io.swagger.v3.oas.models.Operation()
                .summary("GraphQL endpoint")
                .description("Single GraphQL POST endpoint to execute queries and mutations")
                .requestBody(new RequestBody().content(new Content().addMediaType("application/json",
                        new MediaType().schema(graphqlRequestSchema).example(Map.of("query","{ products { id name price } }")))))
                .responses(new ApiResponses().addApiResponse("200",
                        new ApiResponse().description("GraphQL response (200)").content(new Content().addMediaType("application/json",
                                new MediaType().schema(new Schema<>().type("object"))))));

        PathItem graphqlPath = new PathItem().post(graphqlOperation);
        Paths paths = new Paths();
        paths.addPathItem("/graphql", graphqlPath);
        openAPI.setPaths(paths);

        return openAPI;
    }
}

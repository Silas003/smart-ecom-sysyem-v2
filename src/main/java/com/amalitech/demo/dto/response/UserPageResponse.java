package com.amalitech.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Paged user response for GraphQL")
public class UserPageResponse {
    @Schema(description = "User items")
    private List<UserResponse> items;

    @Schema(description = "Total number of users")
    private long totalCount;
}

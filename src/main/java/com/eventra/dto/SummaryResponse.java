package com.eventra.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponse {
    private Integer totalOrderDetails;
    private Integer totalUpcomingEvents;
    private Integer totalRevenue;
    private Integer totalUsersRegistered;
}

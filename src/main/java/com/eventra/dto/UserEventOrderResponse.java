package com.eventra.dto;

import com.eventra.dto.OrderDetailResponse;
import lombok.Data;

import java.util.List;

@Data
public class UserEventOrderResponse {
    private EventResponse event;
    private OrderResponse order;
    private List<OrderDetailResponse> orderDetails;
}

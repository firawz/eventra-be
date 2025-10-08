package com.eventra.config;

import com.eventra.event.AuditCreatedEvent;
import com.eventra.model.*;
import com.eventra.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Component
public class AuditEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AuditEventListener.class);

    @Autowired
    private UserAuditRepository userAuditRepository;
    @Autowired
    private EventAuditRepository eventAuditRepository;
    @Autowired
    private OrderAuditRepository orderAuditRepository;
    @Autowired
    private OrderDetailAuditRepository orderDetailAuditRepository;
    @Autowired
    private RoleAuditRepository roleAuditRepository;
    @Autowired
    private TicketAuditRepository ticketAuditRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditCreatedEvent(AuditCreatedEvent event) {
        logger.info("Received AuditCreatedEvent for entity: {} with action: {}", event.getEntity().getClass().getSimpleName(), event.getAction());
        createAudit(event.getEntity(), event.getAction());
    }

    private void createAudit(Object entity, String action) {
        String entityName = entity.getClass().getSimpleName();
        String createdBy = "SYSTEM"; // Placeholder, ideally from security context

        switch (entityName) {
            case "User":
                User user = (User) entity;
                UserAudit userAudit = new UserAudit();
                userAudit.setCreatedAt(LocalDateTime.now());
                userAudit.setCreatedBy(createdBy);
                userAudit.setAction(action);
                userAudit.setUserId(user.getId());
                userAudit.setFullName(user.getFullName());
                userAudit.setEmail(user.getEmail());
                userAudit.setPhone(user.getPhone());
                userAudit.setPassword(user.getPassword());
                userAudit.setRole(user.getRole());
                userAudit.setGender(user.getGender());
                userAudit.setNik(user.getNik());
                userAudit.setIsRegistered(user.getIsRegistered());
                userAuditRepository.save(userAudit);
                break;
            case "Event":
                Event event = (Event) entity;
                EventAudit eventAudit = new EventAudit();
                eventAudit.setCreatedAt(LocalDateTime.now());
                eventAudit.setCreatedBy(createdBy);
                eventAudit.setAction(action);
                eventAudit.setEventId(event.getId());
                eventAudit.setTitle(event.getTitle());
                eventAudit.setDescription(event.getDescription());
                eventAudit.setLocation(event.getLocation());
                eventAudit.setStartDate(event.getStartDate().toLocalDateTime());
                eventAudit.setEndDate(event.getEndDate().toLocalDateTime());
                eventAudit.setUserId(event.getUser() != null ? event.getUser().getId() : null);
                eventAuditRepository.save(eventAudit);
                break;
            case "Order":
                Order order = (Order) entity;
                OrderAudit orderAudit = new OrderAudit();
                orderAudit.setCreatedAt(LocalDateTime.now());
                orderAudit.setCreatedBy(createdBy);
                orderAudit.setAction(action);
                orderAudit.setOrderId(order.getId());
                orderAudit.setUserId(order.getUser().getId());
                orderAudit.setEventId(order.getEvent().getId());
                orderAudit.setStatus(order.getStatus());
                orderAudit.setTotalPrice(order.getTotalPrice());
                orderAuditRepository.save(orderAudit);
                break;
            case "OrderDetail":
                OrderDetail orderDetail = (OrderDetail) entity;
                OrderDetailAudit orderDetailAudit = new OrderDetailAudit();
                orderDetailAudit.setCreatedAt(LocalDateTime.now());
                orderDetailAudit.setCreatedBy(createdBy);
                orderDetailAudit.setAction(action);
                orderDetailAudit.setOrderDetailId(orderDetail.getId());
                orderDetailAudit.setOrderId(orderDetail.getOrder().getId());
                orderDetailAudit.setNik(orderDetail.getNik());
                orderDetailAudit.setFullName(orderDetail.getFullName());
                orderDetailAudit.setBirthDate(orderDetail.getBirthDate());
                orderDetailAudit.setTicketCode(orderDetail.getTicketCode());
                orderDetailAuditRepository.save(orderDetailAudit);
                break;
            case "Role":
                Role role = (Role) entity; // Assuming Role is an enum or simple class with a name
                RoleAudit roleAudit = new RoleAudit();
                roleAudit.setCreatedAt(LocalDateTime.now());
                roleAudit.setCreatedBy(createdBy);
                roleAudit.setAction(action);
                // Assuming Role entity has an ID and a name field
                // For an enum, you might just store the name
                // roleAudit.setRoleId(role.getId()); // If Role had an ID
                roleAudit.setRoleName(role.name()); // Assuming Role is an enum
                roleAuditRepository.save(roleAudit);
                break;
            case "Ticket":
                Ticket ticket = (Ticket) entity;
                TicketAudit ticketAudit = new TicketAudit();
                ticketAudit.setCreatedAt(LocalDateTime.now());
                ticketAudit.setCreatedBy(createdBy);
                ticketAudit.setAction(action);
                ticketAudit.setTicketId(ticket.getId());
                ticketAudit.setEventId(ticket.getEvent().getId());
                ticketAudit.setTicketCategory(ticket.getTicketCategory());
                ticketAudit.setPrice(ticket.getPrice());
                ticketAudit.setQuota(ticket.getQuota());
                ticketAuditRepository.save(ticketAudit);
                break;
            default:
                logger.warn("No audit configured for entity: {}", entityName);
                break;
        }
    }
}

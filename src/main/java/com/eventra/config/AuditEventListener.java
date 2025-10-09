package com.eventra.config;

import com.eventra.event.AuditCreatedEvent;
import com.eventra.model.*;
import com.eventra.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eventra.event.AuditDeletedEvent;
import com.eventra.event.AuditUpdatedEvent;
import java.time.LocalDateTime;
import java.util.UUID;

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
    @Autowired
    private JwtUtil jwtUtil; // Inject JwtUtil

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditCreatedEvent(AuditCreatedEvent event) {
        logger.info("Received AuditCreatedEvent for entity: {} with action: {}", event.getEntity().getClass().getSimpleName(), event.getAction());
        createAudit(event.getEntity(), event.getAction(), AuditType.CREATE);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditUpdatedEvent(AuditUpdatedEvent event) {
        logger.info("Received AuditUpdatedEvent for entity: {} with action: {}", event.getEntity().getClass().getSimpleName(), event.getAction());
        createAudit(event.getEntity(), event.getAction(), AuditType.UPDATE);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditDeletedEvent(AuditDeletedEvent event) {
        logger.info("Received AuditDeletedEvent for entity: {} with action: {}", event.getEntity().getClass().getSimpleName(), event.getAction());
        createAudit(event.getEntity(), event.getAction(), AuditType.DELETE);
    }

    private void createAudit(Object entity, String action, AuditType auditType) {
        String entityName = entity.getClass().getSimpleName();
        String auditBy = "SYSTEM"; // Default to SYSTEM

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) principal;
                auditBy = customUserDetails.getUserId().toString();
            } else {
                // Fallback if for some reason CustomUserDetails is not in the principal
                // This might happen for anonymous users or other authentication mechanisms
                logger.warn("Principal is not CustomUserDetails. Audit will use default or username if available.");
                if (principal instanceof String) {
                    auditBy = (String) principal;
                } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    auditBy = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                }
            }
        }

        switch (entityName) {
            case "User":
                User user = (User) entity;
                UserAudit userAudit = new UserAudit();
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

                if (auditType == AuditType.CREATE) {
                    userAudit.setCreatedAt(LocalDateTime.now());
                    userAudit.setCreatedBy(auditBy);
                } else if (auditType == AuditType.UPDATE) {
                    userAudit.setUpdatedAt(LocalDateTime.now());
                    userAudit.setUpdatedBy(auditBy);
                } else if (auditType == AuditType.DELETE) {
                    userAudit.setDeletedAt(LocalDateTime.now());
                    userAudit.setDeletedBy(auditBy);
                }
                userAuditRepository.save(userAudit);
                break;
            case "Event":
                Event event = (Event) entity;
                EventAudit eventAudit = new EventAudit();
                eventAudit.setAction(action);
                eventAudit.setEventId(event.getId());
                eventAudit.setTitle(event.getTitle());
                eventAudit.setDescription(event.getDescription());
                eventAudit.setLocation(event.getLocation());
                eventAudit.setStartDate(event.getStartDate().toLocalDateTime());
                eventAudit.setEndDate(event.getEndDate().toLocalDateTime());
                eventAudit.setUserId(event.getUser() != null ? event.getUser().getId() : null);

                if (auditType == AuditType.CREATE) {
                    eventAudit.setCreatedAt(LocalDateTime.now());
                    eventAudit.setCreatedBy(auditBy);
                } else if (auditType == AuditType.UPDATE) {
                    eventAudit.setUpdatedAt(LocalDateTime.now());
                    eventAudit.setUpdatedBy(auditBy);
                } else if (auditType == AuditType.DELETE) {
                    eventAudit.setDeletedAt(LocalDateTime.now());
                    eventAudit.setDeletedBy(auditBy);
                }
                eventAuditRepository.save(eventAudit);
                break;
            case "Order":
                Order order = (Order) entity;
                OrderAudit orderAudit = new OrderAudit();
                orderAudit.setAction(action);
                orderAudit.setOrderId(order.getId());
                orderAudit.setUserId(order.getUser().getId());
                orderAudit.setEventId(order.getEvent().getId());
                orderAudit.setStatus(order.getStatus());
                orderAudit.setTotalPrice(order.getTotalPrice());

                if (auditType == AuditType.CREATE) {
                    orderAudit.setCreatedAt(LocalDateTime.now());
                    orderAudit.setCreatedBy(auditBy);
                } else if (auditType == AuditType.UPDATE) {
                    orderAudit.setUpdatedAt(LocalDateTime.now());
                    orderAudit.setUpdatedBy(auditBy);
                } else if (auditType == AuditType.DELETE) {
                    orderAudit.setDeletedAt(LocalDateTime.now());
                    orderAudit.setDeletedBy(auditBy);
                }
                orderAuditRepository.save(orderAudit);
                break;
            case "OrderDetail":
                OrderDetail orderDetail = (OrderDetail) entity;
                OrderDetailAudit orderDetailAudit = new OrderDetailAudit();
                orderDetailAudit.setAction(action);
                orderDetailAudit.setOrderDetailId(orderDetail.getId());
                orderDetailAudit.setOrderId(orderDetail.getOrder().getId());
                orderDetailAudit.setNik(orderDetail.getNik());
                orderDetailAudit.setFullName(orderDetail.getFullName());
                orderDetailAudit.setBirthDate(orderDetail.getBirthDate());
                orderDetailAudit.setTicketCode(orderDetail.getTicketCode());

                if (auditType == AuditType.CREATE) {
                    orderDetailAudit.setCreatedAt(LocalDateTime.now());
                    orderDetailAudit.setCreatedBy(auditBy);
                } else if (auditType == AuditType.UPDATE) {
                    orderDetailAudit.setUpdatedAt(LocalDateTime.now());
                    orderDetailAudit.setUpdatedBy(auditBy);
                } else if (auditType == AuditType.DELETE) {
                    orderDetailAudit.setDeletedAt(LocalDateTime.now());
                    orderDetailAudit.setDeletedBy(auditBy);
                }
                orderDetailAuditRepository.save(orderDetailAudit);
                break;
            case "Role":
                Role role = (Role) entity; // Assuming Role is an enum or simple class with a name
                RoleAudit roleAudit = new RoleAudit();
                roleAudit.setAction(action);
                // Assuming Role entity has an ID and a name field
                // For an enum, you might just store the name
                // roleAudit.setRoleId(role.getId()); // If Role had an ID
                roleAudit.setRoleName(role.name()); // Assuming Role is an enum

                if (auditType == AuditType.CREATE) {
                    roleAudit.setCreatedAt(LocalDateTime.now());
                    roleAudit.setCreatedBy(auditBy);
                } else if (auditType == AuditType.UPDATE) {
                    roleAudit.setUpdatedAt(LocalDateTime.now());
                    roleAudit.setUpdatedBy(auditBy);
                } else if (auditType == AuditType.DELETE) {
                    roleAudit.setDeletedAt(LocalDateTime.now());
                    roleAudit.setDeletedBy(auditBy);
                }
                roleAuditRepository.save(roleAudit);
                break;
            case "Ticket":
                Ticket ticket = (Ticket) entity;
                TicketAudit ticketAudit = new TicketAudit();
                ticketAudit.setAction(action);
                ticketAudit.setTicketId(ticket.getId());
                ticketAudit.setEventId(ticket.getEvent().getId());
                ticketAudit.setTicketCategory(ticket.getTicketCategory());
                ticketAudit.setPrice(ticket.getPrice());
                ticketAudit.setQuota(ticket.getQuota());

                if (auditType == AuditType.CREATE) {
                    ticketAudit.setCreatedAt(LocalDateTime.now());
                    ticketAudit.setCreatedBy(auditBy);
                } else if (auditType == AuditType.UPDATE) {
                    ticketAudit.setUpdatedAt(LocalDateTime.now());
                    ticketAudit.setUpdatedBy(auditBy);
                } else if (auditType == AuditType.DELETE) {
                    ticketAudit.setDeletedAt(LocalDateTime.now());
                    ticketAudit.setDeletedBy(auditBy);
                }
                ticketAuditRepository.save(ticketAudit);
                break;
            default:
                logger.warn("No audit configured for entity: {}", entityName);
                break;
        }
    }

    private enum AuditType {
        CREATE, UPDATE, DELETE
    }
}

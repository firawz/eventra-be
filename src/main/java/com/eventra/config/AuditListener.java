package com.eventra.config;

import com.eventra.model.*;
import com.eventra.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PostRemove;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AuditListener {

    private static ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        AuditListener.applicationContext = applicationContext;
    }

    @PostPersist
    public void postPersist(Object entity) {
        createAudit(entity, "CREATED");
    }

    @PostUpdate
    public void postUpdate(Object entity) {
        createAudit(entity, "UPDATED");
    }

    @PostRemove
    public void postRemove(Object entity) {
        createAudit(entity, "DELETED");
    }

    private void createAudit(Object entity, String action) {
        String entityName = entity.getClass().getSimpleName();
        String description = String.format("%s %s: %s", entityName, action, entity.toString()); // Customize toString for better description

        // In a real application, you would get the current user from SecurityContext
        String createdBy = "SYSTEM"; // Placeholder

        switch (entityName) {
            case "User":
                UserAudit userAudit = new UserAudit();
                userAudit.setCreatedAt(LocalDateTime.now());
                userAudit.setCreatedBy(createdBy);
                userAudit.setDescription(description);
                applicationContext.getBean(UserAuditRepository.class).save(userAudit);
                break;
            case "Event":
                EventAudit eventAudit = new EventAudit();
                eventAudit.setCreatedAt(LocalDateTime.now());
                eventAudit.setCreatedBy(createdBy);
                eventAudit.setDescription(description);
                applicationContext.getBean(EventAuditRepository.class).save(eventAudit);
                break;
            case "Order":
                OrderAudit orderAudit = new OrderAudit();
                orderAudit.setCreatedAt(LocalDateTime.now());
                orderAudit.setCreatedBy(createdBy);
                orderAudit.setDescription(description);
                applicationContext.getBean(OrderAuditRepository.class).save(orderAudit);
                break;
            case "OrderDetail":
                OrderDetailAudit orderDetailAudit = new OrderDetailAudit();
                orderDetailAudit.setCreatedAt(LocalDateTime.now());
                orderDetailAudit.setCreatedBy(createdBy);
                orderDetailAudit.setDescription(description);
                applicationContext.getBean(OrderDetailAuditRepository.class).save(orderDetailAudit);
                break;
            case "Role":
                RoleAudit roleAudit = new RoleAudit();
                roleAudit.setCreatedAt(LocalDateTime.now());
                roleAudit.setCreatedBy(createdBy);
                roleAudit.setDescription(description);
                applicationContext.getBean(RoleAuditRepository.class).save(roleAudit);
                break;
            case "Ticket":
                TicketAudit ticketAudit = new TicketAudit();
                ticketAudit.setCreatedAt(LocalDateTime.now());
                ticketAudit.setCreatedBy(createdBy);
                ticketAudit.setDescription(description);
                applicationContext.getBean(TicketAuditRepository.class).save(ticketAudit);
                break;
            default:
                // Handle other entities or log a warning
                System.out.println("No audit configured for entity: " + entityName);
                break;
        }
    }
}

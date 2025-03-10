package com.jobhunt.model.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.Instant;

public class AuditTrailListener {

    private static Log log = LogFactory.getLog(AuditTrailListener.class);

    @PrePersist
    private void beforeAnyPersist(Object entity) {
        if(entity instanceof User user) {
            user.setCreatedAt(Instant.now());
            log.info("User created at: " + Instant.now());
        }
    }

    @PreUpdate
    private void beforeAnyUpdate(Object entity) {
        if (entity instanceof User user) {
            user.setUpdatedAt(Instant.now());
            log.info("User updated at: " + Instant.now());
        }
    }
}

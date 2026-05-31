package com.sprint.training.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "access_logs")
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private AccessZone accessZone;


    public AccessLog() {
    }

    public AccessLog(Long id, String direction, LocalDateTime timeStamp, Client client, AccessZone accessZone) {
        this.id = id;
        this.direction = direction;
        this.timeStamp = timeStamp;
        this.client = client;
        this.accessZone = accessZone;
    }

    public AccessLog(String direction, Client client, AccessZone accessZone) {
        this.direction = direction;
        this.client = client;
        this.accessZone = accessZone;
        this.timeStamp = LocalDateTime.now();
    }

    public AccessZone getAccessZone() {
        return accessZone;
    }

    public void setAccessZone(AccessZone accessZone) {
        this.accessZone = accessZone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}

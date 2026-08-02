package com.sprint.training.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="access_cards")
public class AccessCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String rfidToken;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    public AccessCard(){}

    public AccessCard(String rfidToken, Client client){
        this.rfidToken = rfidToken;
        this.isActive = client.isActive();
        this.client = client;
        this.issuedAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRfidToken() {
        return rfidToken;
    }

    public void setRfidToken(String rfidToken) {
        this.rfidToken = rfidToken;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessCard)) return false;
        AccessCard card = (AccessCard) o;
        return id != null && id.equals(card.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

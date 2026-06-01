package com.sprint.training.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean isActive;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "client_zone",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "zone_id")
    )
    private Set<AccessZone> accessZones = new HashSet<>();

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL)
    private AccessCard accessCard;

    public Client() {
    }

    public Client(Long id, String name, String email, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isActive = isActive;
    }

    public AccessCard getAccessCard() {
        return accessCard;
    }

    public void setAccessCard(AccessCard accessCard) {
        this.accessCard = accessCard;
    }

    public void addAccessZone(AccessZone zone) {
        this.accessZones.add(zone);
        zone.getClients().add(this);
    }

    public void removeAccessZone(AccessZone zone) {
        this.accessZones.remove(zone);
        zone.getClients().remove(this);
    }

    public Set<AccessZone> getAccessZones() {
        return accessZones;
    }

    public void setAccessZones(Set<AccessZone> accessZones) {
        this.accessZones = accessZones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;

        return getId() != null && getId().equals(client.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}

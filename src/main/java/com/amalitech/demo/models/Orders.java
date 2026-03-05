package com.amalitech.demo.models;


import com.amalitech.demo.dto.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@NamedEntityGraph(
        name = "orders-with-items-and-user",
        attributeNodes = {
                @NamedAttributeNode("user"),
                @NamedAttributeNode(value = "items", subgraph = "items-with-product")
        },
        subgraphs = {
                @NamedSubgraph(name = "items-with-product", attributeNodes = {
                        @NamedAttributeNode("product")
                })
        }
)
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable =  false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
    private User user;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Orders(User u, Double aDouble, OrderStatus orderStatus) {
        this.user = u;
        this.totalAmount = aDouble;
        this.status = orderStatus;
    }

    @PrePersist
    protected void onCreate() {
        this.status = OrderStatus.pending;
    }

    public Orders() {}
}

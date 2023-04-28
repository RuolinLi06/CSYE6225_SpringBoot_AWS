package com.neu.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * @author Ruolin Li
 */
@Data
@Entity
@Table(name = "product",uniqueConstraints={@UniqueConstraint(columnNames={"sku"})})
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private String sku;
    @NotNull
    private String manufacturer;
    @NotNull
    @Range(max = 100,min = 0)
    private long quantity;
    @Column(name = "date_added",nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date dateAdded;

    @Column(name = "date_last_updated",nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date dateLastUpdated;

    @ReadOnlyProperty
    @Column(name = "owner_user_id")
    private long OwnerUserId;
}

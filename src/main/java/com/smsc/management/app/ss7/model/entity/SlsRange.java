package com.smsc.management.app.ss7.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sls_range")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SlsRange {

    @Id
    private String id;

    private String name;
}

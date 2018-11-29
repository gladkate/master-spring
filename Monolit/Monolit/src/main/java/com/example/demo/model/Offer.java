package com.example.demo.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "offer")
public class Offer implements Serializable{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private long customer_id;
    private String title;
    private String department;
    private String message;
}

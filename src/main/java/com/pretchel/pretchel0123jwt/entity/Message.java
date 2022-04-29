package com.pretchel.pretchel0123jwt.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

public class Message extends BaseTime {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gift_id")
    private Gift gift;
}

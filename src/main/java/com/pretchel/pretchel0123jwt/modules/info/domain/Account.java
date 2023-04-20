package com.pretchel.pretchel0123jwt.modules.info.domain;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private Users users;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String accountNum;

    @Column(nullable = false)
    private String bank;

    @Column(nullable = false)
    private String bankCode;

    @Column(nullable = false)
    private String birthday;

    @Column
    private Boolean isDefault;


}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avp.mems.backstage.entity.basic;

import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by Amber Wang on 2017-05-27
 */
@Data
@Entity
@Table(name = "city")
public class City implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 32)
    @Column(name = "code")
    private String code;
    @Size(max = 50)
    @Column(name = "name_cn")
    private String nameCn;
    @Size(max = 50)
    @Column(name = "name_en")
    private String nameEn;
    @Column(name = "province")
    private Long province;
}
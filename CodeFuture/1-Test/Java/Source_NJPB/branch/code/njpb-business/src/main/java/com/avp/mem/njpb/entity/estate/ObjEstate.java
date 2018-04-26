/**
 * Copyright (c) 2017 Avant-Port All Rights Reserved.
 */
package com.avp.mem.njpb.entity.estate;

import com.avp.mem.njpb.api.entity.EntityBase;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Where;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by six on 2017/7/24.
 */
@Entity
@DynamicUpdate(true)    //不修改全部列
@DynamicInsert(true)    //不插入全部列
@Where(clause = "remove_time is null")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "obj_estate", schema = "business")
public class ObjEstate extends EntityBase {

    private Integer estateNo;
    private String estateName;
    private Integer stationId;
    private Integer category;
    private Integer estateStatusId;
    private Integer estateTypeId;
    private String estateSn;
    private Integer supplierId;
    private String estateBatch;
    private Integer parentId;
    @Generated(GenerationTime.INSERT)
    private Integer logicalId;
    private Integer projectId;
    private String estatePath;
    private Integer bikeFrameNo;
    private String bicycleStakeBarCode;

    @Basic
    @Column(name = "estate_no")
    public Integer getEstateNo() {
        return estateNo;
    }

    public void setEstateNo(Integer estateNo) {
        this.estateNo = estateNo;
    }

    @Basic
    @Column(name = "estate_name")
    public String getEstateName() {
        return estateName;
    }

    public void setEstateName(String estateName) {
        this.estateName = estateName;
    }

    @Basic
    @Column(name = "station_id")
    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    @Basic
    @Column(name = "category")
    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }


    @Basic
    @Column(name = "estate_status_id")
    public Integer getEstateStatusId() {
        return estateStatusId;
    }

    public void setEstateStatusId(Integer estateStatusId) {
        this.estateStatusId = estateStatusId;
    }

    @Basic
    @Column(name = "estate_type_id")
    public Integer getEstateTypeId() {
        return estateTypeId;
    }

    public void setEstateTypeId(Integer estateTypeId) {
        this.estateTypeId = estateTypeId;
    }

    @Basic
    @Column(name = "estate_sn")
    public String getEstateSn() {
        return estateSn;
    }

    public void setEstateSn(String estateSn) {
        this.estateSn = estateSn;
    }

    @Basic
    @Column(name = "supplier_id")
    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @Basic
    @Column(name = "estate_batch")
    public String getEstateBatch() {
        return estateBatch;
    }

    public void setEstateBatch(String estateBatch) {
        this.estateBatch = estateBatch;
    }

    @Basic
    @Column(name = "parent_id")
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Basic
    @Column(name = "logical_id")
    public Integer getLogicalId() {
        return logicalId;
    }

    public void setLogicalId(Integer logicalId) {
        this.logicalId = logicalId;
    }

    @Basic
    @Column(name = "project_id")
    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @Basic
    @Column(name = "estate_path")
    public String getEstatePath() {
        return estatePath;
    }

    public void setEstatePath(String estatePath) {
        this.estatePath = estatePath;
    }

    @Basic
    @Column(name = "bike_frame_no")
    public Integer getBikeFrameNo() {
        return bikeFrameNo;
    }

    public void setBikeFrameNo(Integer bikeFrameNo) {
        this.bikeFrameNo = bikeFrameNo;
    }

    @Basic
    @Column(name = "bicycle_stake_bar_code")
    public String getBicycleStakeBarCode() {
        return bicycleStakeBarCode;
    }

    public void setBicycleStakeBarCode(String bicycleStakeBarCode) {
        this.bicycleStakeBarCode = bicycleStakeBarCode;
    }
}

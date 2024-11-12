package com.telefonica.nomodulos.beans;

import java.math.BigDecimal;
import java.util.Date;

public class DisneyCustomerDTO {

    private BigDecimal dicuIdDisneyCustomer;
    private String dicuCoDisneySharedCustomer;
    private Long spcfIdSpConnectionFile;
    private Date spcfDaCreationDate;
    private int dicuInIsEntitled;
    private int dicuInIsActivated;
    private Date dicuTiProcess;
    private Date dicuTiLastUpdated;
    private BigDecimal userIdCreatorParty;
    private Date audiTiCreation;
    private BigDecimal userIdUpdaterParty;
    private Date audiTiUpdate;

    public BigDecimal getDicuIdDisneyCustomer() {
        return dicuIdDisneyCustomer;
    }
    public void setDicuIdDisneyCustomer(BigDecimal dicuIdDisneyCustomer) {
        this.dicuIdDisneyCustomer = dicuIdDisneyCustomer;
    }
    public String getDicuCoDisneySharedCustomer() {
        return dicuCoDisneySharedCustomer;
    }
    public void setDicuCoDisneySharedCustomer(String dicuCoDisneySharedCustomer) {
        this.dicuCoDisneySharedCustomer = dicuCoDisneySharedCustomer;
    }
    public Long getSpcfIdSpConnectionFile() {
        return spcfIdSpConnectionFile;
    }
    public void setSpcfIdSpConnectionFile(Long spcfIdSpConnectionFile) {
        this.spcfIdSpConnectionFile = spcfIdSpConnectionFile;
    }

    public Date getSpcfDaCreationDate() {
        return spcfDaCreationDate;
    }

    public void setSpcfDaCreationDate(Date spcfDaCreationDate) {
        this.spcfDaCreationDate = spcfDaCreationDate;
    }

    public int getDicuInIsEntitled() {
        return dicuInIsEntitled;
    }

    public void setDicuInIsEntitled(int dicuInIsEntitled) {
        this.dicuInIsEntitled = dicuInIsEntitled;
    }

    public int getDicuInIsActivated() {
        return dicuInIsActivated;
    }

    public void setDicuInIsActivated(int dicuInIsActivated) {
        this.dicuInIsActivated = dicuInIsActivated;
    }

    public Date getDicuTiProcess() {
        return dicuTiProcess;
    }

    public void setDicuTiProcess(Date dicuTiProcess) {
        this.dicuTiProcess = dicuTiProcess;
    }

    public Date getDicuTiLastUpdated() {
        return dicuTiLastUpdated;
    }

    public void setDicuTiLastUpdated(Date dicuTiLastUpdated) {
        this.dicuTiLastUpdated = dicuTiLastUpdated;
    }

    public BigDecimal getUserIdCreatorParty() {
        return userIdCreatorParty;
    }

    public void setUserIdCreatorParty(BigDecimal userIdCreatorParty) {
        this.userIdCreatorParty = userIdCreatorParty;
    }

    public Date getAudiTiCreation() {
        return audiTiCreation;
    }

    public void setAudiTiCreation(Date audiTiCreation) {
        this.audiTiCreation = audiTiCreation;
    }

    public BigDecimal getUserIdUpdaterParty() {
        return userIdUpdaterParty;
    }

    public void setUserIdUpdaterParty(BigDecimal userIdUpdaterParty) {
        this.userIdUpdaterParty = userIdUpdaterParty;
    }

    public Date getAudiTiUpdate() {
        return audiTiUpdate;
    }

    public void setAudiTiUpdate(Date audiTiUpdate) {
        this.audiTiUpdate = audiTiUpdate;
    }
}

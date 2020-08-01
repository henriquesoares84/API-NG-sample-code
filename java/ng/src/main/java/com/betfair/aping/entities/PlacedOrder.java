package com.betfair.aping.entities;

import java.util.Date;

public class PlacedOrder {

	private String betId;
	private Double marketId;
	private Long selectionId;
	private double handicap;
	private PriceSize priceSize;
	private Double bspLiability;
	private String side;
	private String status;
	private String persistenceType;
	private String orderType;
	private Date placedDate;
	private Date matchedDate;
	private Double averagePriceMatched;
	private Double sizeMatched;
	private Double avgPriceMatched;
	private Double sizeRemaining;
	private Double sizeLapsed;
	private Double sizeCancelled;
	private Double sizeVoided;
	public String getBetId() {
		return betId;
	}
	public void setBetId(String betId) {
		this.betId = betId;
	}
	public Double getMarketId() {
		return marketId;
	}
	public void setMarketId(Double marketId) {
		this.marketId = marketId;
	}
	public Long getSelectionId() {
		return selectionId;
	}
	public void setSelectionId(Long selectionId) {
		this.selectionId = selectionId;
	}
	public double getHandicap() {
		return handicap;
	}
	public void setHandicap(double handicap) {
		this.handicap = handicap;
	}
	public PriceSize getPriceSize() {
		return priceSize;
	}
	public void setPriceSize(PriceSize priceSize) {
		this.priceSize = priceSize;
	}
	public Double getBspLiability() {
		return bspLiability;
	}
	public void setBspLiability(Double bspLiability) {
		this.bspLiability = bspLiability;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPersistenceType() {
		return persistenceType;
	}
	public void setPersistenceType(String persistenceType) {
		this.persistenceType = persistenceType;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public Date getPlacedDate() {
		return placedDate;
	}
	public void setPlacedDate(Date placedDate) {
		this.placedDate = placedDate;
	}
	public Date getMatchedDate() {
		return matchedDate;
	}
	public void setMatchedDate(Date matchedDate) {
		this.matchedDate = matchedDate;
	}
	public Double getAveragePriceMatched() {
		return averagePriceMatched;
	}
	public void setAveragePriceMatched(Double averagePriceMatched) {
		this.averagePriceMatched = averagePriceMatched;
	}
	public Double getSizeMatched() {
		return sizeMatched;
	}
	public void setSizeMatched(Double sizeMatched) {
		this.sizeMatched = sizeMatched;
	}
	public Double getAvgPriceMatched() {
		return avgPriceMatched;
	}
	public void setAvgPriceMatched(Double avgPriceMatched) {
		this.avgPriceMatched = avgPriceMatched;
	}
	public Double getSizeRemaining() {
		return sizeRemaining;
	}
	public void setSizeRemaining(Double sizeRemaining) {
		this.sizeRemaining = sizeRemaining;
	}
	public Double getSizeLapsed() {
		return sizeLapsed;
	}
	public void setSizeLapsed(Double sizeLapsed) {
		this.sizeLapsed = sizeLapsed;
	}
	public Double getSizeCancelled() {
		return sizeCancelled;
	}
	public void setSizeCancelled(Double sizeCancelled) {
		this.sizeCancelled = sizeCancelled;
	}
	public Double getSizeVoided() {
		return sizeVoided;
	}
	public void setSizeVoided(Double sizeVoided) {
		this.sizeVoided = sizeVoided;
	}
	@Override
	public String toString() {
		return "PlacedOrder [betId=" + betId + ", marketId=" + marketId + ", selectionId=" + selectionId + ", handicap="
				+ handicap + ", priceSize=" + priceSize + ", side=" + side + ", status=" + status + ", persistenceType="
				+ persistenceType + ", orderType=" + orderType + ", placedDate=" + placedDate + ", matchedDate="
				+ matchedDate + ", averagePriceMatched=" + averagePriceMatched + ", sizeMatched=" + sizeMatched
				+ ", avgPriceMatched=" + avgPriceMatched + ", sizeRemaining=" + sizeRemaining + ", sizeLapsed="
				+ sizeLapsed + ", sizeCancelled=" + sizeCancelled + ", sizeVoided=" + sizeVoided + "]";
	}


}

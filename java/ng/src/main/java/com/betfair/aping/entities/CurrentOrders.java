package com.betfair.aping.entities;

import java.util.List;

public class CurrentOrders {
	
	private List<PlacedOrder> currentOrders;

	public List<PlacedOrder> getCurrentOrders() {
		return currentOrders;
	}

	public void setCurrentOrders(List<PlacedOrder> currentOrders) {
		this.currentOrders = currentOrders;
	}

}

package com.telefonica.nomodulos.beans;

import java.util.List;

public class SPConnectionFile_DTO_IN {

	Long id;
	DisneyCustomer_DTO_IN[] disneyCustomers;
	public Long getId() {
		return id;
	}
	public DisneyCustomer_DTO_IN[] getDisneyCustomers() {
		return disneyCustomers;
	}
	public void setDisneyCustomers(DisneyCustomer_DTO_IN[] disneyCustomers) {
		this.disneyCustomers = disneyCustomers;
	}
	public void setId(Long id) {
		this.id = id;
	}
}

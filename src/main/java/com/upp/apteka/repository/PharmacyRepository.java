package com.upp.apteka.repository;

import java.util.List;

import com.upp.apteka.bo.Pharmacy;
import com.upp.apteka.utils.repository.IRepository;

public interface PharmacyRepository extends IRepository<Pharmacy, Long> {

	List<Pharmacy> findPharmacyByName(String name);
	
	//if exists pharamacy with such name
	boolean containsAddress(String address);

}

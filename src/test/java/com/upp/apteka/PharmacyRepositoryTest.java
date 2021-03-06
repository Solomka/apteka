package com.upp.apteka;


import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.upp.apteka.bo.Pharmacy;
import com.upp.apteka.config.AppConfig;
import com.upp.apteka.repository.PharmacyRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class, loader = AnnotationConfigContextLoader.class)
@Transactional
public class PharmacyRepositoryTest {

	private static final Logger LOGGER = Logger.getLogger(PharmacyRepositoryTest.class.getName());
/*
	@Autowired
	@Qualifier("pharmacyRepository")
	private IRepository<Pharmacy, Long, HqlSpecification> pharmacyRepository;
*/
	@Autowired
	private PharmacyRepository pharmacyRepository;
	
	private Pharmacy pharmacy;

	/**
	 * @BeforeClass: onceExecutedBeforeAll tests"
	 */

	@BeforeClass
	public static void prepare() {

		BasicConfigurator.configure(new ConsoleAppender(new SimpleLayout()));
	}

	/**
	 * @Before: executedBeforeEach test"
	 */
	@Before
	public void before() {

		pharmacy = null;
	}

	public static Pharmacy generatePharmacyInstance() {

		Pharmacy pharmacy = new Pharmacy("Znahr", "Levandivka, 12 str.", 30);
		return pharmacy;
	}
/*
	@Test
	public void createPharmacy() {
		pharmacy = generatePharmacyInstance();
		pharmacyRepository.create(pharmacy);
		assertNotNull(pharmacy.getId());
	}
*/
	
	@Test
	public void getPharmacy() {
		pharmacy = pharmacyRepository.read(new Long("8"));
		assertNotNull(pharmacy);
	}	
	
	@Test 
	public void getAllPharmacies(){
		List<Pharmacy> pharmacies = pharmacyRepository.getAll(0, 5);
		
		for(Pharmacy pharmacy: pharmacies)
			System.out.println("Pharmacy:" + pharmacy + "\n" );
		assertNotNull(pharmacies);
	}
	
	@Test
	public void getPharmacyByName(){
		//List<Pharmacy> pharmacies = pharmacyRepository.searchByCriteria(PharmacySpecificationUtils.findPharmacyByName("Green apteka"));
		List<Pharmacy> pharmacies = pharmacyRepository.findPharmacyByName("Green apteka");
		for(Pharmacy pharmacy: pharmacies)
			System.out.println("Pharmacy by name:" + pharmacy + "\n" );
		Assert.assertNotEquals(pharmacies.size(), 0);
	}
/*
	@Test
	public void updatePharmacy() {
		Long eight = new Long("8");

		pharmacy = pharmacyRepository.read(eight);
		pharmacy.setName("Green apteka");
		pharmacyRepository.update(pharmacy);

		pharmacy = pharmacyRepository.read(eight);

		assertEquals(pharmacy.getName(), "Green apteka");
	}
*/
	/*
	@Test
	public void delete() {
		assertEquals(pharmacyRepository.delete(newLong("1")), true);
	}
*/
}

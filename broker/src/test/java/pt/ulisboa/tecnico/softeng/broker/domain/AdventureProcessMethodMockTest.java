package pt.ulisboa.tecnico.softeng.broker.domain;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import pt.ulisboa.tecnico.softeng.activity.domain.exception.ActivityException;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room.Type;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

@RunWith(JMockit.class)
public class AdventureProcessMethodMockTest {
	private static final String ACTIVITY_REFERENCE = "activityReference";
	private static final String HOTEL_REFERENCE = "hotelReference";
	private static final String PAYMENT_CONFIRMATION = "paymentConfirmation";
	private static final String IBAN = "BK01987654321";
	private final LocalDate begin = new LocalDate(2016, 12, 19);
	private final LocalDate end = new LocalDate(2016, 12, 21);
	private Broker broker;

	@Before
	public void setUp() {
		this.broker = new Broker("BR98", "Travel Light");
	}

	@Test
	public void processWithBankException(@Mocked final BankInterface bankInterface) {
		new Expectations() {
			{
				BankInterface.processPayment(IBAN, 300);
				this.result = new BankException();
			}
		};

		Adventure adventure = new Adventure(this.broker, this.begin, this.end, 20, IBAN, 300);
		try {
			adventure.process();
		} catch(BankException b) {
			Assert.assertNull(adventure.getBankPayment());
			Assert.assertNull(adventure.getActivityBooking());
		}

	}
	
	@Test
	public void processWithHotelException(@Mocked final HotelInterface hotelInterface) {
		new Expectations() {
			{
				HotelInterface.reserveHotel(Type.SINGLE, AdventureProcessMethodMockTest.this.begin, AdventureProcessMethodMockTest.this.end);
				this.result = new HotelException();
			}
		};

		Adventure adventure = new Adventure(this.broker, this.begin, this.end, 20, IBAN, 300);
		try {
			adventure.process();
		} catch(HotelException b) {
			Assert.assertNull(adventure.getRoomBooking());
		}

	}
	
	@Test
	public void processWithActivityException(@Mocked final ActivityInterface activityInterface) {
		new Expectations() {
			{
				ActivityInterface.reserveActivity(AdventureProcessMethodMockTest.this.begin,
						AdventureProcessMethodMockTest.this.end, 20);
				this.result = new ActivityException();
			}
		};

		Adventure adventure = new Adventure(this.broker, this.begin, this.end, 20, IBAN, 300);
		try {
			adventure.process();
		} catch(ActivityException b) {
			Assert.assertNull(adventure.getActivityBooking());
		}

	}

	@After
	public void tearDown() {
		Broker.brokers.clear();
	}

}

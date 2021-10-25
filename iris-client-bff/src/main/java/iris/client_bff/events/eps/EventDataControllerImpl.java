package iris.client_bff.events.eps;

import static iris.client_bff.ui.messages.ErrorMessages.*;
import static org.apache.commons.lang3.RegExUtils.replaceAll;
import static org.apache.commons.lang3.StringUtils.*;

import iris.client_bff.config.JsonRpcDataValidator;
import iris.client_bff.core.utils.ValidationHelper;
import iris.client_bff.core.web.dto.Address;
import iris.client_bff.events.EventDataSubmissionService;
import iris.client_bff.events.web.dto.Guest;
import iris.client_bff.events.web.dto.GuestAttendanceInformation;
import iris.client_bff.events.web.dto.GuestList;
import iris.client_bff.events.web.dto.GuestListDataProvider;
import iris.client_bff.ui.messages.ErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventDataControllerImpl implements EventDataController {

	private static final Pattern NUMBER_REGEX = Pattern.compile("^\\d+$");
	private static final Pattern NUMBER_REPLACE_REGEX = Pattern.compile("[\\s\\-_+#*.,:;()/|]");

	private final EventDataSubmissionService dataSubmissionService;
	private final ValidationHelper validHelper;
	private final JsonRpcDataValidator jsonRpcDataValidator;

	@Override
	public String submitGuestList(JsonRpcClientDto client, UUID dataAuthorizationToken, GuestList guestList) {
		log.trace("Start submission {}", dataAuthorizationToken);

		if (dataAuthorizationToken != null) {
			validateGuestList(guestList);

			jsonRpcDataValidator.validateData(guestList);

			fixInvalidPhoneNumbersInGuestList(guestList);

			return dataSubmissionService.findRequestAndSaveGuestList(dataAuthorizationToken, guestList);
		}
		// token invalid
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.INVALID_INPUT + ": data auth token");
	}

	private void validateGuestList(GuestList guestList) {
		GuestListDataProvider dataProvider = guestList.getDataProvider();
		if (dataProvider == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_INPUT + ": data provider");
		}

		guestList.setAdditionalInformation(defuseInput(guestList.getAdditionalInformation(),
				"additionalInformation", true, 500));

		dataProvider.setName(defuseInput(dataProvider.getName(),
				"dataProvider.name", true, 256));

		Address providerAddress = dataProvider.getAddress();
		if (providerAddress != null) {
			providerAddress.setCity(defuseInput(providerAddress.getCity(),
					"dataProvider.address.city", true, 256));
			providerAddress.setStreet(defuseInput(providerAddress.getStreet(),
					"dataProvider.address.street", true, 256));
			providerAddress.setHouseNumber(defuseInput(providerAddress.getHouseNumber(),
					"dataProvider.address.houseNumber", true, 256));
			providerAddress.setZipCode(defuseInput(providerAddress.getZipCode(),
					"dataProvider.address.zipCode", true, 10));
		}

		if (guestList.getGuests() != null) {
			for (Guest guest : guestList.getGuests()) {
				guest.setFirstName(defuseInput(guest.getFirstName(), "guest.firstName", true, 100));
				guest.setLastName(defuseInput(guest.getLastName(), "guest.lastName", true, 100));
				guest.setEmail(defuseInput(guest.getEmail(), "guest.email", true, 100));
				guest.setPhone(defuseInputPhone(guest.getPhone(), "guest.phone", true, 100));
				guest.setMobilePhone(defuseInputPhone(guest.getMobilePhone(), "guest.mobilePhone", true, 100));

				Address guestAddress = guest.getAddress();
				if (guestAddress != null) {
					guestAddress.setStreet(defuseInput(guestAddress.getStreet(), "guest.address.street", true, 256));
					guestAddress
							.setHouseNumber(defuseInput(guestAddress.getHouseNumber(), "guest.address.houseNumber", true, 256));
					guestAddress.setZipCode(defuseInput(guestAddress.getZipCode(), "guest.address.zipCode", true, 10));
					guestAddress.setCity(defuseInput(guestAddress.getCity(), "guest.address.city", true, 256));
				}

				GuestAttendanceInformation attendInfo = guest.getAttendanceInformation();
				attendInfo.setAdditionalInformation(defuseInput(attendInfo.getAdditionalInformation(),
						"guest.attendanceInformation.additionalInformation", true, 500));
				attendInfo.setDescriptionOfParticipation(defuseInput(attendInfo.getDescriptionOfParticipation(),
						"guest.attendanceInformation.descriptionOfParticipation", true, 500));
			}
		}
	}

	// use mobile number as phone number if mobile number is valid and phone number is not
	private void fixInvalidPhoneNumbersInGuestList(GuestList guestList) {

		if (guestList.getGuests() != null) {

			guestList.getGuests().stream()
					.filter(it -> !isPossiblePhoneNumber(it.getPhone()) && isPossiblePhoneNumber(it.getMobilePhone()))
					.forEach(guest -> guest.setPhone(guest.getMobilePhone()));
		}
	}

	private boolean isPossiblePhoneNumber(String phoneNumber) {

		if (isBlank(phoneNumber)) {
			return false;
		}

		var number = replaceAll(phoneNumber, NUMBER_REPLACE_REGEX, "");

		return NUMBER_REGEX.matcher(number).matches();
	}

	private String defuseInput(String input, String field, boolean obfuscateLogging, int maxLength) {
		if (validHelper.isPossibleAttack(input, field, obfuscateLogging)) {
			return INVALID_INPUT_STRING;
		}

		return StringUtils.truncate(input, maxLength);
	}

	private String defuseInputPhone(String input, String field, boolean obfuscateLogging, int maxLength) {
		if (validHelper.isPossibleAttackForPhone(input, field, obfuscateLogging)) {
			return INVALID_INPUT_STRING;
		}

		return StringUtils.truncate(input, maxLength);
	}
}

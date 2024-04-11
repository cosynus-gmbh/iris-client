package iris.client_bff.core.validation;

import com.google.i18n.phonenumbers.NumberParseException;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = GeoLocationCoordinate.GeoLocationCoordinateValidator.class)
@Documented
public @interface GeoLocationCoordinate {

	public static final String COORDINATE_REGEX = "^(-?\\d+\\.\\d+)$";

	String message() default "{iris.validation.constraints.GeoLocationCoordinate.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	public static class GeoLocationCoordinateValidator implements ConstraintValidator<GeoLocationCoordinate, Double> {

		private boolean isValidCoordinate(Double coordinate) {
			return Pattern.compile(COORDINATE_REGEX).matcher(coordinate.toString()).matches();
		}

		@Override
		public boolean isValid(final Double value, final ConstraintValidatorContext context) {

			if (value == null) {
				return true;
			}

			return isValidCoordinate(value);
		}
	}
}

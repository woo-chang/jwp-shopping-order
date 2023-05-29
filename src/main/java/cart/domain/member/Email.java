package cart.domain.member;

import static cart.exception.MemberException.EmailEmpty;

import cart.exception.MemberException.EmailOverLength;
import org.apache.logging.log4j.util.Strings;

class Email {

    private static final int MAXIMUM_LENGTH = 255;

    private final String value;

    public Email(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (Strings.isBlank(value)) {
            throw new EmailEmpty();
        }
        if (value.length() > MAXIMUM_LENGTH) {
            throw new EmailOverLength(value.length(), MAXIMUM_LENGTH);
        }
    }

    public String getValue() {
        return value;
    }
}
package woowacourse.shoppingcart.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import woowacourse.shoppingcart.exception.InvalidInputException;

class CustomerTest {

    @DisplayName("올바르지 않은 포맷의 username 이다.")
    @ParameterizedTest(name = "올바르지 않은 username - {0}")
    @ValueSource(strings = {"abc", "abc@navercom", "abcnaver.com", "@naver.com", "abc.naver.com", "", " "})
    void invalidUserName(String input) {
        assertThatThrownBy(() -> new Customer(1L, input, "Abcd1234", "jojo", false))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("올바르지 않은 포맷의 아이디 입니다.");
    }

    @DisplayName("올바르지 않은 포맷의 password 이다.")
    @ParameterizedTest(name = "올바르지 않은 password - {0}")
    @ValueSource(strings = {"1q@4567", "12345678", "abcdefgh", "!@#$%^&*", "1234567a", "abcd!@#$", "1234%^&*", "12345678abcdefgh!"})
    void invalidUserPassword(String input) {
        assertThatThrownBy(() -> new Customer(1L, "jo@naver.com", input, "jojo", false))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("올바르지 않은 포맷의 패스워드 입니다.");
    }

    @DisplayName("올바르지 않은 포맷의 nickname 이다.")
    @ParameterizedTest(name = "올바르지 않은 nickname - {0}")
    @ValueSource(strings = {"1", "1234567890a", "abc_", "ab c", "가나다 라마"})
    void invalidUserNickname(String input) {
        assertThatThrownBy(() -> new Customer(1L, "jo@naver.com", "abcde123@", input, false))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("올바르지 않은 포맷의 닉네임 입니다.");
    }
}

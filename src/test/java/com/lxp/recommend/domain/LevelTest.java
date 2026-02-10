package com.lxp.recommend.domain;

import com.lxp.recommend.domain.user.entity.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class LevelTest {

    @Test
    @DisplayName("Level enum은 올바른 값을 가진다")
    void level_hasCorrectValues() {
        assertThat(Level.JUNIOR.getValue()).isEqualTo(1);
        assertThat(Level.MIDDLE.getValue()).isEqualTo(2);
        assertThat(Level.SENIOR.getValue()).isEqualTo(3);
        assertThat(Level.EXPERT.getValue()).isEqualTo(4);
    }

    @ParameterizedTest
    @CsvSource({
            "1, JUNIOR",
            "2, MIDDLE",
            "3, SENIOR",
            "4, EXPERT"
    })
    @DisplayName("값으로 Level을 찾을 수 있다")
    void fromValue_returnsCorrectLevel(int value, Level expected) {
        assertThat(Level.fromValue(value)).isEqualTo(expected);
    }

    @Test
    @DisplayName("잘못된 값으로 Level을 찾으면 예외가 발생한다")
    void fromValue_throwsForInvalidValue() {
        assertThatThrownBy(() -> Level.fromValue(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown level value");

        assertThatThrownBy(() -> Level.fromValue(5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({
            "JUNIOR, JUNIOR",
            "junior, JUNIOR",
            "Middle, MIDDLE",
            "SENIOR, SENIOR",
            "expert, EXPERT"
    })
    @DisplayName("문자열로 Level을 찾을 수 있다")
    void fromString_returnsCorrectLevel(String name, Level expected) {
        assertThat(Level.fromString(name)).isEqualTo(expected);
    }

    @Test
    @DisplayName("잘못된 문자열로 Level을 찾으면 예외가 발생한다")
    void fromString_throwsForInvalidName() {
        assertThatThrownBy(() -> Level.fromString("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown level");
    }
}

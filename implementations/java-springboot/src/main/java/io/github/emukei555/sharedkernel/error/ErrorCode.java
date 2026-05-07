package io.github.emukei555.sharedkernel.error;

import org.springframework.http.HttpStatus;

public sealed interface ErrorCode permits SystemErrorCode {
    String code();

    String message();  // defaultMessage → message() に改名（関数型らしい）

    HttpStatus status();

    // デフォルト実装（enumでオーバーライド可能）
    default String formattedMessage(Object... args) {
        return String.format(message(), args);
    }
}

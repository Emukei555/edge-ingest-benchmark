package io.github.emukei555.sharedkernel.error;

import org.springframework.http.HttpStatus;

public enum SystemErrorCode implements ErrorCode {

    INVALID_PARAMETER("SYS-400", "入力値が不正です。", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("SYS-404", "指定されたリソースが見つかりませんでした。", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("SYS-401", "認証が必要です。", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("SYS-403", "アクセス権限がありません。", HttpStatus.FORBIDDEN),
    CONFLICT("SYS-409", "リソースが競合しています。", HttpStatus.CONFLICT),
    SYSTEM_ERROR("SYS-500", "システムエラーが発生しました。", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("SYS-503", "現在サービスを利用できません。", HttpStatus.SERVICE_UNAVAILABLE),
    BAD_REQUEST("SYS-400", "リクエストが不正です。", HttpStatus.BAD_REQUEST),
    INVALID_TRANSITION("SYS-409", "不正な状態遷移", HttpStatus.CONFLICT);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    SystemErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return defaultMessage;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}
package io.github.emukei555.domain.model.equipment;

import io.github.emukei555.sharedkernel.error.SystemErrorCode;
import io.github.emukei555.sharedkernel.result.Result;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 産業機器を一意に識別するID。
 *
 * @param value フォーマット: yyyyMM-XXXXX （例: 202604-12345）
 *              - 年月プレフィックス（6桁） + 連番（5桁）
 */
public record EquipmentId(String value) {

    private static final Pattern VALID_PATTERN = Pattern.compile("^\\d{6}-\\d{5}$");

    public EquipmentId {
        Objects.requireNonNull(value, "EquipmentId value cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("EquipmentId value cannot be blank");
        }

        if (!VALID_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid EquipmentId format: " + value);
        }
    }

    public static Result<EquipmentId> of(String raw) {
        if (raw == null || raw.isBlank()) {
            return Result.failure(SystemErrorCode.INVALID_PARAMETER, "機器IDは必須です");
        }

        if (!VALID_PATTERN.matcher(raw).matches()) {
            return Result.failure(SystemErrorCode.INVALID_PARAMETER,
                    "機器IDのフォーマットが不正です (期待値例: EQ-0012): " + raw);
        }

        return Result.success(new EquipmentId(raw));
    }

    /**
     * 新しい機器IDを生成する。
     * * @param yearMonthPrefix 年月を表す6桁の数字（例: "202604"）
     *
     * @return 採番された新しい EquipmentId の Result 型
     */
    public static Result<EquipmentId> generateNew(String yearMonthPrefix) {
        if (yearMonthPrefix == null || yearMonthPrefix.length() != 6) {  // yyyyMM形式を想定
            return Result.failure(SystemErrorCode.INVALID_PARAMETER, "プレフィックスはyyyyMM形式(6文字)である必要があります");
        }

        // 5桁の連番サフィックス（00000〜99999）
        String suffix = String.format("%05d", System.currentTimeMillis() % 100000);
        String newValue = yearMonthPrefix + "-" + suffix;

        return of(newValue);
    }

    public boolean hasYearMonthPrefix() {
        return value.length() > 7 && Character.isDigit(value.charAt(2));
    }

    public String extractYearMonth() {
        if (!hasYearMonthPrefix()) return "";
        return value.substring(3, 9);
    }
}
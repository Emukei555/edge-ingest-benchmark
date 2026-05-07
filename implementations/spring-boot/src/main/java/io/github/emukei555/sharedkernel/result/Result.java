package io.github.emukei555.sharedkernel.result;


import io.github.emukei555.sharedkernel.error.ErrorCode;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 不変・純粋関数指向の結果型
 * ・データは不変なrecordのみ
 * ・処理は純粋関数としてswitch式中心
 * ・副作用は完全に排除）
 * ・失敗は型安全に網羅（sealed + enum拡張可能）
 */
public sealed interface Result<T> permits Result.Ok, Result.Err {

    // ── データ構造 ────────────────────────────────────────────────────────────

    record Ok<T>(T value) implements Result<T> {
        public Ok {
            Objects.requireNonNull(value, "成功時の値はnullであってはならない");
        }
    }

    sealed interface Err<T> extends Result<T> {
        ErrorCode code();
        String message();
        Throwable cause();  // null可
    }

    record SimpleErr<T>(ErrorCode code, String message, Throwable cause) implements Err<T> {
        public SimpleErr {
            Objects.requireNonNull(code);
            Objects.requireNonNull(message);
        }

        public SimpleErr(ErrorCode code) {
            this(code, code.message(), null);
        }

        public SimpleErr(ErrorCode code, String message) {
            this(code, message, null);
        }
    }

    // ── 生成 ──────────────────────────────────────────────────────────────────

    static <T> Result<T> success(T value) {
        return new Ok<>(value);
    }

    static <T> Result<T> failure(ErrorCode code) {
        return new SimpleErr<>(code);
    }

    static <T> Result<T> failure(ErrorCode code, String message) {
        return new SimpleErr<>(code, message);
    }

    static <T> Result<T> failure(ErrorCode code, Throwable cause) {
        return new SimpleErr<>(code, code.message(), cause);
    }

    static <T> Result<T> failure(ErrorCode code, String message, Throwable cause) {
        return new SimpleErr<>(code, message, cause);
    }

    // ── 中間操作（Railway） ──────────────────────────────────────────────────

    default <U> Result<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return switch (this) {
            case Ok<T>(var v)  -> success(mapper.apply(v));
            case Err<T> e      -> (Result<U>) e;
        };
    }

    default <U> Result<U> flatMap(Function<? super T, ? extends Result<U>> mapper) {
        Objects.requireNonNull(mapper);
        return switch (this) {
            case Ok<T>(var v)  -> mapper.apply(v);
            case Err<T> e      -> (Result<U>) e;
        };
    }

    default Result<T> recover(Function<? super Err<T>, ? extends T> recovery) {
        Objects.requireNonNull(recovery);
        return switch (this) {
            case Ok<T> o -> o;
            case Err<T> e -> success(recovery.apply(e));
        };
    }



    // ── 終端操作 ─────────────────────────────────────────────────────────────

    default boolean isOk() {
        return this instanceof Ok;
    }

    default boolean isErr() {
        return this instanceof Err;
    }

    default T orElse(T other) {
        return switch (this) {
            case Ok<T>(var v) -> v;
            case Err<T> ignored     -> other;
        };
    }

    default T orElseGet(Supplier<? extends T> supplier) {
        return switch (this) {
            case Ok<T>(var v) -> v;
            case Err<T> ignored     -> supplier.get();
        };
    }

    default <X extends Throwable> T orElseThrow(
            Function<? super Err<T>, ? extends X> exceptionSupplier) throws X {
        return switch (this) {
            case Ok<T>(var v) -> v;
            case Err<T> e -> throw exceptionSupplier.apply(e);
        };
    }

    default <R> R fold(
            Function<? super T, ? extends R> onOk,
            Function<? super Err<T>, ? extends R> onErr) {
        Objects.requireNonNull(onOk);
        Objects.requireNonNull(onErr);
        return switch (this) {
            case Ok<T>(var v)  -> onOk.apply(v);
            case Err<T> e      -> onErr.apply(e);
        };
    }

    // ── 強制展開（テスト・デバッグ用） ──────────────────────────────────────

    default T unwrap() {
        return switch (this) {
            case Ok<T>(var v) -> v;
            case Err<T> err -> {  // ← 変数名を err に変更（予約語回避）
                String causeMsg = err.cause() != null ? err.cause().getMessage() : "none";
                throw new IllegalStateException(
                        "Result failed: [%s] %s (cause: %s)".formatted(
                                err.code(), err.message(), causeMsg
                        )
                );
            }
        };
    }

    default Err<T> unwrapErr() {
        return switch (this) {
            case Ok<T> o ->
                    throw new IllegalStateException("Ok を unwrapErr しようとしました: " + o.value());
            case Err<T> e -> e;
        };
    }
}
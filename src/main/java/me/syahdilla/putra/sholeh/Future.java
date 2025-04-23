package me.syahdilla.putra.sholeh;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

public class Future<T> {

  private T result;
  private Throwable cause;
  private boolean completed = false;

  private Consumer<T> successHandler;
  private Consumer<Throwable> failureHandler;

  private static final Timer timer = new Timer(true);

  public boolean isComplete() {
    return completed;
  }

  public boolean isSucceeded() {
    return isComplete() && cause == null;
  }

  public boolean isFailed() {
    return isComplete() && cause != null;
  }

  public void complete(T result) {
    if (completed) return;
    completed = true;
    this.result = result;
    success();
  }

  private void success() {
    if (successHandler != null) {
      Consumer<T> handler = successHandler;
      successHandler = null;
      handler.accept(result);
    }
  }

  public void fail(Throwable cause) {
    if (completed) return;
    completed = true;
    this.cause = cause;
    failed();
  }

  private void failed() {
    if (failureHandler != null) {
      Consumer<Throwable> handler = failureHandler;
      failureHandler = null;
      handler.accept(cause);
    }
  }

  public Future<T> onSuccess(Consumer<T> handler) {
    this.successHandler = handler;
    if (isSucceeded()) success();
    return this;
  }

  public Future<T> onFailure(Consumer<Throwable> handler) {
    this.failureHandler = handler;
    if (isFailed()) failed();
    return this;
  }

  public <R> Future<R> map(Function<T, R> mapper) {
    Future<R> next = new Future<>();
    this.onSuccess(result -> {
      try {
        next.complete(mapper.apply(result));
      } catch (Throwable e) {
        next.fail(e);
      }
    });
    this.onFailure(next::fail);
    return next;
  }

  public <R> Future<R> mapEmpty() {
    return map(result -> null);
  }

  public <R> Future<R> compose(Function<T, Future<R>> composer) {
    Future<R> next = new Future<>();
    this.onSuccess(result -> {
      try {
        composer.apply(result)
          .onSuccess(next::complete)
          .onFailure(next::fail);
      } catch (Throwable e) {
        next.fail(e);
      }
    });
    this.onFailure(next::fail);
    return next;
  }

  public Future<T> timeout(long milliseconds) {
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (!isComplete()) {
          fail(new TimeoutException("Timeout after " + milliseconds + " ms"));
        }
      }
    }, milliseconds);
    return this;
  }

  public CompletionStage<T> toCompletionStage() {
    CompletableFuture<T> future = new CompletableFuture<>();

    this.onSuccess(future::complete);
    this.onFailure(future::completeExceptionally);

    return future;
  }

  public static <T> Future<T> succeedFuture() {
    return succeedFuture(null);
  }

  public static <T> Future<T> succeedFuture(T result) {
    Future<T> future = new Future<>();
    future.complete(result);
    return future;
  }

  public static <T> Future<T> failedFuture(Throwable cause) {
    Future<T> future = new Future<>();
    future.fail(cause);
    return future;
  }

}

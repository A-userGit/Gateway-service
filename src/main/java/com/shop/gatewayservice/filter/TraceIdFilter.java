package com.shop.gatewayservice.filter;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

@Component
public class TraceIdFilter implements WebFilter {

  private final Tracer tracer;

  TraceIdFilter(Tracer tracer) {
    this.tracer = tracer;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String traceId = getTraceId();
    if (traceId != null) {
      exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
    }
    return chain.filter(exchange);
  }

  private @Nullable String getTraceId() {
    TraceContext context = this.tracer.currentTraceContext().context();
    return context != null ? context.traceId() : null;
  }
}

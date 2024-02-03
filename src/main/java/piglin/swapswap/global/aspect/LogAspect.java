package piglin.swapswap.global.aspect;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Component
@Aspect
@Slf4j(topic = "log")
public class LogAspect {

    @Before("@annotation(piglin.swapswap.global.annotation.SwapLog)")
    public void swapLog(JoinPoint joinPoint) {

        log.info("\nMethod - {} | Method Argument - {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @Before("@annotation(piglin.swapswap.global.annotation.HttpRequestLog)")
    public void httpRequestLog() {

        MDC.put("traceId", UUID.randomUUID().toString());

        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        log.info("\nIP - {} | Browser - {}\n▼ ▼ ▼ ▼ ▼ ▼ ▼ ▼ ▼ Cookie ▼ ▼ ▼ ▼ ▼ ▼ ▼ ▼ ▼\n{} \n▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲ ▲", getRemoteAddr(req), getBrowser(req), getCookie(req));
    }

    @AfterReturning("@annotation(piglin.swapswap.global.annotation.HttpRequestLog)")
    public void swapLogAfterReturning() {

        MDC.clear();
    }

    @AfterThrowing(value = "@annotation(piglin.swapswap.global.annotation.HttpRequestLog)", throwing = "exception")
    public void swapLogAfterThrowing(Exception exception) {

        log.error("errorCause - ", exception.getCause());
        MDC.clear();
    }

    private String getCookie(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        StringBuilder cookieDetail = new StringBuilder();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieValue = cookie.getValue();

                cookieDetail.append("CookieName: ").append(cookieName).append("\n").append("CookieValue: ").append(cookieValue);
            }
        }

        return cookieDetail.toString();
    }

    public String getRemoteAddr(HttpServletRequest request) {
        String ip = null;
        ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public String getBrowser(HttpServletRequest request) {
        // 에이전트
        String agent = request.getHeader("User-Agent");
        // 브라우져 구분
        String browser = null;
        if (agent != null) {
            if (agent.indexOf("Trident") > -1) {
                browser = "MSIE";
            } else if (agent.indexOf("Chrome") > -1) {
                browser = "Chrome";
            } else if (agent.indexOf("Opera") > -1) {
                browser = "Opera";
            } else if (agent.indexOf("iPhone") > -1 && agent.indexOf("Mobile") > -1) {
                browser = "iPhone";
            } else if (agent.indexOf("Android") > -1 && agent.indexOf("Mobile") > -1) {
                browser = "Android";
            }
        }
        return browser;
    }

}

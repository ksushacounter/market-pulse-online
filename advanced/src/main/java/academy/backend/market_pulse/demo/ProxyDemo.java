package academy.backend.market_pulse.demo;

import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Stock;
import academy.backend.market_pulse.proxy.ProxyFactory;
import academy.backend.market_pulse.proxy.ProxyFactory.Kind;
import academy.backend.market_pulse.repository.InstrumentRepository;

import java.math.BigDecimal;
import java.util.Arrays;

public class ProxyDemo {

    public static void main(String[] args) {
        Arrays.stream(ProxyFactory.Kind.values()).forEach(ProxyDemo::demo);
    }

    // NOTICE: различные типы проксирования
    // Если возникает ошибка вида "Unable to make ... accessible ...",
    // то нужно добавить --add-opens java.base/java.lang=ALL-UNNAMED
    // в VM options
    private static void demo(Kind kind) {
        System.out.println("Proxy type: " + kind);
        InstrumentRepository proxy = ProxyFactory.timingRepository(kind);
        proxy.add(new Stock("SBER", "Сбербанк", Currency.RUB, "Financials", new BigDecimal("6.5")));
        proxy.findByTicker("SBER");
    }
}

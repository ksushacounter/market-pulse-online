package academy.backend.market_pulse.factory;

import academy.backend.market_pulse.dictionary.InstrumentType;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Фабрика, реализованная с помощью вызова статических методов.
 * НО! В ней есть проблема! Если будет интересно - могу потом рассказать!
 */
@UtilityClass
public final class StaticInstrumentFactories {

    private static final Map<InstrumentType, InstrumentFactory> REGISTRY = new ConcurrentHashMap<>();

    public static void register(InstrumentFactory factory) {
        REGISTRY.put(factory.getType(), factory);
    }

    public static Instrument create(InstrumentType type, String ticker, String name, Currency currency) {
        InstrumentFactory factory = REGISTRY.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown instrument type: " + type);
        }
        return factory.create(ticker, name, currency);
    }
}

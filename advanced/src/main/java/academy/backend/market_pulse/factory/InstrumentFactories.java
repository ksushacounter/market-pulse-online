package academy.backend.market_pulse.factory;

import academy.backend.market_pulse.dictionary.InstrumentType;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Фабрика, реализованная с помощью SPI.
 */
@UtilityClass
public final class InstrumentFactories {

    private static final Map<String, InstrumentFactory> REGISTRY = load();

    public static Instrument create(InstrumentType type, String ticker, String name, Currency currency) {
        InstrumentFactory factory = REGISTRY.get(type.name());
        if (factory == null) {
            throw new IllegalArgumentException("Unknown instrument type: " + type);
        }
        return factory.create(ticker, name, currency);
    }

    private static Map<String, InstrumentFactory> load() {
        // NOTICE: пример подгрузки классов с помощью ServiceLoader
        Map<String, InstrumentFactory> registry = new HashMap<>();
        for (InstrumentFactory factory : ServiceLoader.load(InstrumentFactory.class)) {
            registry.put(factory.getType().name(), factory);
        }
        return registry;
    }
}

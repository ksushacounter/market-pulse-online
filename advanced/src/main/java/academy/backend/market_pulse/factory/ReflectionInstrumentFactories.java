package academy.backend.market_pulse.factory;

import academy.backend.market_pulse.dictionary.InstrumentType;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.reflections.Reflections;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Фабрика, реализованная с помощью рефлексии и библиотеки Reflections.
 */
@UtilityClass
public final class ReflectionInstrumentFactories {

    private static final Map<InstrumentType, InstrumentFactory> REGISTRY = load();

    public static Instrument create(InstrumentType type, String ticker, String name, Currency currency) {
        InstrumentFactory factory = REGISTRY.get(type);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown instrument type: " + type);
        }
        return factory.create(ticker, name, currency);
    }

    private static Map<InstrumentType, InstrumentFactory> load() {
        final var reflections = new Reflections("academy.backend");
        return reflections.getSubTypesOf(InstrumentFactory.class).stream()
                .map(ReflectionInstrumentFactories::createFactory)
                .collect(Collectors.toMap(InstrumentFactory::getType, factory -> factory));
    }

    @SneakyThrows
    private static InstrumentFactory createFactory(Class<? extends InstrumentFactory> factoryClass) {
        return factoryClass.getDeclaredConstructor().newInstance();
    }
}

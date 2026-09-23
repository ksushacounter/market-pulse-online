package academy.backend.market_pulse.repository;

import academy.backend.market_pulse.model.Instrument;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Реализация {@link InstrumentRepository} поверх внутреннего массива фиксированного размера.
 */
public class InMemoryInstrumentRepository implements InstrumentRepository {

    private final Instrument[] instruments = new Instrument[100];
    private int size = 0;

    @Override
    public void add(Instrument instrument) {
        instruments[size++] = instrument;
    }

    // NOTICE: forEach работает только благодаря реализации паттерна Iterator
    @Override
    public Instrument findByTicker(String ticker) {
        for (Instrument instrument : this) {
            if (instrument.getTicker().equalsIgnoreCase(ticker)) {
                return instrument;
            }
        }
        return null;
    }

    // NOTICE: пример реализации паттерна Iterator с помощью интерфейса
    @Override
    public Iterator<Instrument> iterator() {
        return new Iterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public Instrument next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return instruments[cursor++];
            }
        };
    }
}

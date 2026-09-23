package academy.backend.market_pulse.repository;

import academy.backend.market_pulse.model.Instrument;

// NOTICE: интерфейс, а не просто InMemoryInstrumentRepository, специально ради DIP — команды
// зависят от контракта, а не от реализации, и на этом же контракте строятся JDK Dynamic Proxy и
// CGLIB: обоим нужен тип, отдельный от конкретного класса.
public interface InstrumentRepository extends Iterable<Instrument> {

    void add(Instrument instrument);

    Instrument findByTicker(String ticker);
}

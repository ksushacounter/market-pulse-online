package academy.backend.market_pulse.model;

import academy.backend.market_pulse.model.aware.InstrumentTypeAware;
import lombok.Getter;

/**
 * Базовая абстракция финансового инструмента. Инкапсулирует общие для всех
 * инструментов данные (тикер, название, валюта) и защищает их инварианты
 * прямо в конструкторе.
 */
@Getter
public abstract class Instrument implements InstrumentTypeAware {

    private final String ticker;
    private final String name;
    private final Currency currency;

    public Instrument(String ticker, String name, Currency currency) {
        if (ticker == null || ticker.isBlank()) {
            throw new IllegalArgumentException("Ticker cannot be blank");
        }
        this.ticker = ticker;
        this.name = name;
        this.currency = currency;
    }

    public abstract String getDescription();

    @Override
    public String toString() {
        return "%s(%s)".formatted(getClass().getSimpleName(), ticker);
    }
}

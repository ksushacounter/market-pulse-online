package academy.backend.market_pulse.cli;

import academy.backend.market_pulse.dictionary.InstrumentType;
import academy.backend.market_pulse.filter.InstrumentFilter;
import academy.backend.market_pulse.filter.PriceOp;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Command(name = "list", description = "Список инструментов")
public class ListCommand implements Callable<Integer> {

    @Option(names = "--type", description = "Фильтр по типу инструмента")
    private InstrumentType type;

    @Option(names = "--ticker", description = "Фильтр по части тикера")
    private String ticker;

    @Option(names = "--currency", description = "Фильтр по валюте")
    private Currency currency;

    @Option(names = "--price-op", description = "Оператор сравнения цены")
    private PriceOp priceOp;

    @Option(names = "--price", description = "Значение цены/дивидендной доходности для фильтра")
    private BigDecimal price;

    private final InstrumentRepository repository;

    @Override
    public Integer call() {
        validateSingleFilterRequirement();

        InstrumentFilter filter = selectFilter();

        int matchCount = 0;
        for (Instrument instrument : repository) {
            if (filter.matches(instrument)) {
                System.out.println(instrument.getDescription());
                matchCount++;
            }
        }

        return matchCount;
    }

    private InstrumentFilter selectFilter() {
        if (type != null) {
            return InstrumentFilter.byType(type); // ФТ3
        }
        if (ticker != null && !ticker.isBlank()) {
            return InstrumentFilter.byTicker(ticker); // ФТ4
        }
        if (currency != null) {
            return InstrumentFilter.byCurrency(currency); // ФТ5
        }
        if (priceOp != null && price != null) {
            return InstrumentFilter.byPrice(priceOp, price); // ФТ6
        }

        return InstrumentFilter.all(); // ФТ2 (Без отбора)
    }

    private void validateSingleFilterRequirement() { //ФТ7
        int activeFiltersCount = 0;

        if (type != null) activeFiltersCount++;
        if (ticker != null && !ticker.isBlank()) activeFiltersCount++;
        if (currency != null) activeFiltersCount++;

        if (priceOp != null || price != null) {
            if (priceOp == null || price == null) {
                throw new IllegalArgumentException("Параметры --price-op и --price должны передаваться одновременно!");
            }
            activeFiltersCount++;
        }

        if (activeFiltersCount > 1) {
            throw new IllegalArgumentException(
                    "Ошибка: Критерии отбора взаимоисключающие:("
            );
        }
    }

}

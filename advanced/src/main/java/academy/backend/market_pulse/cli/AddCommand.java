package academy.backend.market_pulse.cli;

import academy.backend.market_pulse.dictionary.InstrumentType;
import academy.backend.market_pulse.factory.InstrumentFactories;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.repository.InstrumentRepository;
import lombok.RequiredArgsConstructor;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Command(name = "add", description = "Добавление инструмента в репозиторий")
public class AddCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "Тип инструмента (STOCK, BOND, ETF)")
    private String type;

    @Parameters(index = "1", description = "Тикер инструмента")
    private String ticker;

    @Parameters(index = "2", description = "Название инструмента")
    private String name;

    @Parameters(index = "3", description = "Валюта инструмента (RUB, USD, EUR)")
    private Currency currency;

    private final InstrumentRepository repository;

    @Override
    public Integer call() {
        var instrument = InstrumentFactories.create(InstrumentType.valueOf(type), ticker, name, currency);
        repository.add(instrument);
        return 1;
    }

}

package academy.backend.market_pulse;

import academy.backend.market_pulse.cli.AddCommand;
import academy.backend.market_pulse.cli.ListCommand;
import academy.backend.market_pulse.cli.MarketPulseCli;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Stock;
import academy.backend.market_pulse.repository.InMemoryInstrumentRepository;
import academy.backend.market_pulse.repository.InstrumentRepository;
import picocli.CommandLine;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        InstrumentRepository repository = new InMemoryInstrumentRepository();
        // NOTICE: подкоманды регистрируются вручную через addSubcommand — у них нет
        //  конструктора без аргументов, каждой нужен уже готовый repository.
        repository.add(new Stock("AAPL", "Apple Inc.", Currency.USD, "IT", new BigDecimal("12.5")));
        repository.add(new Stock("SBER", "Сбербанк", Currency.RUB, "Finance", new BigDecimal("8.0")));

        CommandLine cli = new CommandLine(new MarketPulseCli())
                .addSubcommand(new AddCommand(repository))
                .addSubcommand(new ListCommand(repository));
        int exitCode = cli.execute(args);
        System.exit(exitCode);
    }
}

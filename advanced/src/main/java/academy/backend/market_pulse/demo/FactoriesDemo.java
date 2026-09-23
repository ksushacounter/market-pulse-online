package academy.backend.market_pulse.demo;

import academy.backend.market_pulse.dictionary.InstrumentType;
import academy.backend.market_pulse.factory.InstrumentFactories;
import academy.backend.market_pulse.factory.ReflectionInstrumentFactories;
import academy.backend.market_pulse.factory.StaticInstrumentFactories;
import academy.backend.market_pulse.model.Currency;

public class FactoriesDemo {

    public static void main(String[] args) {
        var stock1 = InstrumentFactories.create(InstrumentType.STOCK, "AAPL", "Apple Inc.", Currency.USD);
        System.out.println(stock1.getDescription());

        var stock2 = StaticInstrumentFactories.create(InstrumentType.STOCK, "AAPL", "Apple Inc.", Currency.USD);
        System.out.println(stock2.getDescription());

        var stock3 = ReflectionInstrumentFactories.create(InstrumentType.STOCK, "AAPL", "Apple Inc.", Currency.USD);
        System.out.println(stock3.getDescription());
    }
}

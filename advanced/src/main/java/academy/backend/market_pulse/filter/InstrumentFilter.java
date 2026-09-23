package academy.backend.market_pulse.filter;

import academy.backend.market_pulse.dictionary.InstrumentType;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Instrument;
import academy.backend.market_pulse.model.Stock;

import java.math.BigDecimal;

/**
 * Правило отбора инструментов для команды {@code list}.
 *
 * <p>Функциональные требования:
 * <ul>
 *     <li>ФТ1. Можно проверить, подходит ли инструмент под правило отбора —
 *     {@link #matches(Instrument)}.</li>
 *     <li>ФТ2. Есть правило «без отбора» — подходят все инструменты.</li>
 *     <li>ФТ3. Есть правило «по типу» — подходит инструмент, чей {@code getType()} совпадает с
 *     переданным значением.</li>
 *     <li>ФТ4. Есть правило «по тикеру» — подходит инструмент, чей тикер содержит переданную
 *     подстроку, без учёта регистра.</li>
 *     <li>ФТ5. Есть правило «по валюте» — подходит инструмент, чья валюта точно совпадает с
 *     переданной.</li>
 *     <li>ФТ6. Есть правило «по цене» — применимо только к {@code Stock}: для остальных типов
 *     инструментов — всегда {@code false}. В роли цены — дивидендная доходность
 *     ({@code Stock.getDividendYield()}), единственный числовой атрибут акции.</li>
 *     <li>ФТ7. Критерии взаимоисключающие — одновременно действует не больше одного правила
 *     отбора; если пользователь запросил больше одного сразу — это ошибка!</li>
 * </ul>
 *
 * <p>Примеры пользовательского ввода:
 * <pre>{@code
 * list --type BOND                  // только облигации
 * list --ticker AAP                 // тикер содержит "AAP" — найдёт, например, AAPL
 * list --currency USD               // только инструменты в долларах
 * list --price-op GE --price 10     // акции с дивидендной доходностью >= 10
 * }</pre>
 */
public interface InstrumentFilter {

    boolean matches(Instrument instrument); //ФТ1

    static InstrumentFilter all() { //ФТ2
        return instrument -> true;
    }

    static InstrumentFilter byType(InstrumentType type) { //ФТ3
        if (type == null) {
            return all();
        }
        return instrument -> {
            return switch (type) {
                case STOCK -> instrument instanceof Stock;
                case BOND -> instrument.getClass().getSimpleName().equalsIgnoreCase("Bond");
                case ETF -> instrument.getClass().getSimpleName().equalsIgnoreCase("Etf");
            };
        };
    }

    static InstrumentFilter byTicker(String tickerSubstring) { //ФТ4
        if (tickerSubstring == null || tickerSubstring.isBlank()) {
            return all();
        }
        return instrument -> instrument.getTicker()
                .toLowerCase()
                .contains(tickerSubstring.toLowerCase());
    }

    static InstrumentFilter byCurrency(Currency currency) { //ФТ5
        if (currency == null) {
            return all();
        }
        return instrument -> instrument.getCurrency() == currency;
    }

    static InstrumentFilter byPrice(PriceOp op, BigDecimal targetPrice) { //ФТ6
        if (op == null || targetPrice == null) {
            return all();
        }
        return instrument -> {
            if (!(instrument instanceof Stock stock)) {
                return false;
            }
            BigDecimal yield = stock.getDividends(BigDecimal.valueOf(100));
            int cmp = yield.compareTo(targetPrice);

            return switch (op) {
                case EQ -> cmp == 0;
                case GT -> cmp > 0;
                case GE -> cmp >= 0;
                case LT -> cmp < 0;
                case LE -> cmp <= 0;
            };
        };
    }

}

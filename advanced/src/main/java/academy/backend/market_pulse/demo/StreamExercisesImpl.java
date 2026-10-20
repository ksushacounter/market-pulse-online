package academy.backend.market_pulse.demo;

import academy.backend.market_pulse.dictionary.InstrumentType;
import academy.backend.market_pulse.model.Currency;
import academy.backend.market_pulse.model.Quote;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;

public class StreamExercisesImpl implements StreamExercises {

    @Override
    public List<String> tickersInCurrency(List<Quote> quotes, Currency currency) {
        return quotes.stream()
                .filter(it -> it.getInstrument().getCurrency().equals(currency))
                .map(it -> it.getInstrument().getName())
                .toList();
    }

    @Override
    public List<Quote> topByPrice(List<Quote> quotes, int n) {
        return quotes.stream()
                .sorted(Comparator.comparing(Quote::getPrice).reversed())
                .limit(n)
                .toList();
    }

    @Override
    public Set<Currency> distinctCurrencies(List<Quote> quotes) {
        return quotes.stream()
                .map(it -> it.getInstrument().getCurrency())
                .collect(Collectors.toSet());

    }

    @Override
    public boolean hasAnyLoser(List<Quote> quotes) {
        return quotes.stream()
                .anyMatch(it -> it.getInstrument().getCurrency().equals(Currency.USD));
    }

    @Override
    public long countByType(List<Quote> quotes, InstrumentType instrumentType) {
        return quotes.stream()
                .filter(it -> it.getInstrument().getType() == (instrumentType))
                .count();
    }

    @Override
    public BigDecimal totalPrice(List<Quote> quotes) {
        return quotes.stream()
                .map(Quote::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public double averageChangePercent(List<Quote> quotes) {
        return quotes.stream()
                .mapToDouble(it -> it.getChangePercent().doubleValue())
                .average()
                .orElse(0.0);
    }

    @Override
    public Map<InstrumentType, Long> countByTypeGrouped(List<Quote> quotes) {
        return quotes.stream()
                .collect(Collectors.groupingBy(it -> it.getInstrument().getType(), Collectors.counting()));
    }

    @Override
    public Map<InstrumentType, Double> averageChangeByType(List<Quote> quotes) {
        return quotes.stream()
                .collect(Collectors.groupingBy(it -> it.getInstrument().getType(), Collectors.averagingDouble(it -> it.getChangePercent().doubleValue())));
    }

    /**
     * Упражнение 10. Разбить котировки на выросшие ({@code changePercent > 0}) и все остальные —
     * ровно два ключа, {@code true}/{@code false}. ({@code partitioningBy}).
     */
    @Override
    public Map<Boolean, List<Quote>> partitionByGain(List<Quote> quotes) {
        return quotes.stream()
                .collect(Collectors.partitioningBy(it -> it.getChangePercent() != null && it.getChangePercent().signum() == 1));
    }

    /**
     * Упражнение 11. Котировка по тикеру; если один и тот же тикер встречается в списке несколько
     * раз (например, цена обновилась) — оставить ту, у которой цена выше. ({@code toMap} с
     * merge-функцией для разрешения дубликатов ключа).
     */

    @Override
    public Map<String, Quote> latestByTicker(List<Quote> quotes) {
        return quotes.stream().collect(Collectors.toMap(quote -> quote.getInstrument().getName(),
                quote -> quote, (existing, replacement) -> existing.getPrice().compareTo(replacement.getPrice()) >= 0 ? existing : replacement
        ));
    }

    /**
     * Упражнение 12. Все тикеры из списка котировок одной строкой — через {@code ", "},
     * в квадратных скобках; дубликаты тикеров (как в упражнении 11) здесь не схлопывать, просто
     * перечислить все по порядку. ({@code map} + {@code Collectors.joining(delimiter, prefix,
     * suffix)}).
     */
    @Override
    public String joinTickers(List<Quote> quotes) {
        return quotes.stream()
                .map(it -> it.getInstrument().getTicker())
                .collect(Collectors.joining(", ", "[", "]"));
    }

    /**
     * Упражнение 13. Для каждой валюты — строка со всеми её тикерами через {@code ", "} (без
     * квадратных скобок), без дублей-пропусков (дубликат тикера, если он есть, встречается в строке
     * дважды, как и в упражнении 12). Комбинация трёх коллекторов: {@code groupingBy(currency)}, у
     * которого downstream — {@code mapping(ticker, joining(", "))}: {@code mapping} сначала
     * проецирует элемент группы в тикер, и только потом {@code joining} их склеивает. Тот же приём,
     * что в {@code TickersByTypeStat} команды {@code stats}.
     */
    @Override
    public Map<Currency, String> tickersJoinedByCurrency(List<Quote> quotes) {
        return quotes.stream()
                .collect(Collectors.groupingBy(it -> it.getInstrument().getCurrency(), Collectors.mapping(it -> it.getInstrument().getName(), Collectors.joining(", "))));

    }

    /**
     * Упражнение 14. Котировки по списку тикеров, в порядке списка тикеров; тикеры, которых нет в
     * {@code quotes}, молча пропускаются. ({@code map} в {@code Optional<Quote>} + {@code flatMap}
     * {@code (Optional::stream)} — тот же приём, что в {@code CompareCommand}).
     */
    @Override
    public List<Quote> findByTickers(List<Quote> quotes, List<String> tickers) {
        return tickers.stream()
                .map(ticker -> quotes.stream()
                        .filter(q -> q.getInstrument().getName().equals(ticker))
                        .findFirst())
                .flatMap(Optional::stream)
                .toList();
    }

    /**
     * Упражнение 15. Скользящее среднее цены по окну {@code windowSize} подряд идущих котировок —
     * среднее округлено до 2 знаков ({@link java.math.RoundingMode#HALF_UP}). Решается через
     * {@code Gatherers.windowSliding(windowSize)} + {@code map}: встроенный gatherer собирает окна,
     * дальше остаётся только посчитать среднее в каждом. Тот же приём, что в
     * {@code WindowedAveragePriceStat} команды {@code stats}.
     */
    @Override
    public List<BigDecimal> movingAveragePrice(List<Quote> quotes, int windowSize) {
        return quotes.stream()
                .map(Quote::getPrice)
                .gather(Gatherers.windowSliding(windowSize))
                .map(it -> it.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(it.size()), 2, RoundingMode.HALF_UP))
                .toList();
    }

    /**
     * ужас)
     * Упражнение 16 (самое сложное). Длина самой длинной серии подряд идущих убыточных котировок
     * ({@code changePercent < 0}) во входном списке; {@code 0}, если убыточных котировок нет.
     * Классическая задача на состояние между соседними элементами — не выражается через
     * {@code map}/{@code filter}/{@code limit}: нужен счётчик текущей серии, который растёт на
     * убыточной котировке и сбрасывается на прибыльной/нулевой, плюс финальный учёт серии,
     * дошедшей до конца списка. Решается кастомным {@code Gatherer.ofSequential(initializer,
     * integrator)} с {@code finisher}.
     */
    @Override
    public int longestLossStreak(List<Quote> quotes) {
        return quotes.stream()
                .map(it -> it.getChangePercent().signum() < 0)
                .gather(Gatherer.<Boolean, State, Integer>ofSequential(State::new,
                        (state, isLoss, downstream) -> {
                            if (isLoss) {
                                state.currentStreak++;
                            } else {
                                state.currentStreak = 0;
                            }
                            downstream.push(state.currentStreak);
                            return true;
                        }))
                .max(Integer::compareTo)
                .orElse(0);
    }

    private static class State {
        int currentStreak = 0;
    }
}

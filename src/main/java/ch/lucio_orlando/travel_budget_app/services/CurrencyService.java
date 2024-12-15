package ch.lucio_orlando.travel_budget_app.services;

import ch.lucio_orlando.travel_budget_app.models.Currency;
import ch.lucio_orlando.travel_budget_app.repositories.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<Currency> getCurrencies() {
        return currencyRepository.findAll();
    }

    public Currency getCurrencyById(Long id) {
        return currencyRepository.findById(id).orElse(null);
    }

    public void saveCurrency(Currency currency) {
        currencyRepository.save(currency);
    }

    public Currency getCurrencyByCode(String code) {
        return this.getCurrencies().stream().filter(currency -> currency.getCode().equals(code)).findFirst().orElse(null);
    }
}

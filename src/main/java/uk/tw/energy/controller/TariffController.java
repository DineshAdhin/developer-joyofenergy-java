package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.service.TariffService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tariff")
public class TariffController {

    private final TariffService tariffService;
    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;
    private final Map<String, String> smartMeterToPricePlanAccounts;

    public TariffController(TariffService tariffService, Map<String, List<ElectricityReading>> meterAssociatedReadings, Map<String, String> smartMeterToPricePlanAccounts) {
        this.tariffService = tariffService;
        this.meterAssociatedReadings = meterAssociatedReadings;
        this.smartMeterToPricePlanAccounts = smartMeterToPricePlanAccounts;
    }

    @GetMapping("/cost/{smartMeterId}")
    public ResponseEntity calculateCost(@PathVariable String smartMeterId) {
        String pricePlanId = smartMeterToPricePlanAccounts.get(smartMeterId);
        Optional<List<ElectricityReading>> readings = Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
        if(ObjectUtils.isEmpty(pricePlanId)) {
            return ResponseEntity.ok("PricePlanId/MeterId not found");
        } else if(readings.isPresent()) {
            String result = tariffService.calculateLastWeekCost(smartMeterId, readings.get(), pricePlanId);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.ok("0.0");
        }
    }
}

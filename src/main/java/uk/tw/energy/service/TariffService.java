package uk.tw.energy.service;

import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class TariffService {

    private final PricePlanService pricePlanService;
    private final List<PricePlan> pricePlans;


    public TariffService(PricePlanService pricePlanService, List<PricePlan> pricePlans) {
        this.pricePlanService = pricePlanService;
        this.pricePlans = pricePlans;
    }

    public String calculateLastWeekCost(String smartMeterId, List<ElectricityReading> readings, String pricePlanId) {

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, -i - 7);
        Instant start = c.getTime().toInstant();
        c.add(Calendar.DATE, 6);
        Instant end = c.getTime().toInstant();

        List<ElectricityReading> lastWeekList = new ArrayList<ElectricityReading>();
        for(ElectricityReading tmp: readings) {
            if(tmp.time().isAfter(start) && tmp.time().isBefore(end)) {
                lastWeekList.add(tmp);
            }
        }

        if(lastWeekList.size() > 0){
            PricePlan pricePlan = this.pricePlans.stream().filter(tmp -> tmp.getPlanName().equals(pricePlanId)).findFirst().get();
            BigDecimal total = pricePlanService.calculateLastWeekCost(lastWeekList, pricePlan);
            return total.toString();
        } else {
            return "0.0";
        }


    }

}

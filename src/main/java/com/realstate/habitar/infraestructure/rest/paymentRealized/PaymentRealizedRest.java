package com.realstate.habitar.infraestructure.rest.paymentRealized;

import com.realstate.habitar.application.components.FunnelRentComponent;
import com.realstate.habitar.domain.dtos.response.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-realized")
public class PaymentRealizedRest {

    private final FunnelRentComponent funnelRentComponent;

    public PaymentRealizedRest(FunnelRentComponent funnelRentComponent) {
        this.funnelRentComponent = funnelRentComponent;
    }

    public record DataRecord(String uuid){}

    @PostMapping("/")
    public ResponseEntity<ResponseDto> createPaymentRealized(@RequestBody DataRecord dataRecord){
        funnelRentComponent.liquidatePlanDeal(dataRecord.uuid);
        return ResponseEntity.ok(new ResponseDto("OK"));
    }
}

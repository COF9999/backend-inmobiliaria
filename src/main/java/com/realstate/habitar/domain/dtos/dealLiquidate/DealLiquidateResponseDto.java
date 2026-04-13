package com.realstate.habitar.domain.dtos.dealLiquidate;

import com.realstate.habitar.domain.dtos.dealRegister.DealRegisterDto;
import com.realstate.habitar.domain.enums.StatesObjects;

public record DealLiquidateResponseDto(DealRegisterDto dealRegisterDto, StatesObjects state) {
}

package com.realstate.habitar.domain.dtos.sales;

import java.time.LocalDateTime;
import java.util.Objects;

public record LiquidationTimeRecord(
                                     Integer onlyDay,
                                     LocalDateTime dateOne,
                                     LocalDateTime dateSecond,
                                     Long milesecondsStartDay,
                                     Long milesecondsEndDay) {

    public static class BuilderLiquidationTime {
        private Integer onlyDay;
        private LocalDateTime dateOne;
        private LocalDateTime dateSecond;
        private Long milesecondsStartDay;
        private Long milesecondsEndDay;


        public BuilderLiquidationTime setOnlyDay(Integer onlyDay) {
            this.onlyDay = onlyDay;
            return this;
        }

        public BuilderLiquidationTime setStartDay(LocalDateTime startDay) {
            this.dateOne = startDay;
            return this;
        }

        public BuilderLiquidationTime setEndDay(LocalDateTime endDay) {
            this.dateSecond = endDay;
            return this;
        }

        public BuilderLiquidationTime setMilesecondsStartDay(Long milesecondsStartDay) {
            this.milesecondsStartDay = milesecondsStartDay;
            return this;
        }

        public BuilderLiquidationTime setMilesecondsEndDay(Long milesecondsEndDay) {
            this.milesecondsEndDay = milesecondsEndDay;
            return this;
        }

        private String concateErrorMessage(String field){
            return "Campo "+field+"Es Obligatorio";
        }

        public LiquidationTimeRecord build(){

            if (this.onlyDay !=null){

                if (this.dateOne !=null || this.dateSecond!=null){
                    throw new IllegalArgumentException("El parametro para días no es el unico campo");
                }


            }else {
                Objects.requireNonNull(this.milesecondsStartDay,concateErrorMessage(String.valueOf(this.milesecondsStartDay)));
                Objects.requireNonNull(this.milesecondsEndDay,concateErrorMessage(String.valueOf(this.milesecondsEndDay)));
            }

            return new LiquidationTimeRecord(
                    this.onlyDay,
                    this.dateOne,
                    this.dateSecond,
                    this.milesecondsStartDay,
                    this.milesecondsEndDay);
        }

    }
}

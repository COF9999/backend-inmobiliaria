package com.realstate.habitar.domain.dtos.sales;

import java.util.Objects;

public record LiquidationTimeRecord(
                                     Integer onlyDay,
                                     Integer year,
                                     Integer month,
                                     Integer startDay,
                                     Integer endDay,
                                     Long milesecondsStartDay,
                                     Long milesecondsEndDay) {

    public static class BuilderLiquidationTime {
        private Integer onlyDay;
        private Integer year;
        private Integer month;
        private Integer startDay;
        private Integer endDay;
        private Long milesecondsStartDay;
        private Long milesecondsEndDay;

        public BuilderLiquidationTime setYear(int year){
            this.year = year;
            return this;
        }

        public BuilderLiquidationTime setMonth(Integer month) {
            this.month = month;
            return this;
        }

        public BuilderLiquidationTime setOnlyDay(Integer onlyDay) {
            this.onlyDay = onlyDay;
            return this;
        }

        public BuilderLiquidationTime setStartDay(Integer startDay) {
            this.startDay = startDay;
            return this;
        }

        public BuilderLiquidationTime setEndDay(Integer endDay) {
            this.endDay = endDay;
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

                if (this.year !=null || this.month!=null || this.startDay != null){
                    throw new IllegalArgumentException("El parametro para días no es el unico campo");
                }


            }else {
                Objects.requireNonNull(this.year,concateErrorMessage(String.valueOf(this.year)));
                Objects.requireNonNull(this.month,concateErrorMessage(String.valueOf(this.month)));
                Objects.requireNonNull(this.startDay,concateErrorMessage(String.valueOf(this.startDay)));
                Objects.requireNonNull(this.endDay,concateErrorMessage(String.valueOf(this.endDay)));
            }

            return new LiquidationTimeRecord(
                    this.onlyDay,
                    this.year,
                    this.month,
                    this.startDay,
                    this.endDay,
                    this.milesecondsStartDay,
                    this.milesecondsEndDay);
        }

    }
}

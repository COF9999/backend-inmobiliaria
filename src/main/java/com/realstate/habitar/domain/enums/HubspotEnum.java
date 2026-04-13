package com.realstate.habitar.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@AllArgsConstructor
public enum HubspotEnum{
    FUNNELSALES {
        @Override
        public List<String> extractProperties() {
            return HubspotEnum.PropertiesHubspot.Sales.getPropertiesSales();
        }
    },
    FUNNELRENT {
        @Override
        public List<String> extractProperties() {
            return HubspotEnum.PropertiesHubspot.Rent.getListPropertiesRent();
        }
    },
    FUNNELRENTPLACEMENT {
        @Override
        public List<String> extractProperties() {
            return List.of();
        }
    };

    public abstract List<String> extractProperties();

        @Getter
        @AllArgsConstructor
        public enum PropertiesHubspot {
            AMOUNT("amount"),
            CLOSEDATE("closedate"),
            HUBSPOT_OWNER_ID("hubspot_owner_id"),
            DEALNAME("dealname"),
            DEALSTAGE("dealstage"),
            PIPELINE("pipeline");


            private final String value;

            private static final List<String> listCommonProperties;

            static {
                listCommonProperties = Arrays.stream(values())
                        .map(PropertiesHubspot::getValue)
                        .sorted() // <--- Orden alfabético constante en la URL
                        .toList();
            }

            public static List<String> getListCommonProperties() {
                return listCommonProperties;
            }


                public enum Sales{
                    ;
                    public static List<String> getPropertiesSales(){
                        return PropertiesHubspot.getListCommonProperties();
                    }
                }

                @Getter
                @AllArgsConstructor
                public enum Rent{
                    LISTINGAGENT("captador_del_inmueble"),
                    CONTRACTDURATION("duracion_del_contrato"){
                        @Override
                        public Integer extractNumber(String contractDuration){
                            try {
                                int num = Integer.parseInt(contractDuration.replaceAll("\\D+", ""));
                                // Validación instantánea contra el Set
                                if (Duration.ALLOWED_MONTHS.contains(num)) {
                                    return num;
                                }
                            } catch (NumberFormatException ignored) {}

                            throw new IllegalArgumentException("Duración no permitida. Solo se acepta: " + Duration.ALLOWED_MONTHS);
                        }
                    };

                    private final String value;

                    private static final List<String> listRentProperties;

                    static {
                        listRentProperties = Arrays.stream(values()).map(Rent::getValue).toList();
                    }

                    public Integer extractNumber(String contractDuration) {
                        throw new UnsupportedOperationException("Este método solo es válido para CONTRACTDURATION");
                    }

                    public static List<String> getListPropertiesRent(){
                        return Stream.concat(
                                PropertiesHubspot.Sales.getPropertiesSales().stream(),
                                listRentProperties.stream())
                                .collect(Collectors.toList());
                    }

                    @Getter
                    @AllArgsConstructor
                    private enum Duration {
                        SIX_MONTHS(6), TWELVE_MONTHS(12);

                        private final int months;

                        private static final Set<Integer> ALLOWED_MONTHS;

                        static {
                            ALLOWED_MONTHS = Arrays.stream(values())
                                    .map(d -> d.months)
                                    .collect(Collectors.toUnmodifiableSet());
                        }
                    }

                }
        }
}









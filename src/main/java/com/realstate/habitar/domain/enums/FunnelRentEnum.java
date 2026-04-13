package com.realstate.habitar.domain.enums;

import com.realstate.habitar.application.usecases.user.UserServiceBasicOperations;
import com.realstate.habitar.domain.dtos.hubspot.HubspotDealDtoApp;
import com.realstate.habitar.infraestructure.advicers.exceptions.ResourceNotFound;
import com.realstate.habitar.infraestructure.classes.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@Getter
public enum FunnelRentEnum {
    TOTALONEYEAR((byte) 0,""),
    TOTALSIXMONTH((byte) 1,""),
    NORMALAGENTSIXMONTH((byte) 2,""),
    NORMALAGENTONEYEAR((byte) 3,"");

    private final byte key;
    private final String value;


    public enum WrapperUser{
        SAMEAGENT{
            @Override
            public List<User> delegateFunction(HubspotDealDtoApp hubspotDealDtoApp, UserServiceBasicOperations userServiceBasicOperations, String keyListingAgent) {
                User userNormal = getAgent(userServiceBasicOperations,hubspotDealDtoApp,HubspotDealDtoApp::ownerId);
                return List.of(userNormal);
            }
        },
        DIFFERENTAGENT{
            @Override
            public List<User> delegateFunction(HubspotDealDtoApp hubspotDealDtoApp, UserServiceBasicOperations userServiceBasicOperations,String keyListingAgent) {
                User userNormal = getAgent(userServiceBasicOperations,hubspotDealDtoApp, HubspotDealDtoApp::ownerId);
                User userListing = getAgent(userServiceBasicOperations,hubspotDealDtoApp,(h)-> h.properties().get(keyListingAgent));
                return List.of(userListing,userNormal);
            }
        };

        private static <T> User getAgent(UserServiceBasicOperations userServiceBasicOperations, T t, Function<T,String> function){
            return userServiceBasicOperations.findUserByHubId(function.apply(t)).orElseThrow(()-> new ResourceNotFound("User not found"));
        }


        public abstract List<User> delegateFunction(HubspotDealDtoApp hubspotDealDtoApp, UserServiceBasicOperations userServiceBasicOperations, String keyListingAgent);
    }


    @AllArgsConstructor
    public enum TypeUsers{
        NORMALAGENT(0),
        LISTINGAGENT(1),
        REALESTATE(2);

        @Getter
        private final Integer order;


        public record InstaceSizePlanContractUsersRecord(FunnelRentEnum.TypeUsers[] instance, boolean isAppointment){}

        public record TypeAgentRecord(int commission, boolean isAppointment){}

        public static InstaceSizePlanContractUsersRecord createInstance(FunnelRentEnum.TypeUsers[] instance, boolean isAppointment){
            return new InstaceSizePlanContractUsersRecord(instance,isAppointment);
        }

        public TypeAgentRecord getTypeAgentRecord(int commission,boolean isAppointment){
            return switch (this){
                case NORMALAGENT, LISTINGAGENT -> new TypeAgentRecord(commission,isAppointment);
                case REALESTATE -> null;
            };
        }

    }
}

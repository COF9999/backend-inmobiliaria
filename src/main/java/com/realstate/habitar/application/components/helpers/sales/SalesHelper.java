package com.realstate.habitar.application.components.helpers.sales;

import com.realstate.habitar.application.usecases.hubspot.HubspotCommand;
import com.realstate.habitar.application.usecases.user.UserService;
import com.realstate.habitar.domain.baselogic.interfaces.DetailVisitorSales;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailPaymentFunnel;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailResumeAbstract;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.DetailUserPayment;
import com.realstate.habitar.domain.dtos.funnelsDtos.sales.Resume;
import com.realstate.habitar.domain.dtos.user.UserRequestDto;
import com.realstate.habitar.infraestructure.adapters.interfaces.userLiquidation.UserLiquidationRepository;
import com.realstate.habitar.infraestructure.classes.model.Role;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Service
public class SalesHelper implements DetailVisitorSales<Void> {

    private final UserLiquidationRepository userLiquidationRepository;
    private final UserService userService;
    private final HubspotCommand hubspotCommands;

    public SalesHelper(UserLiquidationRepository userLiquidationRepository, UserService userService, HubspotCommand hubspotCommands) {
        this.userLiquidationRepository = userLiquidationRepository;
        this.userService = userService;
        this.hubspotCommands = hubspotCommands;
    }

    private void updateAmount(Map<String, DetailUserPayment> resultOperation) {
        String hubId = "";
        BigDecimal acum = BigDecimal.ZERO;
        for (Map.Entry<String, DetailUserPayment> map : resultOperation.entrySet()) {
            hubId = map.getKey();
            DetailUserPayment detailUserPayment  = map.getValue();
            if (userLiquidationRepository.isExistUserLiquidation(hubId)) {
                userLiquidationRepository.addAmount(hubId, acum);
            } else {
                createUserIfExists(hubId);
                userLiquidationRepository.addAmount(hubId,acum);
            }
        }
    }

    private void createUserIfExists(String hubId) {
        Map<String, Object> activeOwner = hubspotCommands.findOwnerByHubId(hubId);

        if (activeOwner != null) {
            // todo crear usuario
            boolean isActive = !((boolean) activeOwner.get("archived"));
            String firstName = (String) activeOwner.get("firstName");
            String username = firstName;
            String name = firstName + " "+ ((String) activeOwner.get("lastName"));
            String email = (String) activeOwner.get("email");
            String identification = "123GENERIC";
            Set<Role> setRoles = Set.of(new Role(null, "ROLE_USER"));
            userService.create(new UserRequestDto(
                    null,
                    hubId,
                    isActive,
                    setRoles,
                    name,
                    username,
                    email,
                    identification,
                    ""
            ));
        }
    }


    @Override
    public Void visit(Resume resume) {
        String hubId = resume.getHubId();
        BigDecimal acum = resume.getTotalAmount();
        userLiquidationRepository.addAmount(hubId, acum);
        return null;
    }

    @Override
    public Void visit(DetailPaymentFunnel detailPaymentFunnel) {
        if (detailPaymentFunnel.getDetailAgentPayment()==null){
            visit(new Resume("",
                    detailPaymentFunnel.getDetailOwnerPayment().hubId(),
                    detailPaymentFunnel.getDetailOwnerPayment().amountToPay(),
                    null));

            return null;
        }
        String hubId = detailPaymentFunnel.getDetailAgentPayment().hubId();
        BigDecimal acum = detailPaymentFunnel.getDetailAgentPayment().amountToPay();
        if (userLiquidationRepository.isExistUserLiquidation(hubId)) {
            userLiquidationRepository.addAmount(hubId, acum);
        } else {
            createUserIfExists(hubId);
            userLiquidationRepository.addAmount(hubId,acum);
        }
        return null;
    }

    public void runClassVisitor(Map<String, DetailResumeAbstract> detailResumeAbstractMap){
        for (Map.Entry<String,DetailResumeAbstract> item:detailResumeAbstractMap.entrySet()){
            if (item.getValue() instanceof DetailPaymentFunnel){
                item.getValue().accept(this);
            }
        }
    }
}

package com.realstate.habitar.application.implementations.user;

import com.realstate.habitar.application.usecases.user.UserService;
import com.realstate.habitar.application.usecases.user.UserServiceBasicOperations;
import com.realstate.habitar.domain.dtos.user.UserRequestDto;
import com.realstate.habitar.domain.dtos.user.UserResponseDto;
import com.realstate.habitar.domain.dtos.token.TokenDto;
import com.realstate.habitar.domain.ports.role.RolePort;
import com.realstate.habitar.domain.ports.user.UserDaoPort;
import com.realstate.habitar.global.domain.ports.DaoCrudPort;
import com.realstate.habitar.global.infraestructure.models.Role;
import com.realstate.habitar.global.infraestructure.models.User;
import com.realstate.habitar.infraestructure.adapters.interfaces.UserLiquidationRepository;
import com.realstate.habitar.infraestructure.advicers.exceptions.ResourceAlreadyExists;
import com.realstate.habitar.infraestructure.classes.model.UserLiquidation;
import com.realstate.habitar.utils.ThrowableActions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserServiceBasicOperations {

    @Value("${config.generic.key}")
    private  String genericKey;

    private final RolePort rolePort;

    private final PasswordEncoder passwordEncoder;

    private final UserDaoPort userDaoPort;

    private final DaoCrudPort<User> userDaoCrudPort;

    private final UserLiquidationRepository userLiquidationRepository;

    private final DaoCrudPort<UserLiquidation> userLiquidationDaoCrudPort;



    public UserServiceImpl(RolePort rolePort,
                           PasswordEncoder passwordEncoder,
                           UserDaoPort userDaoPort,
                           DaoCrudPort<User> userDaoCrudPort,
                           UserLiquidationRepository userLiquidationRepository,
                           DaoCrudPort<UserLiquidation> userLiquidationDaoCrudPort){
        this.rolePort = rolePort;
        this.passwordEncoder = passwordEncoder;
        this.userDaoPort = userDaoPort;
        this.userDaoCrudPort = userDaoCrudPort;
        this.userLiquidationRepository = userLiquidationRepository;
        this.userLiquidationDaoCrudPort = userLiquidationDaoCrudPort;
    }

    @Override
    public UserResponseDto search(Long id) {
        return findUserById(id)
                .map(this::convertToDto)
                .orElseThrow();
    }

    @Override
    @Transactional
    public UserResponseDto create(UserRequestDto userRequestDto) {
        User user;
        Set<Role> roles;
        Boolean isAdmin = false;
        String passwordEncoded = "";
        boolean isActive = true;
        Optional.ofNullable(userRequestDto.email())
                .flatMap(this::findUserByEmail)
                .ifPresent((userOpt)->
                        ThrowableActions.launchRuntimeExeption(
                                ()-> new ResourceAlreadyExists("Error user with email: "+"already exits")
                        ));

        try {
            roles = userRequestDto.roles()
                    .stream()
                    .map(role -> rolePort.findRoleByName(role.getName()))
                    .filter(Optional::isPresent)
                    .map(roleOpt-> (Role) roleOpt.get())
                    .collect(Collectors.toSet());
            if (userRequestDto.isActive()==null || userRequestDto.isActive()){
                passwordEncoded = passwordEncoder.encode(userRequestDto.password());
            }else{
                passwordEncoded = passwordEncoder.encode(genericKey);
                isActive = false;
            }



            user = User.builder()
                    .name(userRequestDto.name())
                    .username(userRequestDto.username())
                    .identification(userRequestDto.identification())
                    .email(userRequestDto.email())
                    .password(passwordEncoded)
                    .hubId(userRequestDto.hubId())
                    .roles(roles)
                    .isAdmin(isAdmin)
                    .isActive(isActive)
                    .build();

            User userCreated =  Optional.ofNullable(create(user)).orElseThrow();
            userLiquidationDaoCrudPort.create(new UserLiquidation(null,userCreated,new BigDecimal("0")));
            return convertToDto(userCreated);
        }catch (Exception e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public UserResponseDto update(UserRequestDto object) {
        User user = new User();
        user.setName(object.name());
        user.setPassword(object.password());
        user.setUsername(object.username());
        user.setRoles(object.roles());

        return  Optional.ofNullable(update(user))
                .map(this::convertToDto)
                .orElseThrow();
    }

    @Override
    public void activeDelete(Long id) {

    }

    @Override
    public UserResponseDto convertToDto(User user) {
        UserRequestDto userRequestDto = new UserRequestDto(
                null,
                user.getHubId(),
                user.getIsActive(),
                user.getRoles(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getIdentification(),
                "",
                user.isAdmin()
                );
        return new UserResponseDto(userRequestDto,new TokenDto(null));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userDaoPort.findByEmail(email)
                .map(objUser-> (User) objUser);
    }

    public Optional<User> findUserById(Long id){
        return userDaoCrudPort.get(id);
    }

    @Override
    public User update(User user) {
        return userDaoCrudPort.update(user)
                .orElseThrow();
    }

    @Override
    public User create(User user) {
        return userDaoCrudPort.create(user)
                .orElseThrow();
    }


}

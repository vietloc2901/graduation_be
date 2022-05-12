package locnv.haui.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import liquibase.pro.packaged.E;
import locnv.haui.config.Constants;
import locnv.haui.domain.Authority;
import locnv.haui.domain.User;
import locnv.haui.repository.AuthorityRepository;
import locnv.haui.repository.UserRepository;
import locnv.haui.security.AuthoritiesConstants;
import locnv.haui.security.SecurityUtils;
import locnv.haui.service.dto.AdminUserDTO;
import locnv.haui.service.dto.ServiceResult;
import locnv.haui.service.dto.UserDTO;
import locnv.haui.web.rest.vm.ManagedUserVM;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.security.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final AmazonClient amazonClient;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, AmazonClient amazonClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.amazonClient = amazonClient;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository
            .findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                return user;
            });
    }

    public User registerUser(AdminUserDTO userDTO, String password) {
        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new UsernameAlreadyUsedException();
                }
            });
        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setAddress(userDTO.getAddress());
        newUser.setFullName(userDTO.getFullName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl("https://locnvgraduation.s3.ap-southeast-1.amazonaws.com/150-1503945_transparent-user-png-default-user-image-png-png.png");
        newUser.setPhone(userDTO.getPhone());
        newUser.setLangKey(StringUtils.isEmpty(userDTO.getLangKey()) ? "en" : userDTO.getLangKey());
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        return true;
    }

    public User createUser(AdminUserDTO userDTO) {
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setAddress(userDTO.getAddress());
        user.setFullName(userDTO.getFullName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    public ServiceResult<AdminUserDTO> createUser1(ManagedUserVM userDTO, MultipartFile image) {
        User user = new User();
        Optional<User> userEx = userRepository.findOneByLoginIgnoreCase(userDTO.getLogin());
        if(userEx.isPresent()){
            return new ServiceResult<>(null, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Username is existed");
        }
        userEx = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if(userEx.isPresent()){
            return new ServiceResult<>(null, HttpStatus.INSUFFICIENT_STORAGE, "Email is existed");
        }
        String imageUrl = "https://locnvgraduation.s3.ap-southeast-1.amazonaws.com/150-1503945_transparent-user-png-default-user-image-png-png.png";
        if(Objects.nonNull(image)){
            imageUrl = amazonClient.uploadFile(image);
        }
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setAddress(userDTO.getAddress());
        user.setFullName(userDTO.getFullName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(imageUrl);
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        user.setPhone(userDTO.getPhone());
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }else{
            HashSet<Authority> authority = new HashSet<>();
            Authority a = new Authority();
            a.setName(AuthoritiesConstants.MANAGE);
            authority.add(a);
            user.setAuthorities(authority);
        }
        try{
            user = userRepository.save(user);
        }catch (Exception e){
            return new ServiceResult<>(null, HttpStatus.BAD_GATEWAY, "Error when save User");
        }
        log.debug("Created Information for User: {}", user);
        return new ServiceResult<>(new AdminUserDTO(user), HttpStatus.OK, "Success");
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return Optional
            .of(userRepository.findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setAddress(userDTO.getAddress());
                user.setFullName(userDTO.getFullName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(AdminUserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository
            .findOneByLogin(login)
            .ifPresent(user -> {
                userRepository.delete(user);
                log.debug("Deleted User: {}", user);
            });
    }

    public ServiceResult<?> updateStatus(AdminUserDTO adminUserDTO){
        Optional<User> u = userRepository.findOneByLoginIgnoreCase(adminUserDTO.getLogin());
        if(u.isEmpty()){
            return new ServiceResult<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "User not found");
        }
        User u1 = u.get();
        u1.setActivated(!u1.isActivated());
        try{
            userRepository.save(u1);
            return new ServiceResult<>(null, HttpStatus.OK, "OK");
        }catch (Exception e){
            return new ServiceResult<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "Loi save");
        }
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
//     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
//     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String phone, String lastName, String email, String address, String imageUrl) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setAddress(address);
                user.setPhone(phone);
                user.setFullName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setImageUrl(imageUrl);
                log.debug("Changed Information for User: {}", user);
            });
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils
            .getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
            });
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    public List<AdminUserDTO> getAllUsers(String keySearch, String role, String roleAdmin, int page, int pageSize){
        return userRepository.findAllByKey(keySearch, role, roleAdmin, page, pageSize).stream().map(AdminUserDTO::new).collect(Collectors.toList());
    }

    public int totalAllUser(String keySearch, String role, String roleAdmin){
        return userRepository.totalRecordAllUser(keySearch, role, roleAdmin);
    }
}

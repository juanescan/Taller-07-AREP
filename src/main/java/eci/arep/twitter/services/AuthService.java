package eci.arep.twitter.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final String userPoolId;
    private final String clientId;

    public AuthService(@Value("${aws.region}") String region,
                       @Value("${cognito.userPoolId}") String userPoolId,
                       @Value("${cognito.clientId}") String clientId) {

        this.userPoolId = userPoolId;
        this.clientId = clientId;

        this.cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    // Registro
    public void register(String username, String password) {
        AdminCreateUserRequest request = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .temporaryPassword(password)
                .messageAction(MessageActionType.SUPPRESS)
                .build();

        cognitoClient.adminCreateUser(request);

        AdminSetUserPasswordRequest pwRequest = AdminSetUserPasswordRequest.builder()
                .userPoolId(userPoolId)
                .username(username)
                .password(password)
                .permanent(true)
                .build();

        cognitoClient.adminSetUserPassword(pwRequest);
    }

    // Login
    public Map<String, String> login(String username, String password) {
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", username);
        authParams.put("PASSWORD", password);

        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .authParameters(authParams)
                .build();

        AdminInitiateAuthResponse response = cognitoClient.adminInitiateAuth(authRequest);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("idToken", response.authenticationResult().idToken());
        tokens.put("accessToken", response.authenticationResult().accessToken());
        tokens.put("refreshToken", response.authenticationResult().refreshToken());
        return tokens;
    }
}

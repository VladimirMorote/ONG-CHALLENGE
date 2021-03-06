package com.alkemy.ong.integration.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.alkemy.ong.config.segurity.RoleType;
import com.alkemy.ong.exception.ErrorResponse;
import com.alkemy.ong.model.entity.Role;
import com.alkemy.ong.model.entity.User;
import com.alkemy.ong.repository.IUserRepository;
import java.sql.Timestamp;
import java.time.Instant;
import org.assertj.core.util.Lists;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

public abstract class AbstractBaseIntegrationTest {

  protected static final long USER_ID = 1L;
  protected static final String FIRST_NAME = "John";
  protected static final String LAST_NAME = "Doe";
  protected static final String EMAIL = "johnny@doe.com";
  protected static final String PHOTO = "https://foo.jpg";
  protected static final String EXPECTED_MESSAGE = "Access denied. Please, try to login again or contact your admin.";

  protected TestRestTemplate restTemplate = new TestRestTemplate();
  protected HttpHeaders headers = new HttpHeaders();

  @MockBean
  protected IUserRepository userRepository;

  @MockBean
  protected AuthenticationManager authenticationManager;

  @LocalServerPort
  private int port;

  protected String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  protected void setExpiredAuthorizationHeaderBasedOn(RoleType roleType) {
    setAuthorizationHeader(SecurityTestConfig.createExpiredToken(EMAIL, roleType));
  }

  protected void setAuthorizationHeaderBasedOn(RoleType roleType) {
    setAuthorizationHeader(SecurityTestConfig.createToken(EMAIL, roleType));
  }

  protected Role stubRole(RoleType roleType) {
    Role role = new Role();
    role.setName(roleType.getFullRoleName());
    return role;
  }

  protected User stubUser(RoleType role) {
    return new User(USER_ID,
        FIRST_NAME,
        LAST_NAME,
        EMAIL,
        "123456789",
        PHOTO,
        Lists.list(stubRole(role)),
        Timestamp.from(Instant.now()),
        false);
  }

  protected String getFirstMessageError(ResponseEntity<ErrorResponse> response) {
    return response.getBody().getMessages().get(0);
  }

  protected int getStatusValue(ResponseEntity<ErrorResponse> response) {
    return response.getBody().getStatus();
  }

  protected int getAmountMessages(ResponseEntity<ErrorResponse> response) {
    return response.getBody().getMessages().size();
  }

  protected void assertOneEmptyOrNullFieldInRequest(ResponseEntity<ErrorResponse> response,
      String expectedErrorMessage) {
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(400, getStatusValue(response));
    assertEquals(1, getAmountMessages(response));
    assertEquals(expectedErrorMessage, getFirstMessageError(response));
  }

  protected void assertObjectNotFound(ResponseEntity<ErrorResponse> response,
      String expectedErrorMessage) {
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(404, getStatusValue(response));
    assertEquals(1, getAmountMessages(response));
    assertEquals(expectedErrorMessage, getFirstMessageError(response));
  }

  protected void assertCustomForbiddenResponse(ResponseEntity<ErrorResponse> response) {
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(EXPECTED_MESSAGE, getFirstMessageError(response));
  }

  private void setAuthorizationHeader(String jwtToken) {
    headers.set(HttpHeaders.AUTHORIZATION, jwtToken);
  }
}

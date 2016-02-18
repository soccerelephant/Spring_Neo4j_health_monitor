import java.nio.charset.Charset;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class Neo4jServerHealthIndicator implements HealthIndicator {

	@Value("${db.manage.server.ha.available}")
	protected String statusEndpoint;

	@Override
	public Health health() {
		try {
			HttpStatus status = checkServerStatus();

			if (status.compareTo(HttpStatus.OK) == 0) {
				return Health.up().withDetail("Neo4j is up with status 200!", HttpStatus.OK).build();
			}

			if (status.compareTo(HttpStatus.UNAUTHORIZED) == 0) {
				return Health.up().withDetail("Neo4j is up but authentication fails!", HttpStatus.UNAUTHORIZED).build();
			}

			if (status.compareTo(HttpStatus.FORBIDDEN) == 0) {
				return Health.up().withDetail("Neo4j is up but authorization fails!", HttpStatus.FORBIDDEN).build();
			}

			return Health.unknown().build();

		} catch (Exception ex) {

			return Health.down().withException(ex)
					.withDetail("Neo4j is down due to severe issue!", HttpStatus.SERVICE_UNAVAILABLE).build();
		}
	}

	private HttpStatus checkServerStatus() {

		try {
			ResponseEntity<String> response = new RestTemplate().exchange(statusEndpoint, HttpMethod.GET, buildHeader(),
					String.class);

			return response.getStatusCode();

		} catch (HttpClientErrorException exception) {
			return exception.getStatusCode();
		}
	}

	private HttpEntity<String> buildHeader() {

		HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		headers.add("Authorization", "Basic " + buildBasicAuthToken());

		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

		return entity;
	}

	private String buildBasicAuthToken() {
		return Base64Utils.encodeToString((username + ":" + password).getBytes(Charset.forName("UTF-8")));
	}
}

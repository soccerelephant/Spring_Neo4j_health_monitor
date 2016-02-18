import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;

import org.neo4j.ogm.session.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class Neo4jDatabaseHealthIndicator implements HealthIndicator {

	@Autowired
	protected Neo4jOperations operation;

	@Override
	public Health health() {
		try {
			Result result = operation.query("RETURN timestamp()", new HashMap<String, Object>());

			if (isValidTimestampReturned(result)) {
				return Health.up().withDetail("Neo4j is up!", HttpStatus.OK).build();
			} else {
				return Health.down().withDetail("Neo4j is down!", HttpStatus.SERVICE_UNAVAILABLE).build();
			}
		} catch (Exception ex) {

			return Health.down().withException(ex).withDetail("Neo4j is down!", HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
	}

	private boolean isValidTimestampReturned(Result result) {

		Long currentTimeStamp = (long) result.iterator().next().get("timestamp()");

		boolean isTimestampValid = !Objects.isNull(currentTimeStamp)
				&& currentTimeStamp.longValue() > Instant.MIN.getEpochSecond()
				&& currentTimeStamp.longValue() < Instant.MAX.getEpochSecond();

		return isTimestampValid;
	}
}

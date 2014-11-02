package dalicia.rsvp.protocol;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(NON_NULL)
public class ErrorResponse {
    public String errorCode;
    public String details;

    public ErrorResponse(String errorCode, String details) {
        this.errorCode = errorCode;
        this.details = details;
    }
}

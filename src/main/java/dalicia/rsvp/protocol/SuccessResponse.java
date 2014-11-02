package dalicia.rsvp.protocol;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(NON_NULL)
public class SuccessResponse {
    public Object result;

    public SuccessResponse(Object result) {
        this.result = result;
    }
}

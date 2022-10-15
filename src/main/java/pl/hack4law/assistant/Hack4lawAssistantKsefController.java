package pl.hack4law.assistant;

import io.micronaut.http.annotation.*;

@Controller("/hack4lawAssistantKsef")
public class Hack4lawAssistantKsefController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}

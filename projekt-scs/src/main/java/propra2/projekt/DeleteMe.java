package propra2.projekt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DeleteMe {

	@GetMapping("**") // Match all GET Requests
	public @ResponseBody String whatever() {
		return "Hallo, ich bin das Projekt-SCS";
	}

}

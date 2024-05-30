package be.kuleuven.dsgt4;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping({"/"})
    public String spa() {
        return "forward:/index.html";
    }

    @GetMapping("/_ah/warmup")
    public void warmup() {
    }
}

package com.github.taoroot.web;

import com.github.taoroot.common.security.annotation.PermitAll;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author zhiyi
 * @date 2020/10/31 19:34
 */
@PermitAll
@Controller
@ApiIgnore
public class IndexController {

    @GetMapping("/")
    public String index1() {
        return "redirect:/admin";
    }

    @GetMapping("/h5")
    public String index2() {
        return "redirect:/h5/index.html";
    }

    @GetMapping("/h5/")
    public String index3() {
        return "redirect:/h5/index.html";
    }

    @GetMapping("/admin")
    public String index4() {
        return "redirect:/admin/index.html";
    }

    @GetMapping("/admin/")
    public String index5() {
        return "redirect:/admin/index.html";
    }
}

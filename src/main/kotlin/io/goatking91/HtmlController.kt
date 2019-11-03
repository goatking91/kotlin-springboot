package io.goatking91

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.lang.Exception
import java.security.MessageDigest
import javax.servlet.http.HttpSession

@Controller
class HtmlController {

    @Autowired
    lateinit var repostiory: UserRepository

    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("title", "Home")
        return "index"
    }

    fun crypto(ss: String): String {
        val sha = MessageDigest.getInstance("SHA-256")
        val hex = sha.digest(ss.toByteArray())
        return hex.fold("", { str, it -> str + "%02x".format(it) })
    }

    @GetMapping("/{formType}")
    fun htmlForm(model: Model, @PathVariable formType: String): String {
        var response = ""
        when (formType) {
            "sign" -> response = "sign"
            "login" -> response = "login"
        }
        model.addAttribute("title", response)
        return response
    }

    @PostMapping("/sign")
    fun postSign(model: Model,
                 @RequestParam(value = "id") userId: String,
                 @RequestParam(value = "password") password: String): String {

        try {
            val cryptoPassword = crypto(password)
            val user = repostiory.save(User(userId, cryptoPassword))
            println(user.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        model.addAttribute("title", "sign success")

        return "login"
    }

    @PostMapping("login")
    fun postLogin(model: Model,
                  session: HttpSession,
                  @RequestParam(value = "id") userId: String,
                  @RequestParam(value = "password") password: String): String {

        var pageName = ""

        try {
            val cryptoPassword = crypto(password)
            val dbUser = repostiory.findByUserId(userId)

            if (dbUser != null) {
                val dbPassword = dbUser.password
                if (cryptoPassword == dbPassword) {
                    session.setAttribute("userId", dbUser.userId)
                    model.addAttribute("title", "welcome")
                    model.addAttribute("userId", userId)
                    pageName = "welcome"
                } else {
                    model.addAttribute("title", "login")
                    pageName = "login"
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pageName
    }

}
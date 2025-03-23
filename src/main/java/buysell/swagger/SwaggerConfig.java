package buysell.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "BuySell Application API",
        description = "API для приложения BuySell,"
           + " позволяющего пользователям покупать и продавать товары.",
        version = "1.0.0",
        contact = @Contact(
            name = "Nikitos",
            email = "kevranikita663@gmail.com",
            url = "https://github.com/your-github"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    )
)
public class SwaggerConfig {
}
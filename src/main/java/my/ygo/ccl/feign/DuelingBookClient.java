package my.ygo.ccl.feign;

import feign.RequestLine;

//TODO: Convert to FeignClient when moving to Spring project
public interface DuelingBookClient {

    @RequestLine("POST /php-scripts/login-user.php")
    void login();

}

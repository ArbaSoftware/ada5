<?php

class HttpRequest {
    private $url;
    private $urlItems;
    private $method;
    private $user;

    public function __construct() {
        $this->url = $_SERVER['REQUEST_URI'];
        $this->method = $_SERVER['REQUEST_METHOD'];
        $this->urlItems = explode('/', $this->url);    
        $auth = new Auth();
        $this->user = $auth->isAuthorized();
    }

    public function getUrl() {
        return $this->url;
    }

    public function getMethod() {
        return $this->method;
    }

    public function isAuthorized() {
        return !(gettype($this->user) == 'boolean' && !$this->user);
    }

    public function getUser() {
        return $this->user;
    }

    public function getUrlPart($index) {
        return $this->urlItems[$index];
    }

    public function matches($method, $pattern) {
        if ($method == $this->getMethod()) {
            $patternEls = explode('/', $pattern);
            if (sizeof($patternEls) == sizeof($this->urlItems)) {
                $matches = TRUE;
                for ($index = 0; $index < sizeof($patternEls); $index++) {
                    if ($patternEls[$index] != '*' && $this->urlItems[$index] != $patternEls[$index]) {
                        $matches = FALSE;
                        break;
                    }
                }
                return $matches;
            }
            else {
                return FALSE;
            }
        }
        else {
            return FALSE;
        }
    }

}

?>
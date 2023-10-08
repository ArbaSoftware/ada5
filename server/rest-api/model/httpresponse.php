<?php
    class HttpResponse {
        private $contentType;
        private $responseCode;
        private $content;
        private $errorMessage;

        private function __construct($responsecode, $contenttype, $content, $errormessage = NULL) {
            $this->responseCode = $responsecode;
            $this->contentType = $contenttype;
            $this->content = $content;
            $this->errorMessage = $errormessage;
        }

        public static function createErrorResponse($code, $errormessage) {
            return new HttpResponse($code,NULL, NULL,$errormessage);
        }

        public static function createResponse($code, $contenttype, $content) {
            return new HttpResponse($code, $contenttype, $content);
        }

        public function getCode() {
            return $this->responseCode;
        }

        public function expose() {
            if ($this->responseCode === 200) {
                header("HTTP/1.1 200");
                if (!is_null($this->contentType))
                    header("Content-Type: " . $this->contentType);
                if (!is_null($this->content)) {
                    if (gettype($this->content) == 'string')
                        header("Content-Length:" . strlen($this->content));
                    else
                        header("Content-Length: " . sizeof($this->content));
                    echo $this->content;
                }
            }
            else {
                header("HTTP/1.1 " . $this->responseCode . "Internal server error");
                if (!is_null($this->errorMessage))
                    echo '{"errors": ["' . $this->errorMessage . '"]}';
            }
        }
    }
?>